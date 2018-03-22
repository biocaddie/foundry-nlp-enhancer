package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class ChemicalRelatedFeatureConf extends NERFeatureConf {

	public ChemicalRelatedFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERChemicalRelatedFeature );
		// TODO Auto-generated constructor stub
	}

	public NERFeatureExtractor create() {
		return ChemicalRelatedFeature.INSTANCE;
	}

}
