package edu.uth.clamp.nlp.ner;

import java.io.File;
import java.util.Vector;

public abstract class NameEntityRecognizer {	
	public abstract NESpan[] recognize( String document );
	public abstract NESpan[] recognize( String document, File featureFile );
	
	public abstract int extractFeature( Vector<NERSentence> sentences, File featureFile );
	public abstract int recognize( Vector<NERSentence> sentences, File featureFile );
	
	public abstract void addFeaExtractor( NERFeatureExtractor extractor );
	public abstract void setFeaExtractors( Vector<NERFeatureExtractor> extractors );
	
	public abstract void setModel( File modelFile );
	
}
