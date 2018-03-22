package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class WordShapeFeatureConf extends NERFeatureConf {

	public WordShapeFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERWordShapeFeature );
		// TODO Auto-generated constructor stub
	}

	public NERFeatureExtractor create() {
		return WordShapeFeature.INSTANCE;
	}

}
