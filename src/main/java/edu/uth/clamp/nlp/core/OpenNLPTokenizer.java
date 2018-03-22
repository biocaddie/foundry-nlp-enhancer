package edu.uth.clamp.nlp.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

public class OpenNLPTokenizer implements Tokenizer {
	static Tokenizer instance = null;
	static public Tokenizer getDefault() {
		if( instance == null ) {
			instance = new OpenNLPTokenizer( OpenNLPTokenizer.class.getResourceAsStream( "en-token.bin" ) );
		}
		return instance;
	}

	TokenizerME tokenizer = null;
	public OpenNLPTokenizer() {
		tokenizer = null;
	}

	public OpenNLPTokenizer( InputStream instream ) {
		init( instream );
	}
	
	public int init( InputStream instream ) {
		try {
			TokenizerModel model = new TokenizerModel( instream );
			tokenizer = new TokenizerME( model );
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if ( instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return -1;
				}
			}
		}
		return 0;
	}

	public String[] tokenize( String sent ) {
		return tokenizer.tokenize( sent );
	}

	public Span[] tokenizePos(String sent ) {
		return tokenizer.tokenizePos( sent );
	}

	public static void main( String[] argv ) {
		InputStream instream = OpenNLPTokenizer.class.getResourceAsStream( "en-token.bin" );
		OpenNLPTokenizer tokenizer = new OpenNLPTokenizer( instream );
		String sent = " This is a   \n sentence  05-16-1982. ";
		System.out.println( "Sent=[" + sent + "]" );
		for( Span span : tokenizer.tokenizePos(sent) ) {
			System.out.println( "Span:" + span.getStart() + " " + span.getEnd() + " " + sent.substring( span.getStart(), span.getEnd() ));
		}
	}
}
