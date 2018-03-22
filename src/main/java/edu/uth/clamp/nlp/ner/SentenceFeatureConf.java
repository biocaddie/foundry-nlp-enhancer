package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class SentenceFeatureConf extends NERFeatureConf {
	
	

	public SentenceFeatureConf() {
		super(ClampConstants.NERFeatureExtractor, ClampConstants.NERSentenceFeature );
		// TODO Auto-generated constructor stub
	}

	public NERFeatureExtractor create() {
		return SentenceFeature.INSTANCE;
	}

}
