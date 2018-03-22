package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class CharGramFeatureConf extends NERFeatureConf {

	public CharGramFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERCharGramFeature );
		// TODO Auto-generated constructor stub
	}

	public NERFeatureExtractor create() {
		return CharGramFeature.INSTANCE;
	}

}
