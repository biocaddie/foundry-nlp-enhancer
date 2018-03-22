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

public class WordEmbeddingFeature implements NERFeatureExtractor {
	public static final String wordClusterFile = "word_embedding_kmeans1000.txt";
	public static final WordEmbeddingFeature INSTANCE = new WordEmbeddingFeature();
	Map<String, String> embeddingMap = new HashMap<String, String>();

	
	public WordEmbeddingFeature(File value) {
		try {
			loadFile( new FileInputStream( value ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public WordEmbeddingFeature() {
		loadFile( WordEmbeddingFeature.class.getResourceAsStream( wordClusterFile ) );
	}
	
	public void loadFile( InputStream instream ) {
		BufferedReader infile = new BufferedReader( new 
				InputStreamReader( instream ) );
        String line = "";
        try {
            while ((line = infile.readLine()) != null)   {
                String[] splitLine = line.trim().split( "\\s" );
                String word = splitLine[0];
                String cluster = splitLine[1];
                embeddingMap.put( word.toLowerCase(), cluster );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public synchronized int extract(NERSentence sent) {
		Vector<String> clusterVec = new Vector<String>();
		
		clusterVec.add( "E" );
		clusterVec.add( "E" );
		for( int i = 0; i < sent.length(); i++ ) {
			String token = sent.getToken(i).toLowerCase();
			String cluster = "N";
			if( embeddingMap.containsKey( token ) ) {
				cluster = embeddingMap.get( token );
			}
			clusterVec.add( cluster );
		}
		clusterVec.add( "E" );
		clusterVec.add( "E" );
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i, clusterVec );
		}
		
		
		return 0;
	}
	
	public void extract( NERSentence sent, int index, Vector<String> clusterVec ) {
		int newi = index + 2;
		sent.addFeature( index, new NERFeature( "DLFeaUNI-2", clusterVec.get( newi - 2 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaUNI-1", clusterVec.get( newi - 1 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaUNI-0", clusterVec.get( newi - 0 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaUNI+1", clusterVec.get( newi + 1 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaUNI+2", clusterVec.get( newi + 2 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaBI-2", clusterVec.get( newi - 2 ) + "+" + clusterVec.get( newi - 1 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaBI-1", clusterVec.get( newi - 1 ) + "+" + clusterVec.get( newi - 0 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaBI-0", clusterVec.get( newi - 0 ) + "+" + clusterVec.get( newi + 1 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaBI+1", clusterVec.get( newi + 1 ) + "+" + clusterVec.get( newi + 2 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaTRI-1", clusterVec.get( newi - 2 ) + "+" + clusterVec.get( newi - 1 ) + "+" + clusterVec.get( newi - 0 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaTRI-0", clusterVec.get( newi - 1 ) + "+" + clusterVec.get( newi - 0 ) + "+" + clusterVec.get( newi + 1 ) ) );
		sent.addFeature( index, new NERFeature( "DLFeaTRI+1", clusterVec.get( newi - 0 ) + "+" + clusterVec.get( newi + 1 ) + "+" + clusterVec.get( newi + 2 ) ) );		
	}
	

}
