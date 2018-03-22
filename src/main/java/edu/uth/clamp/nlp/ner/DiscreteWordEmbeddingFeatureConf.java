package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.util.ClampConstants;

public class DiscreteWordEmbeddingFeatureConf extends NERFeatureConf {
	FileParam wordEmbFile;
	
	public static final String defaultFile = "word_embedding_kmeans1000.txt";

	static final String name = "Discrete Word embedding file";
	static final String value = defaultFile;
	static final String defaultValue = defaultFile;
	static final String description = "Words to discret embedding data file.";
	
	public DiscreteWordEmbeddingFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERDiscreteWordEmbeddingFeature );
		wordEmbFile = new FileParam( name, defaultValue, defaultValue, description );
	}

	public NERFeatureExtractor create() {
		if( wordEmbFile.isDefault() ) {
			return DiscreteWordEmbeddingFeature.INSTANCE;
		}

		return new DiscreteWordEmbeddingFeature( wordEmbFile.value() );
	}
	

}
