package edu.uth.clamp.nlp.ner;

import java.io.File;
import java.util.Vector;

public abstract class NameEntityMatcher {

	public abstract NESpan[] recognize( String document );
	

}
