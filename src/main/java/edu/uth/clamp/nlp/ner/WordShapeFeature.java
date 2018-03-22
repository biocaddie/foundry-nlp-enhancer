package edu.uth.clamp.nlp.ner;

import edu.uth.clamp.nlp.util.StringUtil;


public class WordShapeFeature implements NERFeatureExtractor {
	static public final WordShapeFeature INSTANCE = new WordShapeFeature();
	
	public int extract(NERSentence sent) {
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i );
		}
		return 0;
	}
	
	public synchronized int extract( NERSentence sent, int index ) {
		String token = sent.getToken( index );
		String stem = StringUtil.stem( token );
		String pos = sent.getPos( index );
		
		String lowercase= token.toLowerCase();
		String BNT= token.replaceAll("[0-9]+", "0");
		String NT= token.replaceAll("[0-9]", "0");
		
		String txt= token; 
		txt  = txt.replaceAll("[A-Z]+", "A");
		txt  = txt.replaceAll("[a-z]+", "a");
		txt  = txt.replaceAll("[0-9]+", "0");
		txt  = txt.replaceAll("[^A-Za-z0-9]+", "x");
		String BWC= txt;
		
		txt =token;
		txt  = txt.replaceAll("[A-Z]", "A");
		txt  = txt.replaceAll("[a-z]", "a");
		txt  = txt.replaceAll("[0-9]", "0");
		txt  = txt.replaceAll("[^A-Za-z0-9]", "x");
		String WC=txt;
//		String shape1 = "";
//		String shape2 = "";
//		char prevc = ' ';
//		for( char c : token.toCharArray() ) {
//			if( c >= '0' && c <= '9' ) {
//				c = '#';
//			} else if( c >= 'A' && c <= 'Z' ) {
//				c = 'A';
//			} else if( c >= 'a' && c <= 'z' ) {
//				c = 'a';
//			} else {
//				c = '_';
//			}
//			shape1 += c;
//			if( prevc != c ) {
//				shape2 += c;
//			}
//			prevc = c;
//		}
		sent.addFeature( index, new NERFeature( "OriWord", token ) );
		sent.addFeature( index, new NERFeature( "LC", lowercase ) );
		sent.addFeature( index, new NERFeature( "PartOfSp", pos ) );
		sent.addFeature( index,  new NERFeature( "StemWord", stem ) );
		sent.addFeature(index, new NERFeature( "BNT", BNT ) );
		sent.addFeature( index, new NERFeature( "NT", NT ) );		
		sent.addFeature( index, new NERFeature( "WC", WC ) );
		sent.addFeature( index, new NERFeature( "BWC", BWC ) );
		return 0;
	}
}
