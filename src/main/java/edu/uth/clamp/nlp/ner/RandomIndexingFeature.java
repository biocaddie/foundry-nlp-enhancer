package edu.uth.clamp.nlp.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import edu.uth.clamp.nlp.util.Stemmer;
import edu.uth.clamp.nlp.util.StringUtil;

public class RandomIndexingFeature implements NERFeatureExtractor {
	public static final String defaultFile = "random-indexing-train-dev-test-stem.thesaurus";
	public static final RandomIndexingFeature INSTANCE = new RandomIndexingFeature();
	
	Map<String, Integer> wordMap = new HashMap<String, Integer>();
	Map<String, Vector<String> > wordFeature = new HashMap<String, Vector<String> >();
	int maxLen = 0;
	
	public RandomIndexingFeature() {
		loadFile( RandomIndexingFeature.class.getResourceAsStream( defaultFile ) );
	}
	
	public RandomIndexingFeature(File value) {
		try {
			loadFile( new FileInputStream( value ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadFile( InputStream instream ) {
		BufferedReader infile = new BufferedReader( new InputStreamReader( instream ) );
        String line = "";
        try {
            while ((line = infile.readLine()) != null)   {
                if( line.trim().isEmpty() ) {
                	continue;
                }
            	String[] splitLine = line.trim().split( "\\s" );
                if( maxLen < splitLine.length - 1 ) {
                	maxLen = splitLine.length - 1;
                }            	
            	String word = StringUtil.stem( splitLine[0] ).toLowerCase();
                if( !wordMap.containsKey( word ) ) {
                	wordMap.put( word, wordMap.size() );
                }
                if( !wordFeature.containsKey( word ) ) {
                	wordFeature.put( word, new Vector<String>() );
                }
                for( int i = 1; i < splitLine.length; i++ ) {
                	String feaWord = splitLine[i].substring( 0, splitLine[i].lastIndexOf( ":" ) );
                	feaWord = StringUtil.stem( feaWord ).toLowerCase(); 
                	if( !wordMap.containsKey( feaWord ) ) {
                    	wordMap.put( feaWord, wordMap.size() );
                    }
                	wordFeature.get( word ).add( Integer.toString( wordMap.get( feaWord ) ) );
                }
            }
            infile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	


	public synchronized int extract(NERSentence sent) {
		Vector<String> defaultFea = new Vector<String>();
		for( int i = 0; i < maxLen; i++ ) {
			defaultFea.add( "RI" );
		}
		for( int i = 0; i < sent.length(); i++ ) {
			String word = StringUtil.stem( sent.getToken(i) ).toLowerCase();
			Vector<String> ret = defaultFea;
			if( wordFeature.containsKey( word ) ) {
				ret = wordFeature.get( word );
			}
			for( int j = 0; j < maxLen; j++ ) {
				if( j < ret.size() ) {
					sent.addFeature(i, new NERFeature( "RIFea" + j, ret.get(j) ) );
				} else {
					sent.addFeature(i, new NERFeature( "RIFea" + j, "RI" ) );
				}
			}
		}
		return 0;
	}
	
	

}
