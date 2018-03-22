package edu.uth.clamp.nlp.uima;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSTagger;
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
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.XmiUtil;

public class PosTaggerUIMA extends DocProcessor {
	POSTagger posTagger = null;
	
	public PosTaggerUIMA( POSTagger posTagger ) {
		this.posTagger = posTagger;
	}
	
	@Override
	public int loadResource() {
		NLPProcessorConf conf = NLPProcessorConf.createFromFile( processorConf );
    	if( conf == null ) {
    		System.out.println( "conf==[null]" );
    		return -1;
    	}
    	posTagger = (POSTagger) conf.create();
		return 0;
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( Sentence.type ).iterator();
		while( iter.hasNext() ) {
			Sentence sent = (Sentence)iter.next();
			List<ClampToken> tokenList = new ArrayList<ClampToken>();
			List<String> tokenStrList = new ArrayList<String>();
			for( ClampToken token : XmiUtil.selectToken(aJCas, sent.getBegin(), sent.getEnd() ) ) {
				tokenStrList.add( token.textStr() );
				tokenList.add( token );
			}
			String[] pos = posTagger.tag( tokenStrList.toArray( new String[ tokenStrList.size() ] ) );
			assert( pos.length == tokenList.size() );
			for( int i = 0; i < pos.length; i++ ) {
				tokenList.get(i).setPartOfSpeech( pos[i] );
			}
		}
		return;
	}
}
