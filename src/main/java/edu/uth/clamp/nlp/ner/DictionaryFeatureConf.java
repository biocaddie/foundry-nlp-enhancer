package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.util.ClampConstants;

public class DictionaryFeatureConf extends NERFeatureConf {
	public FileParam dictFile;
	
	static final String defaultDict = "UMLS2015.txt";
	
	static final String name = "Dictionary file";
	static final String value = defaultDict;
	static final String defaultValue = defaultDict;
	static final String description = "The dictionary which is used for feature extraction. \\nphrase <TAB> semantic\\n";
	
	public DictionaryFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERDictionaryFeature );
		dictFile = new FileParam( name, defaultValue, defaultValue, description );
	}

	public NERFeatureExtractor create() {
		if( dictFile.isDefault() ) {
			return DictionaryFeature.INSTANCE;
		}

		return new DictionaryFeature( dictFile.value() 	);
	}
	

}
