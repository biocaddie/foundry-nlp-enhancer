package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.util.ClampConstants;

public class RandomIndexingFeatureConf extends NERFeatureConf {
	public FileParam riFile;
	
	public RandomIndexingFeatureConf( ) {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERRandomIndexingFeature );
		riFile = new FileParam( name, defaultValue, defaultValue, description );
	}
	public static final String defaultFile = "random-indexing-train-dev-test-stem.thesaurus";
	static final String name = "Random indexing file";
	static final String value = defaultFile;
	static final String defaultValue = defaultFile;
	static final String description = "The results of random indexding.";

	public NERFeatureExtractor create() {
		if( riFile.isDefault() ) {
			return RandomIndexingFeature.INSTANCE;
		}

		return new RandomIndexingFeature( riFile.value() 	);
	}
	

}
