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

public class BrownClusteringFeature implements NERFeatureExtractor {

	//public static final String bcList = "word_path.txt";
	public static final String bcList = "medline_2013.brownclutering.path";
	//
	public static final BrownClusteringFeature INSTANCE = new BrownClusteringFeature();
	int maxLen = 0;
	Map<String, String> bcMap = new HashMap<String, String>();
	
	public BrownClusteringFeature(File value) {
		try {
			loadFile( new FileInputStream( value ) );
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public BrownClusteringFeature() {
		loadFile( BrownClusteringFeature.class.getResourceAsStream( bcList ) );
	}
	
	public BrownClusteringFeature(String string) {
		loadFile( BrownClusteringFeature.class.getResourceAsStream( string) );
	}

	public void loadFile( InputStream instream ) {
		BufferedReader infile = new BufferedReader( new InputStreamReader( instream ) );
        String line = "";
        try {
            while ((line = infile.readLine()) != null)   {
                String[] splitLine = line.trim().split( "\t" );
                String word = splitLine[0];
                String path = splitLine[1];
                if( path.length() > maxLen ) {
                	maxLen = path.length();
                }
                bcMap.put( word.toLowerCase(), path );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	


	public synchronized int extract(NERSentence sent) {
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i );
		}
		return 0;
	}
	
	public void extract( NERSentence sent, int index ) {
		String token = sent.getToken( index ).toLowerCase();
		String path = null;
		if( bcMap.containsKey( token ) ) {
			path = bcMap.get( token );
		}
		String fea = "";
		for( int i = 0; i < maxLen; i++ ) {
			if( path == null ) {
				fea += "N";
			} else if( path.length() <= i ) {
				fea += "N";
			} else {
				fea += path.charAt(i);
			}
			sent.addFeature( index, new NERFeature( "BCFeature" + i, fea ) );
		}
	}
	
}
