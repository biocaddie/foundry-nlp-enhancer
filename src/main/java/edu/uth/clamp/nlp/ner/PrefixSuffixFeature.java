package edu.uth.clamp.nlp.ner;

public class PrefixSuffixFeature implements NERFeatureExtractor {
	public static final PrefixSuffixFeature INSTANCE = new PrefixSuffixFeature();
	
	public PrefixSuffixFeature() {
		
	}

	public synchronized int extract(NERSentence sent) {
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i );
		}
		return 0;
	}
	
	public int extract( NERSentence sent, int index ) {
		String token = sent.getToken( index );
		
		if(token.length()>=3){
			sent.addFeature( index, new NERFeature( "Prefix1", token.substring( 0, 1 ) ) );
			sent.addFeature( index, new NERFeature( "Prefix2", token.substring( 0, 2 ) ) );
			sent.addFeature( index, new NERFeature( "Prefix3", token.substring( 0, 3 ) ) );
		}
		else if(token.length() == 2){
			sent.addFeature( index, new NERFeature( "Prefix1", token.substring( 0, 1 ) ) );
			sent.addFeature( index, new NERFeature( "Prefix2", token.substring( 0, 2 ) ) );
			sent.addFeature( index, new NERFeature( "Prefix3", "_NIL_" ) );
		}
		else if(token.length() == 1){
			sent.addFeature( index, new NERFeature( "Prefix1", token.substring( 0, 1 ) ) );
			sent.addFeature( index, new NERFeature( "Prefix2", "_NIL_" ) );
			sent.addFeature( index, new NERFeature( "Prefix3", "_NIL_" ) );
		}
		else{
			sent.addFeature( index, new NERFeature( "Prefix1", "_NIL_" ) );
			sent.addFeature( index, new NERFeature( "Prefix2", "_NIL_" ) );
			sent.addFeature( index, new NERFeature( "Prefix3", "_NIL_" ) );
		}
		
		if(token.length()>=3){
			sent.addFeature( index, new NERFeature( "Suffix3", token.substring(token.length()-3,token.length()) ) );
			sent.addFeature( index, new NERFeature( "Suffix2", token.substring(token.length()-2,token.length()) ) );
			sent.addFeature( index, new NERFeature( "Suffix1", token.substring(token.length()-1,token.length()) ) );	
		}
		else if(token.length() == 2){
			sent.addFeature( index, new NERFeature( "Suffix3", "__NIL__" ) );
			sent.addFeature( index, new NERFeature( "Suffix2", token.substring(token.length()-2,token.length()) ) );
			sent.addFeature( index, new NERFeature( "Suffix1", token.substring(token.length()-1,token.length()) ) );	
		}
		else if(token.length() == 1){
			sent.addFeature( index, new NERFeature( "Suffix3", "__NIL__" ) );
			sent.addFeature( index, new NERFeature( "Suffix2", "__NIL__" ) );
			sent.addFeature( index, new NERFeature( "Suffix1", token.substring(token.length()-1,token.length()) ) );	
		}
		else{
			sent.addFeature( index, new NERFeature( "Suffix3", "__NIL__" ) );
			sent.addFeature( index, new NERFeature( "Suffix2", "__NIL__" ) );
			sent.addFeature( index, new NERFeature( "Suffix1", "__NIL__" ) );
		}
		
		
		return 0;
	}
}
