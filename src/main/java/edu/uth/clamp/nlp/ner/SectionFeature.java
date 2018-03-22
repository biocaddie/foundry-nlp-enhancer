package edu.uth.clamp.nlp.ner;

public class SectionFeature implements NERFeatureExtractor {
	public static final SectionFeature INSTANCE = new SectionFeature();

	public synchronized int extract(NERSentence sent) {
		for( int i = 0; i < sent.length(); i++ ) {
			sent.addFeature( i, new NERFeature( "Section", sent.section ) );
		}
		return 0;
	}
}
