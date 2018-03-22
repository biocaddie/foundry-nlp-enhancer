package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.util.ClampConstants;

public class WordEmbeddingFeatureConf extends NERFeatureConf {
	FileParam wordEmbFile;
	
	public static final String defaultFile = "word_embedding_kmeans1000.txt";

	static final String name = "Word embedding file";
	static final String value = defaultFile;
	static final String defaultValue = defaultFile;
	static final String description = "Words to embedding cluster file.";
	
	public WordEmbeddingFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERWordEmbeddingFeature );
		wordEmbFile = new FileParam( name, defaultValue, defaultValue, description );
	}

	public NERFeatureExtractor create() {
		if( wordEmbFile.isDefault() ) {
			return WordEmbeddingFeature.INSTANCE;
		}

		return new WordEmbeddingFeature( wordEmbFile.value() );
	}
	

}
