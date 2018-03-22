package edu.uth.clamp.nlp.attr.ast;

import opennlp.tools.util.Span;

public interface Assertion {
	
	public String extract( String sentence, Span nes );
	
}
