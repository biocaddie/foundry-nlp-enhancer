package edu.uth.clamp.nlp.core;

import java.util.List;

import opennlp.tools.util.Span;

public interface SectionHeaderIdf {
	
	public SectionHeader[] idf( String document );
	public SectionHeader[] idf( String document, List<Span[]> tokens );

}
