package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.util.ClampConstants;

public class BrownClusteringFeatureConf extends NERFeatureConf {
	FileParam dictFile;
	
	static final String defaultDict = "word_path.txt";
	
	static final String name = "Brown clustering path file";
	static final String value = defaultDict;
	static final String defaultValue = defaultDict;
	static final String description = "The file contains the words to brown clustring pathes.\\n word <TAB> path";
	
	public BrownClusteringFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERBrownCluteringFeature);
		dictFile = new FileParam( name, defaultValue, defaultValue, description );
	}

	public NERFeatureExtractor create() {
		if( dictFile.isDefault() ) {
			return BrownClusteringFeature.INSTANCE;
		}

		return new BrownClusteringFeature( dictFile.value() );
	}
}
