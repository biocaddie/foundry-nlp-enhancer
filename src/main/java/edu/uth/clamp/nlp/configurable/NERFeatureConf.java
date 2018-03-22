package edu.uth.clamp.nlp.configurable;

import java.util.Set;

import edu.uth.clamp.nlp.configurable.param.StringParam;
import edu.uth.clamp.nlp.ner.NERFeatureExtractor;

public abstract class NERFeatureConf extends ComponentConf {
	public final StringParam feaType;
	public StringParam feaName = null;
	

	public NERFeatureConf( String componentName, String feaType ) {
		super( componentName );
		this.feaType = new StringParam( "NER feature type", feaType, feaType, "This is the type of NER feature." );
		this.feaName = new StringParam( "NER feature name", feaType, feaType, "This is the name of NER feature." );
	}

	@Override
	public boolean equals( Object o ) {
		if( !( o instanceof NERFeatureConf ) ) {
			return false;
		}
		return this.getFeaName().equals( ((NERFeatureConf)o).getFeaName() )
				&& this.getFeaType().equals( ((NERFeatureConf)o).getFeaType() );
	}

	public Set<String> getRequiredCompSet() {
		return requiredComp;
	}

	public String getCompName() {
		return componentName.value();
	}

	public String getFeaType() {
		return feaType.value();
	}

	public String getFeaName() { 
		return feaName.value();
	}

	public void setFeaName( String feaName ) {
		this.feaName.setValueStr( feaName );
	}
	
	public NERFeatureExtractor create() { return null; };
}
