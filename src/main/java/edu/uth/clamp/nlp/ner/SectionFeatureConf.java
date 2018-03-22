package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.util.ClampConstants;

public class SectionFeatureConf extends NERFeatureConf {
	

	public SectionFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERSectionFeature );
		// TODO Auto-generated constructor stub
	}

	public NERFeatureExtractor create() {
		return SectionFeature.INSTANCE;
	}

}
