package edu.uth.clamp.nlp.ner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SentenceFeature implements NERFeatureExtractor {
	public static final SentenceFeature INSTANCE = new SentenceFeature();

	public synchronized int extract(NERSentence sent) {
		// 1. sent length feature;
		if( sent.length() >= 6 ) {
			addFeature( sent, "SentFeaLen", "6+" );
		} else if( sent.length() >= 5 ) {
			addFeature( sent, "SentFeaLen", "3-5" );
		} else {
			addFeature( sent, "SentFeaLen", Integer.toString( sent.length() ) );
		}
		
		// 2. sent start feature;
		String reg = "^((\\w|\\d+)\\)|\\w?\\d+(\\.|\\)))";
		Pattern p = Pattern.compile( reg );
		Matcher m = p.matcher( sent.sentStr );
		if( m.matches() ) {
			addFeature( sent, "SEN_STARTWITH_ENUM", "TRUE" );
		} else {
			addFeature( sent, "SEN_STARTWITH_ENUM", "FALSE" );
		}
		
		// 3. sent end feature;
		if(sent.sentStr.trim().endsWith(":")){
	    	addFeature( sent, "COLON_END", "TRUE" );
	    } else {
	    	addFeature( sent, "COLON_END", "FALTH" );
	    }
		return 0;
	}
	
	public void addFeature( NERSentence sent, String name, String value ) {
		for( int i = 0; i < sent.length(); i++ ) {
			sent.addFeature(i, new NERFeature( name, value ) );
		}
	}

}
