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

public class DiscreteWordEmbeddingFeature implements NERFeatureExtractor {
	//public static final String wordEmbeddingFile = "word_embedding_wu_35000.txt";
	public static final String wordEmbeddingFile = "word_embedding_WordNet_wu_3500.txt";
	//public static final String wordEmbeddingFile = "finalized_proto_word_embedding_wu_3500.txt";
	public static final DiscreteWordEmbeddingFeature INSTANCE = new DiscreteWordEmbeddingFeature();
	Map<String, String> embeddingMap = new HashMap<String, String>();

	
	public DiscreteWordEmbeddingFeature(File value) {
		try {
			loadFile( new FileInputStream( value ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public DiscreteWordEmbeddingFeature() {
		loadFile( DiscreteWordEmbeddingFeature.class.getResourceAsStream( wordEmbeddingFile ) );
	}
	
	public void loadFile( InputStream instream ) {
		BufferedReader infile = new BufferedReader( new 
				InputStreamReader( instream ) );
        String line = "";
        try {
            while ((line = infile.readLine()) != null)   {
                String[] splitLine = line.trim().split( "\\t" );
                String word = splitLine[0];
                String dims = splitLine[1];
                embeddingMap.put( word, dims);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public synchronized int extract(NERSentence sent) {
		Vector<String> embVec = new Vector<String>();
		
	//	embVec.add(embeddingMap.get( "<S>" ));
		for( int i = 0; i < sent.length(); i++ ) {
			String token = sent.getToken(i).toLowerCase();
			token=token.replaceAll("\\d+", "0");
			String embedings = "";
			if( embeddingMap.containsKey( token ) ) 
			{
				embedings = embeddingMap.get( token );
			}
			else
				embedings = embeddingMap.get( "UNKNOWN" );
			
			embVec.add( embedings );
		}
	//	embVec.add(embeddingMap.get( "<S>" ));
		
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i, embVec );
		}
		
		
		return 0;
	}
	
	public void extract( NERSentence sent, int index, Vector<String> embVec ) {
		int newi = index;// + 1;
	 	
//		int newi = index+ 1;
		
		String embString= embVec.get( newi );		
		String tmp[] = embString.split("\\s");
		
//		String embString_R1= embVec.get( newi+1 );
//		String embString_L1= embVec.get( newi-1 );		
//		String tmpR1[] = embString_R1.split("\\s");
//		String tmpL1[] = embString_L1.split("\\s");
		for(int i=0;i<tmp.length;i++)
		{	
		//	sent.addFeature( index, new NERFeature( "EB_R1_"+i,tmpR1[i] ) );
			sent.addFeature( index, new NERFeature( "EB_"+i,tmp[i] ) );
		//	sent.addFeature( index, new NERFeature( "EB_L1_"+i,tmpL1[i] ) );		
		}
	}
	

}
