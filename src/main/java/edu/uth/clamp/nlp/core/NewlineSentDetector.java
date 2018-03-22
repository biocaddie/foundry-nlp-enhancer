package edu.uth.clamp.nlp.core;

import java.util.ArrayList;
import java.util.List;

import edu.uth.clamp.nlp.util.StringUtil;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.util.Span;

public class NewlineSentDetector implements SentenceDetector {
	static SentenceDetector instance = null;
	static public SentenceDetector getDefault() {
		if( instance == null ) {
			instance = new NewlineSentDetector();
		}
		return instance;
	}
	
	public static final char newlineN = '\n';
	public static final char newlineR = '\r';
	
	public String[] sentDetect( String doc ) {
		List<String> ret = new ArrayList<String>();
		for( Span span : sentPosDetect( doc ) ) {
			ret.add( getToken( doc, span ) );
		}
		return ret.toArray( new String[ ret.size() ] );
	}

	public Span[] sentPosDetect( String doc ) {
		List<Span> ret = new ArrayList<Span>();
		int start = -1;
		int end = -1;
		doc = doc + newlineN;
	    for( int i = 0; i < doc.length(); i++ ) {
	    	char c = doc.charAt(i);
	    	if( c == newlineN ) {
	    		if( start >= 0 && end >= 0 ) {
	    			addTrim( ret, doc, start, end );
	    		}
	    		start = -1;
	    		end   = -1;
	    	} else {
	    		if( start < 0 ) {
	    			start = i;
	    		}
	    		end = i + 1;
	    	}
	    }
	    if( start >= 0 ) {
	    	addTrim( ret, doc, start, end );
	    }
		return ret.toArray( new Span[ ret.size() ] );
	}

	private void addTrim(List<Span> ret, String doc, int start, int end) {
		int sentStart = start;
		int sentEnd = end;
		for( int i = start; i < end; i++ ) {
			char c = doc.charAt(i);
			if( !StringUtil.isSpace( c ) ) {
				sentStart = i;
				break;
			}
		}
		for( int i = end; i > start; i-- ) {
			char c = doc.charAt( i - 1 );
			if( StringUtil.isSpace(c) ) {
				sentEnd = i - 1;
			} else {
				break;
			}
		}
		if( sentStart < sentEnd ) {
			ret.add( new Span( sentStart, sentEnd ) );
		}
	}
	
	private String getToken( String sent, Span span ) {
		return sent.substring( span.getStart(), span.getEnd() );
	}
	
	public static void main( String[] argv ) {
		String doc = "\n1\n\n2\r\n3\n\r4\n\r";
		NewlineSentDetector detector = new NewlineSentDetector();
		for( Span span : detector.sentPosDetect( doc ) ) {
			System.out.println( "Sent:[" + span.getStart() + " " + span.getEnd() + "], text=[" + doc.substring( span.getStart(), span.getEnd() ) + "]");
		}
	}

	
}
