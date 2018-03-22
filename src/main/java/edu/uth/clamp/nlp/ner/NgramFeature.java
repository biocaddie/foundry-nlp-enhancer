package edu.uth.clamp.nlp.ner;

import java.util.Vector;

import edu.uth.clamp.nlp.util.Stemmer;
import edu.uth.clamp.nlp.util.StringUtil;

public class NgramFeature implements NERFeatureExtractor {
	public static final NgramFeature INSTANCE = new NgramFeature();
	public static final int WINDOWSIZE = 5;

	public synchronized int extract(NERSentence sent) {
		Vector<String> wordVec = new Vector<String>();
		Vector<String> posVec = new Vector<String>();
		wordVec.add( "BOS" );
		wordVec.add( "BOS" );

		posVec.add( "BOS" );
		posVec.add( "BOS" );
		for( int i = 0; i < sent.length(); i++ ) {
			String token = sent.getToken(i);
			String pos = sent.getPos(i);
			String stem = StringUtil.stem( token );
			
			wordVec.add( stem );
			posVec.add( pos );
		}
		wordVec.add( "EOS" );
		wordVec.add( "EOS" );
		wordVec.add( "EOS" );

		posVec.add( "EOS" );
		posVec.add( "EOS" );
		posVec.add( "EOS" );
		
		
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i, wordVec, posVec );
		}
		return 0;
	}

	/**
	 * @param sent
	 * @param i
	 * @param wordVec
	 * @param posVec
	 */
	private void extract(NERSentence sent, int i, Vector<String> wordVec,
			Vector<String> posVec) {
		int newi = i + 2;
		sent.addFeature( i, new NERFeature( "TRIGRAM-1", wordVec.get(newi-2) + "+" + wordVec.get(newi-1) + "+" + wordVec.get(newi) ) );
		sent.addFeature( i, new NERFeature( "TRIGRAM0", wordVec.get(newi-1) + "+" + wordVec.get(newi) + "+" + wordVec.get(newi+1) ) );
		sent.addFeature( i, new NERFeature( "TRIGRAM1", wordVec.get(newi) + "+" + wordVec.get(newi+1) + "+" + wordVec.get(newi+2) ) );
		
		sent.addFeature( i, new NERFeature( "POSTRIGRAM-1", posVec.get(newi-2) + "+" + posVec.get(newi-1) + "+" + posVec.get(newi) ) );
		sent.addFeature( i, new NERFeature( "POSTRIGRAM0", posVec.get(newi-1) + "+" + posVec.get(newi) + "+" + posVec.get(newi+1) ) );
		sent.addFeature( i, new NERFeature( "POSTRIGRAM1", posVec.get(newi) + "+" + posVec.get(newi+1) + "+" + posVec.get(newi+2) ) );
		
		sent.addFeature( i, new NERFeature( "BIGRAM-2", wordVec.get( newi - 2 ) + "+" + wordVec.get( newi - 1 ) ) );
		sent.addFeature( i, new NERFeature( "BIGRAM-1", wordVec.get( newi - 1 ) + "+" + wordVec.get( newi - 0 ) ) );
		sent.addFeature( i, new NERFeature( "BIGRAM0", wordVec.get( newi ) + "+" + wordVec.get( newi + 1 ) ) );
		sent.addFeature( i, new NERFeature( "BIGRAM1", wordVec.get( newi + 1 ) + "+" + wordVec.get( newi + 2 ) ) );
		sent.addFeature( i, new NERFeature( "BIGRAM2", wordVec.get( newi + 2 ) + "+" + wordVec.get( newi + 3 ) ) );
		sent.addFeature( i, new NERFeature( "BIGRAM-1+1", wordVec.get( newi - 1 ) + "+" + wordVec.get( newi + 1 ) ) );

		sent.addFeature( i, new NERFeature( "POSBIGRAM-2", posVec.get( newi - 2 ) + "+" + posVec.get( newi - 1 ) ) );
		sent.addFeature( i, new NERFeature( "POSBIGRAM-1", posVec.get( newi - 1 ) + "+" + posVec.get( newi - 0 ) ) );
		sent.addFeature( i, new NERFeature( "POSBIGRAM0", posVec.get( newi ) + "+" + posVec.get( newi + 1 ) ) );
		sent.addFeature( i, new NERFeature( "POSBIGRAM1", posVec.get( newi + 1 ) + "+" + posVec.get( newi + 2 ) ) );
		sent.addFeature( i, new NERFeature( "POSBIGRAM2", posVec.get( newi + 2 ) + "+" + posVec.get( newi + 3 ) ) );
		sent.addFeature( i, new NERFeature( "POSBIGRAM-1+1", posVec.get( newi - 1 ) + "+" + posVec.get( newi + 1 ) ) );
		
		sent.addFeature( i, new NERFeature( "UNIGRAM-2", wordVec.get( newi - 2 ) ) );
		sent.addFeature( i, new NERFeature( "UNIGRAM-1", wordVec.get( newi - 1 ) ) );
		sent.addFeature( i, new NERFeature( "UNIGRAM0", wordVec.get( newi - 0 ) ) );
		sent.addFeature( i, new NERFeature( "UNIGRAM1", wordVec.get( newi + 1 ) ) );
		sent.addFeature( i, new NERFeature( "UNIGRAM2", wordVec.get( newi + 2 ) ) );

		sent.addFeature( i, new NERFeature( "POSUNIGRAM-2", posVec.get( newi - 2 ) ) );
		sent.addFeature( i, new NERFeature( "POSUNIGRAM-1", posVec.get( newi - 1 ) ) );
		sent.addFeature( i, new NERFeature( "POSUNIGRAM0", posVec.get( newi - 0 ) ) );
		sent.addFeature( i, new NERFeature( "POSUNIGRAM1", posVec.get( newi + 1 ) ) );
		sent.addFeature( i, new NERFeature( "POSUNIGRAM2", posVec.get( newi + 2 ) ) );
		
		sent.addFeature( i, new NERFeature( "W0P0", wordVec.get( newi) + "+" + posVec.get( newi ) ) );
		sent.addFeature( i, new NERFeature( "W0P-1", wordVec.get( newi) + "+" + posVec.get( newi - 1) ) );
		sent.addFeature( i, new NERFeature( "W0P-1P0", wordVec.get( newi) + "+" + posVec.get( newi - 1) + "+" + posVec.get( newi ) ) );
		sent.addFeature( i, new NERFeature( "W0P1", wordVec.get( newi) + "+" + posVec.get( newi + 1) ) );
		sent.addFeature( i, new NERFeature( "W0P0P1", wordVec.get( newi) + "+" + posVec.get( newi ) + "+" + posVec.get( newi + 1) ) );
		sent.addFeature( i, new NERFeature( "W0P-1P1", wordVec.get( newi) + "+" + posVec.get( newi - 1 ) + "+" + posVec.get( newi + 1) ) );
		sent.addFeature( i, new NERFeature( "W0P-1P1P0", wordVec.get( newi) + "+" + posVec.get( newi - 1 ) + "+" + posVec.get( newi + 1) + "+" + posVec.get( newi + 0 ) ) );
	}
}
