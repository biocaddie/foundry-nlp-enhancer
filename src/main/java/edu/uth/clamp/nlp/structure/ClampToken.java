package edu.uth.clamp.nlp.structure;

import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.uima.jcas.JCas;

public class ClampToken extends TextSection {
	public static final String ATTR_POS = "pos";
	public static final String ATTR_BIOTAG = "bioTag";  // B or I or O tag for NER sequential labeling;

	public ClampToken(JCas aJCas, BaseToken token) {
		super( aJCas, token );
		String pos = ((BaseToken)this.uimaEnt).getPartOfSpeech();
		setAttr( ATTR_POS, pos );
	}
	    
	public void setPartOfSpeech( String pos ) {
		((BaseToken)this.uimaEnt).setPartOfSpeech( pos );
    	setAttr( ATTR_POS, pos );
	}
	
    public String getPartOfSpeech() {
		return ((BaseToken)this.uimaEnt).getPartOfSpeech();
	}

    public String getBIO() {
    	return (String)getAttr( ATTR_BIOTAG );
    }
    
    public void setBIO( String bioTag ) {
    	setAttr( ATTR_BIOTAG, bioTag );
    }
}
