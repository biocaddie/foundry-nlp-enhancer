package edu.uth.clamp.nlp.uima;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.structure.DocProcessor;

public class TokenizerUIMA extends DocProcessor {
	Tokenizer tokenizer = null;
	
	public TokenizerUIMA( Tokenizer tokenizer ) {
		this.tokenizer = tokenizer;
	}
	
	@Override
	public int loadResource() {
		NLPProcessorConf conf = NLPProcessorConf.createFromFile( processorConf );
    	if( conf == null ) {
    		System.out.println( "conf==[null]" );
    		return -1;
    	}
    	tokenizer = (Tokenizer) conf.create();
		return 0;
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( Sentence.type ).iterator();
		int i = 0;
		while( iter.hasNext() ) {
			Sentence sent = (Sentence)iter.next();
			String sentStr = sent.getCoveredText();
			for( Span span : tokenizer.tokenizePos( sentStr ) ) {
				BaseToken token = new BaseToken( aJCas );
				token.setBegin( sent.getBegin() + span.getStart() );
				token.setEnd( sent.getBegin() + span.getEnd() );
				token.addToIndexes();
				token.setTokenNumber(i);
				i += 1;
			}
		}
		return;
	}
}
