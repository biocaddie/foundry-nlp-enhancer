package edu.uth.clamp.nlp.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.configurable.param.BooleanParam;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.configurable.param.IntegerParam;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.util.ClampConstants;
import edu.uth.clamp.nlp.util.StringUtil;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

public class ClampSentDetector implements SentenceDetector {
	public final static String defaultWordsFile = "defaultWords.txt";
	public final static String defaultAbbrFile = "defaultAbbrs.txt";
	public final boolean defaultBreakLong = true;
	public final int defaultMaxLen = 500;
	
    static SentenceDetector instance = null;
    static public SentenceDetector getDefault() {
    	if( instance == null ) {
    		instance = new ClampSentDetector();    		
    	}
    	return instance;
    }
	
	
	/*public FileParam wordFile;
    public FileParam abbrFile;
    public BooleanParam breakLongSent;
    public IntegerParam maxSentLen;
    public ClampSentDetector() {
		super( ClampConstants.SentDetector, ClampConstants.ClampSentDetector );
		wordFile = new FileParam( "Common English words file",
				defaultWordsFile,
				defaultWordsFile,
				"This file contains all the common English words which are used by the sent detector to decide the end of a sentence." );

		abbrFile = new FileParam( "Clinical abbreviations",
				defaultAbbrFile,
				defaultAbbrFile,
				"This file contains the abbreviations which are used by the sent detector to decide the end of a sentence." );

		breakLongSent = new BooleanParam( "Break sentence when exceeds the max length",
				"true",
				"true",
				"When set true, clamp will break the long sentence into several short sentences." );
		
		maxSentLen = new IntegerParam( "The max words of a sentence", 
				"300",
				"300",
				"The max words length of a sentence." );		
    }*/


	
	private Map<String, Integer> wordMap = null;		// all the regular english words;
    private Map<String, Integer> abbrMap = null;		// clinical abbreviations;
    boolean breakLong = true;
    int maxLen = 200;


	//@Override
	//public DocProcessor create() {
	//	// TODO Auto-generated method stub
	//	return null;
	//}
    
    public ClampSentDetector() {
    	wordMap = new HashMap<String, Integer>();
    	abbrMap = new HashMap<String, Integer>();
    	breakLong = defaultBreakLong;
    	maxLen = defaultMaxLen;
    }
    
    public ClampSentDetector( InputStream wordStream, InputStream abbrStream, boolean breakLong, int maxLen ) {
    	init( wordStream, abbrStream, breakLong, maxLen );
    }
    
	public int init( InputStream wordStream, InputStream abbrStream, boolean breakLong, int maxLen ) {
		this.wordMap = loadMapFile( wordStream );
		this.abbrMap = loadMapFile( abbrStream );
		this.breakLong = breakLong;
		this.maxLen = maxLen;
		return 0;
	}

	public String[] sentDetect( String doc ) {
		List<String> ret = new ArrayList<String>();
		for( Span span : sentPosDetect( doc ) ) {
			ret.add( getToken( doc, span ) );
		}
		return ret.toArray( new String[ ret.size() ] );
	}

	public Span[] sentPosDetect(String doc ) {
		Span[] ret1 = boundaryByNewline( doc );
		Span[] ret2 = boundaryByDot( doc, ret1 );
		//for( Span span : ret2 ) {
		//	System.out.println( doc.substring( span.getStart(), span.getEnd() ) );
		//}
		if( breakLong ) {
			return breakLongSent( doc, ret2 );
		}
		return ret2;
	}
	
	Map<String, Integer> loadMapFile( InputStream inStream ) {
        BufferedReader infile = new BufferedReader( new InputStreamReader( inStream ) );
        String line = "";
        Map<String, Integer> retMap = new HashMap<String, Integer>();
        int count=0;
        try {
            while ((line = infile.readLine()) != null)   {
                retMap.put(line.trim().toLowerCase(), count);
                count=count+1;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
        return retMap;
    }

	Span[] boundaryByNewline( String doc ) {
		NewlineSentDetector detector = new NewlineSentDetector();
		return detector.sentPosDetect( doc );
	}
	
	Span[] boundaryByDot( String doc, Span[] sentList ) {
		List<Span> ret = new ArrayList<Span>();
		for( Span span : sentList ) {
			String sent = doc.substring( span.getStart(), span.getEnd() );
			SpaceTokenizer tokenizer = new SpaceTokenizer();
			Span[] tokens = tokenizer.tokenizePos( sent );
			int sentStart = span.getStart();
			int sentEnd = span.getEnd();
			for( int i = 0; i < tokens.length; i++ ) {
	            if( i + 1 >= tokens.length ) {
	                addTrim( ret, doc, sentStart, sentEnd );
	            	break;
	            }

				// 2.1 get current token and next token;
	            String token = getToken( sent, tokens[ i ] );
	            String nextToken = getToken( sent, tokens[ i + 1 ] );
	            if( token.trim().isEmpty() ) {
	                // current token is empty string
	                continue;                
	            }
	            
	            // 2.2 if token has dot and if dot is a sentence end;
	            int tokenStart = span.getStart() + tokens[i].getStart();
	            int dotPos = getSentEndPos( token, nextToken );
	            if( dotPos < 0 ) {
	                continue;
	            }
	            addTrim( ret, doc, sentStart, tokenStart + dotPos + 1 );
	            sentStart = tokenStart + dotPos + 1;
			}
		}

		return ret.toArray( new Span[ ret.size() ] );
	}
	
	Span[] breakLongSent(String doc, Span[] sentList ) {
		List<Span> ret = new ArrayList<Span>();
		for( Span span : sentList ) {
			if( span.getEnd() - span.getStart() <= maxLen ) {
				addTrim( ret, doc, span.getStart(), span.getEnd() );
				continue;
			}
			
			int startPos = span.getStart();
    		int i = span.getStart();
    		while( i < span.getEnd() ) {
    			char c = doc.charAt(i);
    			if( StringUtil.isSpace(c) && i - startPos > maxLen ) {
    				// create a new sentence;
    				addTrim( ret, doc, startPos, i );
    				i += 1;
    				// lstrip;
    				c = doc.charAt(i);
    				while( StringUtil.isSpace( c ) ) {
    					i += 1;
    					c = doc.charAt(i);
    				}
    				startPos = i;
    			} else {
    				i += 1;
    			}
    		}
    		if( startPos != span.getEnd() ) {
    			addTrim( ret, doc, startPos, span.getEnd() );
    		}
		}
    	return ret.toArray( new Span[ ret.size() ] );	
	}
	
	private String getToken( String sent, Span span ) {
		return sent.substring( span.getStart(), span.getEnd() );
	}
	
	void addTrim(List<Span> ret, String doc, int start, int end) {
		int sentStart = start;
		int sentEnd = end;
		for( int i = start; i < end; i++ ) {
			char c = doc.charAt(i);
			if( !StringUtil.isSpace( c ) ) {
				sentStart = i;
				break;
			}
		}
		for( int i = end; i > start; i-- ) {
			char c = doc.charAt( i - 1 );
			if( StringUtil.isSpace(c) ) {
				sentEnd = i - 1;
			} else {
				break;
			}
		}
		if( sentStart < sentEnd ) {
			ret.add( new Span( sentStart, sentEnd ) );
		}
	}
	
    int getSentEndPos( String curToken, String nextToken ) {        
        int firstDotPos = curToken.indexOf( '.' );
        if( firstDotPos < 0 ) {
            // 1. token does not have dot, return -1;
            return -1;
        }
        int lastDotPos = curToken.lastIndexOf( '.' );

        if( firstDotPos != lastDotPos ) {
            // 2. current token has more than one dot;
            if( curToken.toUpperCase().equals( curToken.toLowerCase() ) ) {
                // does not contain any letters, eg. 12.365.
                //logger.trace( "getSentEndPos, curToken=[" + curToken //$NON-NLS-1$
                //        + "], nextToken=[" + nextToken //$NON-NLS-1$
                //        + "], sentEnd=[" + lastDotPos + "], rule=[multi-dot, current token is number]" ); //$NON-NLS-1$ //$NON-NLS-2$
                return lastDotPos;
            } else if ( !nextToken.isEmpty() && Character.isUpperCase( nextToken.charAt(0) ) ) {
                // next token is sentence start, eg. About xxxx;
                //logger.trace( "getSentEndPos, curToken=[" + curToken //$NON-NLS-1$
                //        + "], nextToken=[" + nextToken //$NON-NLS-1$
                //        + "], sentEnd=[" + lastDotPos + "], rule=[multi-dot, next token is sentence start]" ); //$NON-NLS-1$ //$NON-NLS-2$
                return lastDotPos;
            } else {
                return -1;
            }
        } else {
            // 3. current token has only one dot;
            if( curToken.equals(".") ) { //$NON-NLS-1$
                // 3.1 token is a single dot;
                //logger.trace( "getSentEndPos, curToken=[" + curToken //$NON-NLS-1$
                 //       + "], nextToken=[" + nextToken //$NON-NLS-1$
                //        + "], sentEnd=[" + lastDotPos + "], rule=[single-dot, single dot]" ); //$NON-NLS-1$ //$NON-NLS-2$
                return lastDotPos;
            } else if ( firstDotPos == 0 ) {
                // 3.2 current token starts with dot
                return -1;
            } else if ( lastDotPos == curToken.length() - 1 ) {
                // 3.3 current token ends with dot
                String word = curToken.substring( 0, lastDotPos );
                if( word.toLowerCase().equals( "dr" ) ) {
                	// dr. XXX
                	
                	return -1;
                }
                if( !nextToken.isEmpty() && Character.isUpperCase( nextToken.charAt(0) ) ) {
                    //logger.trace( "getSentEndPos, curToken=[" + curToken //$NON-NLS-1$
                    //        + "], nextToken=[" + nextToken //$NON-NLS-1$
                    //        + "], sentEnd=[" + lastDotPos + "], rule=[single-dot, end with dot, next token is sentence start]" ); //$NON-NLS-1$ //$NON-NLS-2$
                    return lastDotPos;
                }
                if( wordMap.containsKey( word.toLowerCase() ) ) {
                    //logger.trace( "getSentEndPos, curToken=[" + curToken //$NON-NLS-1$
                    //        + "], nextToken=[" + nextToken //$NON-NLS-1$
                    //        + "], sentEnd=[" + lastDotPos + "], rule=[single-dot, end with dot, current token is an English word]" ); //$NON-NLS-1$ //$NON-NLS-2$
                    return lastDotPos;
                } else if( !abbrMap.containsKey( word.toLowerCase() ) && !abbrMap.containsKey( curToken.toLowerCase() ) ) {
                    //logger.trace( "getSentEndPos, curToken=[" + curToken //$NON-NLS-1$
                    //        + "], nextToken=[" + nextToken //$NON-NLS-1$
                   //         + "], sentEnd=[" + lastDotPos + "], rule=[single-dot, end with dot, current token is not an abbr]" ); //$NON-NLS-1$ //$NON-NLS-2$
                    return lastDotPos;
                } else {
                    // dot in current token is a part of abbreviation
                    return -1;
                }
            } else {
                // 3.4 dot is inside current token
                String rword = curToken.substring( lastDotPos + 1 );
                if( Character.isUpperCase( nextToken.charAt(0) ) && wordMap.containsKey( rword.toLowerCase() ) ) {
                    //logger.trace( "getSentEndPos, curToken=[" + curToken //$NON-NLS-1$
                    //        + "], nextToken=[" + nextToken //$NON-NLS-1$
                    //        + "], sentEnd=[" + lastDotPos + "], rule=[single-dot, dot inside token, right word is sentence start]" ); //$NON-NLS-1$ //$NON-NLS-2$
                    return lastDotPos;
                } else {
                    return -1;
                }
            }
        }
    }

    /*
	public static void main( String[] argv ) {
		String doc = "\n1\n\n2\r\n3\n\r4\n\r";
		ClampSentDetector detector = new ClampSentDetector(
				ClampSentDetector.class.getResourceAsStream( "word.txt" ),
				ClampSentDetector.class.getResourceAsStream( "abbr.txt" ),
				true,
				300
				);
		for( Span span : detector.sentPosDetect( doc ) ) {
			System.out.println( "Sent:[" + span.getStart() + " " + span.getEnd() + "], text=[" + doc.substring( span.getStart(), span.getEnd() ) + "]");
		}
		return ;
	}
*/

}