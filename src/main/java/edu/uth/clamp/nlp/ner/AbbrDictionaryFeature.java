package edu.uth.clamp.nlp.ner;

import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import edu.uth.clamp.nlp.util.BigIntegerDictionary;
//import edu.uth.clamp.nlp.util.DFABasedDictionary;
import edu.uth.clamp.nlp.util.BigIntegerDictionary.Span;

public class AbbrDictionaryFeature implements NERFeatureExtractor {
	 
	public static final String defaultDict = "abbr.txt";
	public static AbbrDictionaryFeature INSTANCE = new AbbrDictionaryFeature();
	
	//DFABasedDictionary dict = null;
	BigIntegerDictionary dict =null;
	public AbbrDictionaryFeature() {
		System.err.println("Loading Abbr dict!");
		InputStream dictStream = AbbrDictionaryFeature.class.getResourceAsStream( defaultDict );
		dict = new BigIntegerDictionary( dictStream );
		dict.setCaseSensitive(true);
	}
	
	public AbbrDictionaryFeature(File dictFile ) {;
		dict = new BigIntegerDictionary( dictFile );

	}

	public AbbrDictionaryFeature( String dictname) {
		InputStream dictStream = AbbrDictionaryFeature.class.getResourceAsStream( dictname);
		dict = new BigIntegerDictionary( dictStream );
	}
	
	public synchronized int extract(NERSentence sent) {
		String[] sems = new String[ sent.length()];
		String[] tokens = new String[ sent.length() ];
		
		for( int i = 0; i < sent.length(); i++ ) {
			tokens[i] = sent.getToken(i);
			sems[i] = "_O_";
		}
		for( Span span : dict.lookup( tokens ) ) {
			sems[span.start()] = NERInstance.LABELB + span.sem();
			for( int i = span.start() + 1; i < span.end(); i++ ) {
				sems[i] = NERInstance.LABELI + span.sem();
			}
		}
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i, sems );
		}
		return 0;
	}
	
	public void extract( NERSentence sent, int index, String[] sems ) {		
		sent.addFeature( index, new NERFeature( "CTD", sems[ index ] ) );	
	}
	

	
}
