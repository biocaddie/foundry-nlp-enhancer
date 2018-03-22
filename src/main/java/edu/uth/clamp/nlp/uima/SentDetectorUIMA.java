package edu.uth.clamp.nlp.uima;

import opennlp.tools.sentdetect.SentenceDetector;
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

public class SentDetectorUIMA extends DocProcessor {
	SentenceDetector sentDetector = null;
	
	public SentDetectorUIMA( SentenceDetector sentDetector ) {
		this.sentDetector = sentDetector;
	}
	
	@Override
	public int loadResource() {
		NLPProcessorConf conf = NLPProcessorConf.createFromFile( processorConf );
    	if( conf == null ) {
    		System.out.println( "conf==[null]" );
    		return -1;
    	}
    	sentDetector = (SentenceDetector) conf.create();
		return 0;
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String doc = aJCas.getDocumentText();
		int i = 0;
		for( Span span : sentDetector.sentPosDetect( doc ) ) {
			Sentence sent = new Sentence( aJCas );
			sent.setBegin( span.getStart() );
			sent.setEnd( span.getEnd() );
			sent.addToIndexes();
			sent.setSentenceNumber( i );
			i += 1;
		}
		return;
	}
}
