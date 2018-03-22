package edu.uth.clamp.nlp.ner;

import java.util.Vector;

public class NERInstance {
	public static final String LABELO = "O";
	public static final String LABELB = "B-";
	public static final String LABELI = "I-";
	public static final String SEP = "\t";
	public static final String LINEEND = "\n";
	String label;
	String prediction;
	Vector<NERFeature> features = new Vector<NERFeature>();
	
	public NERInstance() {
		this.label = LABELO;
		this.prediction = LABELO;
	}
	
	public void addFeature( NERFeature fea ) {
		features.add( fea );
	}
	
	public void setLabel( String label ) {
		assert( label.equals( LABELO ) 
				|| label.startsWith( LABELB ) 
				|| label.startsWith( LABELI ) );
		this.label = label;
	}
	public void setPrediction( String prediction ) {
		assert( prediction.equals( LABELO ) 
				|| prediction.startsWith( LABELB ) 
				|| prediction.startsWith( LABELI ) );
		this.prediction = prediction;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getPrediction() {
		return prediction;
	}
	
	public String dump() {
		String ret = label;
		int i = 0;
		for( NERFeature fea : features ) {
			//ret += SEP + i + fea.dump();
			ret += SEP + fea.dump();
			i += 1;
		}
		return ret;
	}
}
