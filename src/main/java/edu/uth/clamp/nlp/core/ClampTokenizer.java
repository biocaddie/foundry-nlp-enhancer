package edu.uth.clamp.nlp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.configurable.param.BooleanParam;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.configurable.param.FolderParam;
import edu.uth.clamp.nlp.configurable.param.IntegerParam;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.RuleParam;
import edu.uth.clamp.nlp.configurable.param.StringParam;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.uima.TokenizerUIMA;
import edu.uth.clamp.nlp.util.ClampConstants;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

public class ClampTokenizer implements Tokenizer {

	public static final String defaultConfFile = "defaultTokenRule.conf";
	
	static Tokenizer instance = null;
	static public Tokenizer getDefault() {
		if( instance == null ){
			instance = new ClampTokenizer( ClampTokenizer.class.getResourceAsStream( defaultConfFile ) );
		}
		return instance;
	}
	
	/*
	public FileParam confFile;
	public ClampTokenizer() {
		super( ClampConstants.Tokenizer, ClampConstants.ClampTokenizer );
		requireComp( ClampConstants.SentDetector );
		confFile = new FileParam( "Rule file",
				defaultConfFile,
				defaultConfFile,
				"This file contains all the delimeters and rules which is used by Clamp Tokenizer." );
	}*/
	
	public ClampTokenizer() {
		SEPDELIM = "DEL";
	    delimList = new ArrayList<String>();
	    nosplitList = new ArrayList<String>();
	   	mergeRules = new Vector<String[]>();
	}

	public ClampTokenizer( InputStream instream ) {
		init( instream );
	}

	//@Override
	//public DocProcessor create() {
	//	return new TokenizerUIMA( new ClampTokenizer() );
	//}

	// resources;
	String SEPDELIM = "DEL";
    ArrayList<String> delimList;
    ArrayList<String> nosplitList;
   	Vector<String[]> mergeRules;

	public int init( InputStream instream ) {
   		Properties prop = new Properties();
   		try {
			prop.load( instream );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
        mergeRules = new Vector<String[]>();
        delimList = new ArrayList<String>();
        nosplitList = new ArrayList<String>();
        for(String rule : prop.stringPropertyNames()) {
            if(rule.equals("DELIMETER")){
                String[] delims = prop.getProperty(rule).split( SEPDELIM );
                for(String delim:delims){
                    if(!delim.equals("")) {
                    	delimList.add(delim);
                    }
                }
            }
            if(rule.equals("STRING_NO_SPLIT")){ //$NON-NLS-1$
                String nosplit = prop.getProperty(rule);
                String[] nosplits = nosplit.split( SEPDELIM );
                for(String phrase:nosplits){
                    if(!phrase.equals("")) {
						nosplitList.add(phrase);
					}
                }
            }
            if(rule.startsWith("CUSTOM_RULE")){ //$NON-NLS-1$
                String custom_rule = prop.getProperty(rule);
                mergeRules.add(custom_rule.split( SEPDELIM ));                
            }
        }
        return 0;
   	}

	public String[] tokenize( String sent ) {
		List<String> ret = new ArrayList<String>();
		for( Span span : tokenizePos( sent ) ) {
			ret.add( getToken( sent, span ) );
		}		
		return ret.toArray( new String[ ret.size() ] );
	}

	public Span[] tokenizePos( String sent ) {
		Vector<Span> splitedToken = splitToken( sent );
		Span[] mergedToken = mergeToken( sent, splitedToken );
		return mergedToken;
	}

	private Vector<Span> splitToken( String sent ) {
		Vector<Span> ret = new Vector<Span>();
		SpaceTokenizer tokenizer = new SpaceTokenizer();
		for( Span span : tokenizer.tokenizePos( sent ) ) {
			String word = getToken( sent, span );
			if( nosplitList.contains( word ) ) {
				ret.add( span );
				continue;
			}
			int offset = 0;
			for( int i = 0;i < word.length(); i++ ) {
				String ch = Character.toString( word.charAt(i) );
				//if( delimList.contains( ch ) )
				if(!ch.matches("[0-9a-zA-Z]"))
				{
					// word contains delimeters;
					if( i != offset ) {
						int tokenStart = span.getStart() + offset;
						int tokenEnd = span.getStart() + i;
						ret.add( new Span( tokenStart, tokenEnd ) );
					}
					
					int tokenStart = span.getStart() + i;
					int tokenEnd = span.getStart() + i + 1;
					ret.add( new Span( tokenStart, tokenEnd ) );
					offset = i + 1;
				}
			}
			if( offset != word.length() ) {
				int tokenStart = span.getStart() + offset;
				int tokenEnd = span.getStart() + word.length();
				ret.add( new Span( tokenStart, tokenEnd ) );
			}
		}
		
		
		Span lastSpan = ret.get( ret.size() - 1 );
		String lastToken = getToken( sent, lastSpan );
		if( lastToken.endsWith( "." ) && lastToken.length() != 1 ) {
			int pos = lastToken.lastIndexOf( "." );
			Span splitToken1 = new Span( lastSpan.getStart(), lastSpan.getStart() + pos );
			Span splitToken2 = new Span( lastSpan.getStart() + pos, lastSpan.getEnd() );
			ret.remove( lastSpan );
			if( ! ( splitToken1.getStart() == splitToken1.getEnd() ) ) {
				ret.add( splitToken1 );				
			}
			ret.add( splitToken2 );
		}
		return ret;
	}
	
	private Span[] mergeToken( String sent, Vector<Span> splitedToken ) {
		List<Span> ret = new ArrayList<Span>();
		int i = 0;
		while( i < splitedToken.size() ) {
			Span curToken = splitedToken.get( i );
            int mergeCount = ifMeetRule( sent, splitedToken, i);
            if(mergeCount > 0) {
            	i += mergeCount - 1;
            	Span endToken = splitedToken.get( i );
            	int tokenStart = curToken.getStart();
            	int tokenEnd = endToken.getEnd();
            	Span newToken = new Span(tokenStart, tokenEnd );
            	////System.out.println( newToken.textStr() );
            	ret.add( newToken );
            } else {
            	ret.add( curToken );
            }
            i += 1;
		}
        return ret.toArray( new Span[ ret.size() ] );
	}
	
	
	private int ifMeetRule(String sent, Vector<Span> token_list, int i){
        for(int j=0; j<mergeRules.size(); j++){
            String[] curRules = mergeRules.get(j);
            boolean isMeet = true;
            for(int k=0; k<curRules.length; k++){
                if(i+k<token_list.size()){
                	if( k >= 1 ) {
                		int e = token_list.get( i + k - 1 ).getEnd();
                		int s = token_list.get( i + k ).getStart();
                		if( s != e ) {
                			isMeet = false;
                			break;
                		}
                	}
                    String cur_token = getToken( sent, token_list.get(i+k) );
                    if(!cur_token.matches(curRules[k])){
                    	isMeet = false;
                        break;
                    }
                }
                else
                    return -1;
            }
            if(isMeet){
                return curRules.length;
            }
            
        }
        return -1;
    }
	
	String getToken( String sent, Span span ) {
		return sent.substring( span.getStart(), span.getEnd() );
	}
	
	public static void main( String[] argv ) {
		/*
		ClampTokenizer conf = new ClampTokenizer();
		String dump1 = conf.toString();
		System.out.println( dump1 );
		
		ClampTokenizer conf2 = new ClampTokenizer();
		System.out.println( conf2.toString() );
		*/
		File dir = new File( "/Users/jwang16/Downloads/merged_text/" );
		for( File file : dir.listFiles() ) {
			if( file.getName().startsWith(".") ) {
				continue;
			}
			
			try {
				FileWriter outfile = new FileWriter( new File( "merged_token/" + file.getName() ) );
				BufferedReader infile = new BufferedReader( new FileReader( file ) );
				String line = "";
				while( ( line = infile.readLine() ) != null ) {
					for( Span span : ClampSentDetector.getDefault().sentPosDetect( line ) ) {
						//System.out.println( line.substring( span.getStart(), span.getEnd() ) );
						String[] tokens = ClampTokenizer.getDefault().tokenize( line.substring( span.getStart(), span.getEnd() ) );
						for( String token : tokens ) {
							outfile.write( token + " " );
						}
						outfile.write( "\n" );
					}
				}
				infile.close();
				outfile.close();
			} catch ( IOException e ) {
				e.printStackTrace();				
			}
		}
		
	}


}
