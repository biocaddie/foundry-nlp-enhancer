package edu.uth.clamp.nlp.ner;

import java.io.File;
import java.io.InputStream;
import java.util.Vector;

import edu.uth.clamp.nlp.util.BigIntegerDictionary;
//import edu.uth.clamp.nlp.util.DFABasedDictionary;
import edu.uth.clamp.nlp.util.BigIntegerDictionary.Span;

public class DictionaryFeature implements NERFeatureExtractor {
	//public static final String defaultDict = "lexicon_3.txt";
	public static final String defaultDict = "UMLS2015.txt";
	public static DictionaryFeature INSTANCE = new DictionaryFeature();
	
	//DFABasedDictionary dict = null;
	BigIntegerDictionary dict =null;
	public DictionaryFeature() {
		InputStream dictStream = DictionaryFeature.class.getResourceAsStream( defaultDict );
	//	dict = new DFABasedDictionary( dictStream );
		dict = new BigIntegerDictionary( dictStream );
		
	}
	
	public DictionaryFeature(File dictFile ) {
	//	dict = new DFABasedDictionary( dictFile );
		dict = new BigIntegerDictionary( dictFile );

	}

	public DictionaryFeature( String dictname) {
		InputStream dictStream = DictionaryFeature.class.getResourceAsStream( dictname);
	//	dict = new DFABasedDictionary( dictStream );
		dict = new BigIntegerDictionary( dictStream );
	}
	
	public synchronized int extract(NERSentence sent) {
		String[] tokens = new String[ sent.length() ];
		String[] sems = new String[ sent.length() + 4 ];
		sems[0] = "BOS";
		sems[1] = "BOS";
		sems[ sems.length - 2 ] = "EOS";
		sems[ sems.length - 1 ] = "EOS";
		for( int i = 0; i < sent.length(); i++ ) {
			tokens[i] = sent.getToken(i);
			sems[i+2] = "TK";
		}
		for( Span span : dict.lookup( tokens ) ) {
			sems[span.start() + 2] = NERInstance.LABELB + span.sem();
			for( int i = span.start() + 1; i < span.end(); i++ ) {
				sems[i + 2] = NERInstance.LABELI + span.sem();
			}
		}
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i, sems );
		}
		return 0;
	}
	
	public void extract( NERSentence sent, int index, String[] sems ) {
		int newi = index + 2;
		sent.addFeature( index, new NERFeature( "DictFeaUNI-2", sems[ newi - 2 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaUNI-1", sems[ newi - 1 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaUNI-0", sems[ newi - 0 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaUNI+1", sems[ newi + 1 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaUNI+2", sems[ newi + 2 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaBI-2", sems[ newi - 2 ] + "+" + sems[ newi - 1 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaBI-1", sems[ newi - 1 ] + "+" + sems[ newi - 0 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaBI-0", sems[ newi - 0 ] + "+" + sems[ newi + 1 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaBI+1", sems[ newi + 1 ] + "+" + sems[ newi + 2 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaTRI-1", sems[ newi - 2 ] + "+" + sems[ newi - 1 ] + "+" + sems[ newi - 0 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaTRI-0", sems[ newi - 1 ] + "+" + sems[ newi - 0 ] + "+" + sems[ newi + 1 ] ) );
		sent.addFeature( index, new NERFeature( "DictFeaTRI+1", sems[ newi - 0 ] + "+" + sems[ newi + 1 ] + "+" + sems[ newi + 2 ] ) );		
	}
	

	
}
