package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NERFeatureConf;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.util.ClampConstants;

public class RegularExprFeatureConf extends NERFeatureConf {
	public FileParam regExprFile;
	public RegularExprFeatureConf() {
		super( ClampConstants.NERFeatureExtractor, ClampConstants.NERRegularExpressionFeature );
		regExprFile = new FileParam( name, defaultValue, defaultValue, description );
	}
	
	public static final String defaultFile = "reglist.txt";
	static final String name = "Regular expression file";
	static final String value = defaultFile;
	static final String defaultValue = defaultFile;
	static final String description = "All the regular expressions.";
	
	public NERFeatureExtractor create() {
		if( regExprFile.isDefault() ) {
			return RegularExprFeature.INSTANCE;
		}
		return new RegularExprFeature( regExprFile.value() 	);
	}
	

}
