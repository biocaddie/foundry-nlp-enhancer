package edu.uth.clamp.nlp.structure;

import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.uima.jcas.JCas;

public class ClampSection extends TextSection {
	public static final String ATTR_SECTION_HEADER = "sectionHeader";

	public ClampSection( JCas aJCas, Segment section ) {
		super( aJCas, section );
		String secName = ((Segment)this.uimaEnt).getPreferredText();
		setAttr( ATTR_SECTION_HEADER, secName );
	}
	
	public void setSectionName(String sectionName) {
		((Segment)this.uimaEnt).setPreferredText( sectionName );
		setAttr( ATTR_SECTION_HEADER, sectionName );
	}
	
	public String getSectionName() {
		return ((Segment)this.uimaEnt).getPreferredText();
	}

}
