package edu.uth.clamp.nlp.structure;

import java.util.Vector;

import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.jcas.JCas;

public class ClampSentence extends TextSection {
	public static final String ATTR_SECTION_HEADER = "sectionHeader";
	
	public ClampSentence( JCas aJCas, Sentence sentence ) {
		super( aJCas, sentence );
	}
	
	public Vector<ClampToken> getTokens() {
		return XmiUtil.selectToken(aJCas, this.getBegin(), this.getEnd() );
	}
	
	public void setSectionHeader(String sectionHeader) {
		((Sentence)this.uimaEnt).setSegmentId( sectionHeader );
		setAttr( ATTR_SECTION_HEADER, sectionHeader );
	}
	
	public String getSectionHeader() {
		return ((Sentence)this.uimaEnt).getSegmentId();
	}
	
}
