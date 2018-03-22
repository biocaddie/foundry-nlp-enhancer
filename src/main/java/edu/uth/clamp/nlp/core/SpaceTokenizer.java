package edu.uth.clamp.nlp.core;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;
import edu.uth.clamp.nlp.util.StringUtil;

public class SpaceTokenizer implements Tokenizer {
	static Tokenizer instance = null;
	static public Tokenizer getDefault() {
		if( instance == null ) {
			instance = new SpaceTokenizer();
		}
		return instance;
	}

	public String[] tokenize( String sent ) {
		List<String> ret = new ArrayList<String>();
		for( Span span : tokenizePos( sent ) ) {
			ret.add( sent.substring( span.getStart(), span.getEnd() ) );
		}
		return ret.toArray( new String[ ret.size() ] );
	}

	public Span[] tokenizePos( String sent ) {
		List<Span> ret = new ArrayList<Span>();
		int start = -1;
		int end = -1;
	    for( int i = 0; i < sent.length(); i++ ) {
	    	if( !StringUtil.isSpace( sent.charAt( i ) ) ) {
	    		if( start < 0 ) {
	    			start = i;
	    		}	    		
	    		end = i + 1;
	    	} else {
	    		if( start >= 0 && end >= 0 ) {
	    			ret.add( new Span( start, end ) );
	    		}
	    		start = -1;
	    		end = -1;
	    	}
	    }
	    if( start >= 0 ) {
	    	ret.add( new Span( start, end ) );
	    }
		return ret.toArray( new Span[ ret.size() ] );
	}

	public static void main( String[] argv ) {
		String sent = " This is a   \n sentence  . ";
		SpaceTokenizer tokenizer = new SpaceTokenizer();
		System.out.println( "Sent=[" + sent + "]" );
		for( Span span : tokenizer.tokenizePos(sent) ) {
			System.out.println( "Span:" + span.getStart() + " " + span.getEnd() + " " + sent.substring( span.getStart(), span.getEnd() ));
		}
	}
}
