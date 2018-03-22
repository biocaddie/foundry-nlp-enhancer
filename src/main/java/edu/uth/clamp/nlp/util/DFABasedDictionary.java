package edu.uth.clamp.nlp.util;

import edu.uth.clamp.nlp.ner.DictionaryFeature;
import org.apache.uima.util.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;
/**
 * This is a Deterministic Finite Automaton (DFA) based dictionary, 
 * which support quick max length string match; using stem;
 * @author jxu6, jwang16
 *
 */
public class DFABasedDictionary {
	public static final Logger logger = Logger.getLogger( DFABasedDictionary.class.getName() );
	public static final String FLAG = "isEnd";
	public static final String ISEND = "0";	
	public static final String ENCODING = "UTF-8";
	public static final String defaultDict = "lexicon_3.txt";
	public static final String NEWLINE = "\\n";
	public static final String SPACE = "\\s";
	public static final String TAB = "\\t";
	
	@SuppressWarnings("rawtypes")
	HashMap keysMap = new HashMap();
	int maxLen = 0;
	
	public class Span {
		int start = 0;
		int end = 0;
		String sem = "";
		public Span( int start, int end, String sem ) {
			this.start = start;
			this.end = end;
			this.sem = sem;
		}
		
		public int start() {
			return start;
		}
		public int end() {
			return end;
		}
		public String sem() {
			return sem;
		}
	}
	
	public DFABasedDictionary( String dictFile ) {
		try {
			InputStream dictStream = new FileInputStream( new File( dictFile ) );
			loadDict( dictStream );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public DFABasedDictionary( InputStream dictStream ) {
		loadDict( dictStream );
	}
	public DFABasedDictionary() {
		InputStream dictStream = DictionaryFeature.class.getResourceAsStream( defaultDict );
		loadDict( dictStream );
	}
	
	public DFABasedDictionary(File dictFile) {
		try {
			InputStream dictStream = new FileInputStream( dictFile );
			loadDict( dictStream );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addPhrase( String word, String sem ) 
	{
		word=word.trim();
		if(word.length()==0) return;
		String[] words = word.split( SPACE );
		if( maxLen < words.length ) {
    		maxLen = words.length;
    	}
		addPhrase( words, sem );
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	void addPhrase( String[] words, String sem ) {
		HashMap nowhash = null;
        nowhash = keysMap;
        for (int j = 0; j < words.length; j++) {
            String word = StringUtil.stem( words[j] ).toLowerCase();
            Object wordMap = nowhash.get(word);
            if (wordMap != null) {
                nowhash = (HashMap) wordMap;
            } else {
                HashMap<String, String> newWordHash = new HashMap<String, String>();
                newWordHash.put( FLAG, ISEND );
                nowhash.put(word, newWordHash);
                nowhash = newWordHash;
            }
            if (j == words.length - 1) {
                nowhash.put( FLAG, sem );
            }
        }
	}
	
	/*
	 *load all phrases from a dict file in free text
	 *a phrase per line 
	 */
	private void loadDict( InputStream dictStream )
	{
		
	//	int num_line =0;
		try {
			BufferedReader infile = new BufferedReader( new InputStreamReader( dictStream ) );
			String content = FileUtils.reader2String( infile );
			
	//		System.out.println(content.length());
			
			for( String line : content.split( NEWLINE ) ) {
	//			System.out.println(line);
	//			num_line=num_line+1;
				String[] splitStr = line.trim().split( TAB );
				if( splitStr.length != 2 ) {
					logger.warning( "format error in dictionary. line=[" + line + "]" );
					continue;
				}
				String[] words = splitStr[0].split( SPACE );
				String sem = splitStr[1];
		    	if( maxLen < words.length ) {
		    		maxLen = words.length;
		    	}
				addPhrase( words, sem );
			}
//			logger.info( "Dictionary Size. [" + num_line + "]" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Vector<Span> lookup( String[] tokens ) {
		Vector<Span> ret = new Vector<Span>();
		int i = 0;
		while( i < tokens.length ) {
			String word = StringUtil.stem( tokens[i] ).toLowerCase();
			if( !keysMap.containsKey( word ) ) {
				// word is not a start of dict phrase;
				i++;
				continue;
			}
			int j = i + maxLen;
			if( j > tokens.length ) {
				j = tokens.length;
			}
			while( j > i ) {
				String sem = getSem( tokens, i, j );
				if( !sem.isEmpty() && !sem.equals( ISEND ) ) {
					ret.add( new Span( i, j, sem ) );
					i = j-1;
					break;
				}
				j -= 1;
			}
			i++;
		}
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	public String getSem( String[] tokens, int begin, int end ) {
		assert( begin >= 0 );
		assert( begin < tokens.length );
		assert( end >= 0 );
		assert( end < tokens.length );
		
		String ret = "";
		HashMap nowhash = keysMap;
	    String word = "";
	    for (int i = begin; i < end; i++) {
	    	word = StringUtil.stem( tokens[i] ).toLowerCase();
	    	nowhash = (HashMap)nowhash.get( word );
	        if (nowhash == null) {
		    	//System.out.println( "word=[" + word + "], ret=[" + ret + "] " + begin + ":" + end );
		    	ret = "";
	        	return ret;
	        } else {
	        	ret = ( String ) nowhash.get( FLAG );
		    	//System.out.println( "word=[" + word + "], ret=[" + ret + "] " + begin + ":" + end );
	        }
	    }
	    if( ret.equals( ISEND ) ) {
	    	ret = "";
	    }
	    
	    return ret;
	}

	public static void main( String[] argv ) {
		DFABasedDictionary dict = new DFABasedDictionary();
		
		String ret = "Symptoms lasted about five hours and then gradually resolved except for the change in vision .";
		String[] tokens = ret.split( "\\s" );
		for( Span span : dict.lookup( tokens ) ) {
			System.out.println( span.start + "\t" + span.end + "\t" + span.sem );
		}
		return ;
	}
}




//
//public class DFABasedDictionary {
//
//	private HashMap keysMap = new HashMap();
//	private boolean MaxLengthMatch = true; //false for min length match
//	
//	public DFABasedDictionary(String dic_file_name) 
//	{
//		loadDic(dic_file_name);
//	}
//	
//	public DFABasedDictionary() 
//	{
//	
//	}
//	
//	//Match Pattern
//	public void setMaxLengthMatch()
//	{
//		this.MaxLengthMatch = true;
//	}
//	
//	//Match Pattern
//	public void setMinLengthMatch()
//	{
//		this.MaxLengthMatch = false;
//	}
//
//	//Add a phrase to the dic, one word by one word.
//	private void addPhrase(String phrs)
//	{
//		  HashMap nowhash = null;
//          nowhash = keysMap;
//          String[] tmp = phrs.split("\\s+");
//          for (int j = 0; j < tmp.length; j++) {
//              String word = tmp[j];
//         //     System.out.println(word);
//              Object wordMap = nowhash.get(word);
//              if (wordMap != null) {
//                  nowhash = (HashMap) wordMap;
//              } else {
//                  HashMap<String, String> newWordHash = new HashMap<String, String>();
//                  newWordHash.put("isEnd", "0");
//                  nowhash.put(word, newWordHash);
//                  nowhash = newWordHash;
//              }
//              if (j == tmp.length - 1) {
//                  nowhash.put("isEnd", "1");
//              }
//          }
//	}
//	
//	/*
//	 *load all phrases from a dict file in free text
//	 *a phrase per line 
//	 */
//	private void loadDic(String dic_filename)
//	{
//		File dic_file = new File(dic_filename);
//		FileInputStream dicfis;
//
//		try {
//			dicfis = new FileInputStream(dic_file);
//			byte[] data = new byte[(int) dic_file.length()];
//			dicfis.read(data);
//			String text = new String(data).trim();
//			dicfis.close();
//
//			String[] words = text.split("\\n");
//			for (String w : words) 
//			{
//				String[] tmp = w.split("\\t");
//				//System.out.println(tmp[0].trim());
//				if(!tmp[0].trim().equals("#"))
//				addPhrase(tmp[0].trim());
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//
//    /**
//     * Check whether there is a key phrase in the txt[].
//     * if there is a key phrase ,return the length of the key phrase; else return 0
//     */
//    
//	private int checkKeyWords(String[] txt, int begin, int end, boolean IsMaxMathch) {
//        HashMap nowhash = null;
//        nowhash = keysMap;
//        int maxMatchRes = 0;
//        int res = 0;
//        int l = txt.length;
//        if(end>l-1)
//        	end =l-1;
//        String word = "";
//        for (int i = begin; i <= end; i++) {
//            word = txt[i];
//            Object wordMap = nowhash.get(word);
//            if (wordMap != null) 
//            {
//            	res++;
//                nowhash = (HashMap) wordMap;
//                if (((String) nowhash.get("isEnd")).equals("1")) 
//                {
//                    if (!IsMaxMathch) {
//                        wordMap = null;
//                        nowhash = null;
//                       // txt = null;
//                        return res;
//                    } 
//                    else 
//                    {
//                        maxMatchRes = res;
//                    }
//                }
//            } 
//            else 
//            {
//               // txt = null;
//                nowhash = null;
//                return maxMatchRes;
//            }
//        }
//        //txt = null;
//        nowhash = null;
//        return maxMatchRes;
//    }
//	
//    /**
//     * Check whether there is a key phrase in the txt.
//     * if there is a key phrase ,return the length of the key phrase; else return 0
//     */
//	public int ContainKeywordsInString(String[] txt, int begin,int end)
//	{
//		return checkKeyWords(txt,begin,end,this.MaxLengthMatch);
//	}
//	
//    /**
//     * get all key phrases in the txt.
//     */
//    public List<String> getAllKeyWordsInTxt(String[] txt,int beg, int end)
//    {
//    
//    	List<String> list = new ArrayList<String>();
//    	
//        int l = txt.length;
//        int b= Math.max(beg, 0);
//        int e= Math.min(end+1, l);
//        
//        for (int i = b; i < e;) 
//        {
//            int len = checkKeyWords(txt, i,l-1, this.MaxLengthMatch);
//            if (len > 0) 
//            {
//            String inside_phrase="";
//             for(int j=i;j<i+len-1;j++)
//             {
//            	 inside_phrase= inside_phrase+txt[j] +"_";
//             }
//             inside_phrase= inside_phrase+txt[i+len-1];
//             list.add(inside_phrase);
//             i += len;
//            } 
//            else 
//            {
//                i++;
//            }
//        }
//        return list;
//    }
//  
//	/**
//	 * return the list of <start,end> pairs of the key phrases.
//	 * @param txt
//	 * @param beg
//	 * @param end
//	 * @param dic
//	 * @return
//	 */
//    public List<Pair<Integer,Integer>> getAllKeyWordPossInTxt(String[] txt,int beg, int end)
//    {    
//    	List<Pair<Integer,Integer>> list = new ArrayList<Pair<Integer,Integer>>();    	
//        int l = txt.length;
//        int b= Math.max(beg, 0);
//        int e= Math.min(end+1, l);        
//        for (int i = b; i < e;) 
//        {
//            int len =this.ContainKeywordsInString(txt, i,l-1);
//            if (len > 0) 
//            {            
//             Pair<Integer,Integer> np = new Pair<Integer,Integer>(i, i+len-1);	
//             list.add(np);
//             i += len;
//            } 
//            else 
//            {
//                i++;
//            }
//        }
//        return list;
//    }  
//    
//    /**
//     * whether the dic contains this phrase
//     * @param word
//     * @return
//     */
//    public boolean hasKeyPhrase(String phrs)
//    {    	
//    	 String[] tmp = phrs.toLowerCase().trim().split("\\s+");
//    	 int len = tmp.length;
//    	 if(len==0) return false;
//    	 
//    	 HashMap nowhash = keysMap;
//    	 String word = "";
//         for (int i = 0; i <= len; i++) {
//             word = tmp[i];
//             Object wordMap = nowhash.get(word);
//             if (wordMap != null) 
//             {    
//            	 nowhash = (HashMap) wordMap;
//                 if (((String) nowhash.get("isEnd")).equals("1")) 
//                 {
//                   return true;
//                 }
//             } 
//             else 
//             { 
//            	 return false;
//             }
//         } 
//    		return false;
//    }
//}
