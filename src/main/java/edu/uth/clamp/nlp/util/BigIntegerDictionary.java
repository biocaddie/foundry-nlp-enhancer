package edu.uth.clamp.nlp.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap; 
import java.util.List; 
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Logger;


import edu.uth.clamp.nlp.core.NewlineSentDetector;
import edu.uth.clamp.nlp.core.SpaceTokenizer;
import edu.uth.clamp.nlp.ner.DictionaryFeature;
import edu.uth.clamp.nlp.structure.ClampSentence;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.uima.SentDetectorUIMA;
import edu.uth.clamp.nlp.uima.TokenizerUIMA;

public class BigIntegerDictionary {
	static final Logger logger = Logger.getLogger( BigIntegerDictionary.class.getName() );
	static final String ENCODING = "UTF-8";
	static final String defaultDict = "lexicon_3.txt";
	static final String NEWLINE = "\\n";
	static final String SPACE = "\\s";
	static final String TAB = "\\t";
	static final int INCOMPLETE = -1;
	
	class BigInteger {
		int[] values = null;
		public BigInteger( int[] values ) {
			this.values = values;
		}
		public int length() {
			return values.length;
		}
		public int getInt( int i ) {
			assert( values != null );
			assert( i < values.length );
			return values[i];
		}
		
		@Override
		public boolean equals( Object obj ) {
			if( obj instanceof BigInteger ) {
				return false;
			}
			BigInteger another = ( BigInteger )obj;
			if( this.length() != another.length() ) {
				return false;
			}
			for( int i = 0; i < this.length(); i++ ) {
				if( this.getInt(i) != another.getInt(i) ) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public String toString() {
			String ret = "";
			for( int i = 0; i < length(); i++ ) {
				ret += values[i] + ", ";
			}
			return ret;
		}
	}
	
	class BigIntegerComparator implements Comparator<BigInteger> {
		//@Override
		public int compare(BigInteger o1, BigInteger o2) {
			if( o1.length() > o2.length() ) {
				return 1;
			} else if( o1.length() < o2.length() ) {
				return -1;
			} else {
				for( int i = 0; i < o1.length(); i++ ) {
					int k1 = o1.getInt(i);
					int k2 = o2.getInt(i);
					if( k1 > k2 ) {
						return 1;
					} else if( k1 < k2 ) {
						return -1;
					}
				}
			}
			// equal;
			return 0;
		}
		
	}

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
	
	Map<BigInteger, Integer> keywordsMap = new TreeMap<BigInteger, Integer>( new BigIntegerComparator() );
	Map<String, Integer> strIdMap = new HashMap<String, Integer>();
	Map<String, Integer> semIdMap = new HashMap<String, Integer>();
	List<String> semList = new ArrayList<String>();
	
	// options;
	int maxLen = 0;
	int windowSize = 0;						// max window size when searching;
	//private boolean doStemFlag = true;		// by default, will do the stem;
	private boolean doStemFlag = false;
	private boolean caseSensitive = false;	// by default, all to lower case;
	public void setDoStem( boolean doStem ) {
		this.doStemFlag  = doStem;
	}
	public void setCaseSensitive( boolean caseSensitive ) {
		this.caseSensitive = caseSensitive;
	}
	public void setWindowSize( int windowSize ) {
		this.windowSize = windowSize;
	}

	public BigIntegerDictionary() {
	}
	
	public BigIntegerDictionary(InputStream dictStream) {
		loadDict(dictStream);
	}
	 
	public BigIntegerDictionary(File dictStream) {
		loadDict(dictStream);
	}
	
	public int loadDict( String dictFile ) {
		try {
			InputStream dictStream = new FileInputStream( new File( dictFile ) );
			loadDict( dictStream );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keywordsMap.size();
	}
	
	public int loadDict( File dictFile ) {
		try {
			InputStream dictStream = new FileInputStream( dictFile );
			loadDict( dictStream );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keywordsMap.size();
	}
	public int loadDefaultDict() {
		InputStream dictStream = DictionaryFeature.class.getResourceAsStream( defaultDict );
		loadDict( dictStream );
		return keywordsMap.size();
	}

	public int loadDict( InputStream dictStream ) {
		try {
			BufferedReader infile = new BufferedReader( new InputStreamReader( dictStream ) );
			//String content = FileUtils.reader2String( infile );
			int count = 0;
			long start = System.currentTimeMillis();
			String line = "";
			while( ( line = infile.readLine() ) != null ) {
				String[] splitStr = line.trim().split( TAB );
				if( splitStr.length != 2 ) {
					logger.warning( "format error in dictionary. line=[" + line + "]" );
					continue;
				}
				String[] words = splitStr[0].split( SPACE );
				int[] values = new int[ words.length ];
				String sem = splitStr[1];
				int semid = insertSem( sem );
				for( int i = 0 ; i < words.length; i++ ) {
					String word = processWord( words[i] );
					values[i] = insertWord( word );
				}
				BigInteger key = new BigInteger( values );
				keywordsMap.put( key, semid );
				
				if( values.length > 2 ) {
					// for long phrase, keep the first 2 words as incomplete;
					int[] incomplete = new int[ 2 ];
					incomplete[0] = values[0];
					incomplete[1] = values[1];
					BigInteger inkey = new BigInteger( incomplete );
					if( !keywordsMap.containsKey( inkey ) ) {
						keywordsMap.put( inkey, INCOMPLETE );
					}
				}
				
		    	if( maxLen < words.length ) {
		    		maxLen = words.length;
		    	}
				count += 1;
				if( count % 100000 == 0 ) {
					System.out.println( "dict items loaded, count=[" + count + "]" );
				}
			}
			long end = System.currentTimeMillis();
			System.out.println( "load dictionary finished. ts=[" + ( end - start ) + "],"
					+ " wordCount=[" + strIdMap.size() + "], itemCount=[" + keywordsMap.size() + "]" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return keywordsMap.size();
	}
	
	
	String processWord( String word ) {
		String ret = word;
		if( !caseSensitive ) {
			ret = ret.toLowerCase();
		}
		if( doStemFlag ) {
			ret = StringUtil.stem( ret );
		}
		return ret;
	
	}
	
	int insertSem( String sem ) {
		if( semIdMap.containsKey( sem ) ) {
			return semIdMap.get( sem );
		} else {
			int id = semList.size();
			semList.add( sem );
			semIdMap.put( sem, id );
			return id;
		}
	}
	
	int insertWord( String word ) {
		if( strIdMap.containsKey( word ) ) {
			return strIdMap.get( word );
		} else {
			int id = strIdMap.size();
			strIdMap.put( word, id );
			return id;
		}
	}
	
	
	Vector<Span> getTokenGroup( int[] values ) {
		Vector<Span> ret = new Vector<Span>();
		int start = -1;
		int end = -1;
		//System.out.println( "getTokenGroup:" );
		for( int i = 0; i < values.length; i++ ) {
			//System.out.print( values[i] + ", " );
			if( values[i] >= 0 ) {
				// words in dictionary;
				if( start < 0 ) {
					start = i;
				}
				end = i;
			} else {
				// words not in dictionary;
				if( start < 0 ) {
					continue;
				} else {
					ret.add( new Span( start, end + 1, "group" ) );
					start = -1;
					end = -1;
				}
			}
		}
		if( start >= 0 && end >= 0 ) {
			ret.add( new Span( start, end + 1, "group" ) );
		}
		//System.out.println( "" );
		//for( Span span : ret ) {
		//	System.out.println( "\t" + span.start() + ", " + span.end() );
		//}
		//System.out.println( "" );
		
		return ret;
	}
	
	
	public Vector<Span> lookup( String[] tokens ) {
		Vector<Span> ret = new Vector<Span>();

		// 1. all words to id, -1 if word is not in dict;
		int[] values = new int[ tokens.length ];
		for( int i = 0; i < tokens.length; i++ ) {
			String word = processWord( tokens[i] );
			if( !strIdMap.containsKey( word ) ) {
				values[i] = -1;
			} else {
				values[i] = strIdMap.get( word );
			}
		}
		
		int window = maxLen;
		if( this.windowSize != 0 ) {
			// use user's setting;
			window = this.windowSize;
		}
		
		// 2. Continuous intervals of which all the words occur in the dictionary;
		for( Span span : getTokenGroup( values ) ) {
			int start = span.start();
			int end = span.end();
			int i = start;
			while( i < end ) {
				int j = i + window;
				if( j > end ) {
					j = end;
				}
				while( j > i ) {
					if( j > i + 2 ) {
						//System.out.println( "\ti=" + i + ", j=" + j + ", contains=" + contains( values, i, i + 2 ) );
						if( !contains( values, i, i + 2 ) ) {
							String sem = getSem( values, i, i + 1 );
							if( !sem.isEmpty() ) {
								ret.add( new Span( i, i + 1, sem ) );
							}
							i += 1;
							continue;
						}
					}
					
					String sem = getSem( values, i, j );
					if( !sem.isEmpty() ) {
						ret.add( new Span( i, j, sem ) );
						i = j - 1;
						break;
					}
					j -= 1;
				}
				i += 1;
			}
		}
		return ret;
	}
	
	boolean contains( int[] tokens, int begin, int end ) {
		int[] values = new int[ end - begin ];
		for( int i = begin; i < end; i++ ) {
			assert( tokens[i] >= 0 );
			values[i - begin] = tokens[i];
		}
		BigInteger key = new BigInteger( values );
		return keywordsMap.containsKey( key );
	}

	String getSem( int[] tokens, int begin, int end ) {
		assert( begin >= 0 );
		assert( begin < tokens.length );
		assert( end >= 0 );
		assert( end < tokens.length );
		
		int[] values = new int[ end - begin ];
		for( int i = begin; i < end; i++ ) {
			assert( tokens[i] >= 0 );
			values[i - begin] = tokens[i];
		}
		BigInteger key = new BigInteger( values );
		//System.out.println( key.toString() );
		Integer ret = keywordsMap.get(key);
		//System.out.println( "\ti=" + begin + ", j=" + end + ", ret=" + ret );
		if( ret == null || ret == INCOMPLETE ) {
			return "";
		}
		return semList.get( ret );
	}

	public static void main( String[] argv ) {
		BigIntegerDictionary dict = new BigIntegerDictionary();
		dict.setCaseSensitive( false );
		dict.setDoStem( false );
		dict.setWindowSize( 5 );
		dict.loadDefaultDict();
		
//		Document doc = new Document( "/Users/jwang16/git/local/ClampGUI/data/i2b2/test_text/0002.txt" );
//		DocProcessor sentProc = new SentDetectorUIMA( NewlineSentDetector.getDefault() );
//		DocProcessor tokenProc = new TokenizerUIMA( SpaceTokenizer.getDefault() );
//		sentProc.process( doc );
//		tokenProc.process( doc );
//		for( ClampSentence sent : doc.getSentences() ) {
//			List<String> tokens = new ArrayList<String>();
//	//		for( ClampToken token : sent.getTokens() ) {
//				tokens.add( token.textStr() );
//			}
//			System.out.println( sent.textStr() );
//			for(  Span span : dict.lookup( tokens.toArray( new String[ tokens.size() ] ) ) ) {
//				System.out.println( span.start + "\t" + span.end + "\t" + span.sem );
//			}
//			System.out.println( "\n" );
//		}
//
//		return ;
	}
}

