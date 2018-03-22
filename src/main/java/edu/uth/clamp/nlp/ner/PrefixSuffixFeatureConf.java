package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class PrefixSuffixFeatureConf extends NERFeatureConf {

	public PrefixSuffixFeatureConf( ) {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERPrefixSuffixFeature );
	}

	public NERFeatureExtractor create() {
		return PrefixSuffixFeature.INSTANCE;
	}

}
