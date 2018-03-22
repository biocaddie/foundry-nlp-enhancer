package edu.uth.clamp.nlp.attr.ast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.util.Span;
import edu.uth.clamp.nlp.attr.ast.negex.GenNegEx;
import edu.uth.clamp.nlp.core.NewlineSentDetector;
import edu.uth.clamp.nlp.core.SpaceTokenizer;
import edu.uth.clamp.nlp.structure.Document;

public class NegExAssertion implements Assertion {
	static final String PRESENT = "present";
	static final String ABSENT = "absent";

	static NegExAssertion instance = null;
	static public NegExAssertion getDefault() {
		if( instance == null ) {
			instance = new NegExAssertion();
		}
		return instance;
	}
	
	GenNegEx g = new GenNegEx( true );
	
	public String[] extract( String sent, Span[] nes ) {
		List<String> ret = new ArrayList<String>();
		String sentence = cleans( sent );
	    String scope = g.negScope(sentence); 
	    if( scope.equals( "-1" ) ) {
	    	for( int i = 0; i < nes.length; i++ ) {
	    		ret.add( PRESENT );
	    	}
	    	return ret.toArray( new String[ ret.size() ] );
	    } else if( scope.equals("-2") ) {
	    	for( int i = 0; i < nes.length; i++ ) {
	    		ret.add( ABSENT );
	    	}
	    	return ret.toArray( new String[ ret.size() ] );
	    } else {
	    	for( Span span : nes ) {
	    		String keyWords = cleans( sent.substring( span.getStart(), span.getEnd() ) ); 
	    		if( contains( scope, sentence, keyWords ) ) {
	    			ret.add( ABSENT );
	    		} else {
	    			ret.add( PRESENT );
	    		}
	    	}
	    	return ret.toArray( new String[ ret.size() ] );
	    }
	}
	
	
	public String extract( String sent, Span nes ) {
		String sentence = cleans( sent );
	    String scope = g.negScope(sentence);
	    if( scope.equals( "-1" ) ) {
	    	return PRESENT;
	    } else if( scope.equals("-2") ) {
	    	return ABSENT;
	    } else {
    		String keyWords = cleans( sent.substring( nes.getStart(), nes.getEnd() ) ); 
	    	if( contains( scope, sentence, keyWords ) ) {
	    		return ABSENT;
	    	} else {
	    		return PRESENT;
	    	}
	    }
	}

	// From NegEx
    // post: removes punctuations
    private static String cleans(String line) {
    	line = line.toLowerCase();
    	if (line.contains("\""))
    	    line = line.replaceAll("\"", "");
    	if (line.contains(","))
    	    line = line.replaceAll(",", "");  
    	if (line.contains("."))
    	    line = line.replaceAll("\\.", "");
    	if (line.contains(";"))
    	    line = line.replaceAll(";", "");
    	if (line.contains(":"))
    	    line = line.replaceAll(":", "");
    	return line;
    }
    
    // From NegEx
    // post: returns true if a keyword is in the negation scope. otherwise, returns false 
    private static boolean contains(String scope, String line, String keyWords) {  
	String[] token = line.split("\\s+");  
	String[] s = keyWords.trim().split("\\s+");  
	String[] number = scope.split("\\s+");
	int counts = 0;  
	for (int i = Integer.valueOf(number[0]); i <= Integer.valueOf(number[2]); i++)
	    if (s.length == 1) {
		if (token[i].equals(s[0]))
		    return true;
	    } else 
		if ((token.length - i) >= s.length) {
		    String firstWord = token[i];
		    if (firstWord.equals(s[0])) {
			counts++;
			for (int j = 1; j < s.length; j++) { 
			    if (token[i + j].equals(s[j]))
				counts++;
			    else {
				counts = 0;
				break;
			    }
			    if (counts == s.length)
				return true;
			}
		    }
		}
	return false;
    }
	
	
	public static void main( String[] argv ) {
		Document doc = new Document("/Users/jwang16/git/clampnlp/data/i2b2/train_text/record-82.txt");
		String document = doc.getFileContent();
		
		List<Span> sentences = new ArrayList<Span>();
		List<Span[]> tokens = new ArrayList<Span[]>();
		for( Span sent : NewlineSentDetector.getDefault().sentPosDetect( document ) ) {
			sentences.add( sent );
			tokens.add( SpaceTokenizer.getDefault().tokenizePos( document.substring( sent.getStart(), sent.getEnd() ) ) );
		}
		
		
		try {
			BufferedReader infile = new BufferedReader( new FileReader( "/Users/jwang16/git/clampnlp/data/i2b2/I2b2_ast/record-82.ast" ) );
			String line;
			while( ( line = infile.readLine() ) != null ) {
				String[] splitStr = line.split( "\\|\\|" );
				String[] splitStr2 = splitStr[0].split( "\\s" );
				String[] startStr = splitStr2[ splitStr2.length - 2 ].split( ":" );
				String[] endStr = splitStr2[ splitStr2.length - 1 ].split( ":" );
				
				int sentStart = Integer.parseInt( startStr[0] );
				int tokenStart = Integer.parseInt( startStr[1] );
				int sentEnd = Integer.parseInt( endStr[0] );
				int tokenEnd = Integer.parseInt( endStr[1] );
				
				Span sentSpan = sentences.get( sentStart - 1 );
				String sent = document.substring( sentSpan.getStart(), sentSpan.getEnd() );
				Span ne = new Span( tokens.get( sentStart - 1 )[tokenStart].getStart(),
						tokens.get( sentStart - 1 )[ tokenEnd ].getEnd() );
				String ret = NegExAssertion.getDefault().extract( sent, ne );
				
				System.out.print( ret + "\t" );
				System.out.print( splitStr[ splitStr.length - 1 ] + "\t" );
				System.out.print( splitStr[0] + " " );
				
				for( int i = tokenStart; i <= tokenEnd; i++ ) {
					Span tokenSpan = tokens.get(sentStart - 1)[i];
					System.out.print( sent.substring( tokenSpan.getStart(), tokenSpan.getEnd() ) + " " );
				}
				System.out.println( "" );
				

				
			}
			infile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
