package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class NgramFeatureConf extends NERFeatureConf {
	
	public NgramFeatureConf() {
		super(ClampConstants.NERFeatureExtractor, ClampConstants.NERNgramFeature );
		// TODO Auto-generated constructor stub
	}

	public NERFeatureExtractor create() {
		return NgramFeature.INSTANCE;
	}

}
