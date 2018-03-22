package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class DiseaseRelatedFeatureConf extends NERFeatureConf {

	public DiseaseRelatedFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERDiseaseRelatedFeature );
		// TODO Auto-generated constructor stub
	}

	public NERFeatureExtractor create() {
		return DiseaseRelatedFeature.INSTANCE;
	}

}
