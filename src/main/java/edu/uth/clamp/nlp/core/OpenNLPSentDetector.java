package edu.uth.clamp.nlp.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

public class OpenNLPSentDetector implements SentenceDetector {
	static SentenceDetector instance = null;
	static public SentenceDetector getDefault() {
		if( instance == null ) {
			instance = new OpenNLPSentDetector( OpenNLPSentDetector.class.getResourceAsStream( "en-sent.bin" ) );
		}
		return instance;
	}
	
    SentenceDetectorME sentenceDetector = null;
    public OpenNLPSentDetector() {
    	sentenceDetector = null;
    }
    public OpenNLPSentDetector( InputStream instream ) {
    	init( instream );
    }

    public int init( InputStream instream ) {
		try {
			SentenceModel model = new SentenceModel(instream);
			sentenceDetector = new SentenceDetectorME(model);			
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

	public String[] sentDetect( String doc ) {
	    return sentenceDetector.sentDetect( doc );

	}

	public Span[] sentPosDetect( String doc ) {
	    return sentenceDetector.sentPosDetect( doc );
	}

	public static void main( String[] argv ) {
		OpenNLPSentDetector detector = new OpenNLPSentDetector( OpenNLPSentDetector.class.getResourceAsStream( "en-sent.bin" ) );
		String doc = "\n1\n\n2\r\n3\n\r4\n\r";
		for( Span span : detector.sentPosDetect( doc ) ) {
			System.out.println( "Sent:[" + span.getStart() + " " + span.getEnd() + "], text=[" + doc.substring( span.getStart(), span.getEnd() ) + "]");
		}
	}

}
