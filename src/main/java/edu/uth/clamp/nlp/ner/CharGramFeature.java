package edu.uth.clamp.nlp.ner;

public class CharGramFeature implements NERFeatureExtractor {
	static public final CharGramFeature INSTANCE = new CharGramFeature();
	static char startBorderChar = '<';
	static char endBorderChar = '>';
	
	public int extract(NERSentence sent) {
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i );
		}
		return 0;
	}
	
	
	public static String getLeftChar(String text, int tokenbeg) 
	{
		String ret= "";
		
		if (tokenbeg > 0) 
		{ 
			char ch =text.substring(tokenbeg - 1, tokenbeg).charAt(0);
			if (Character.isWhitespace(ch))
				return ret+"<WS>";
			else
				return ret+ch;
		} else 
		{
			return ret+"<S>";
		}
	}
	
	
	public static String getRightChar(String text, int tokenend) 
	{
		String ret= ""; 
		if (tokenend < text.length()) 
		{ 
			char ch =text.substring(tokenend , tokenend+1).charAt(0);
			if (Character.isWhitespace(ch))
				return ret+"<WS>";
			else
				return ret+ch;
		} else 
		{
			return ret+"<E>";
		}
	}
	
	
	public synchronized int extract( NERSentence sent, int index ) 
	{
		String leftchar=getLeftChar(sent.sentStr,sent.getTokenBegin(index)-sent.sent_offset );
		String rightchar=getRightChar(sent.sentStr,sent.getTokenEnd(index)-sent.sent_offset );
		sent.addFeature( index, new NERFeature( "LCHAR", leftchar));
		sent.addFeature( index, new NERFeature( "RCHAR", rightchar));
		
		String t = sent.getToken( index );
	 
	//	boolean distinguishBorders = true;
	//    if(distinguishBorders)
	    t = startBorderChar + t + endBorderChar;
		int tlen= t.length();
	    //n-gram
		for(int n=2;n<=3;n++)
		{	
			for (int k = 0; k < (tlen - n)+1; k++)
			{
				sent.addFeature( index, new NERFeature( n+"GRAM",t.substring (k, k+n)));
			}
		}
		return 0;
	}
}
