package edu.uth.clamp.nlp.uima;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.parser.Parse;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import org.apache.ctakes.constituency.parser.util.TreeUtils;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.syntax.PunctuationToken;
import org.apache.ctakes.typesystem.type.syntax.TerminalTreebankNode;
import org.apache.ctakes.typesystem.type.syntax.TopTreebankNode;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;

import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.core.ClampSentDetector;
import edu.uth.clamp.nlp.core.ClampTokenizer;
import edu.uth.clamp.nlp.core.DictBasedSectionHeaderIdf;
import edu.uth.clamp.nlp.core.OpenNLPPosTagger;
import edu.uth.clamp.nlp.core.SectionHeaderIdf;
import edu.uth.clamp.nlp.core.SpaceTokenizer;
import edu.uth.clamp.nlp.core.SectionHeader;
import edu.uth.clamp.nlp.parser.OpenNLPConstituencyParser;
import edu.uth.clamp.nlp.structure.ClampSentence;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.structure.XmiUtil;
import edu.uth.clamp.nlp.util.StringUtil;

public class ConstituencyParserUIMA extends DocProcessor {
	
	OpenNLPConstituencyParser parser = null;
	
	public ConstituencyParserUIMA( OpenNLPConstituencyParser parser ) {
		this.parser = parser;
	}
	
	@Override
	public int loadResource() {
		NLPProcessorConf conf = NLPProcessorConf.createFromFile( processorConf );
    	if( conf == null ) {
    		System.out.println( "conf==[null]" );
    		return -1;
    	}
    	parser = (OpenNLPConstituencyParser) conf.create();
		return 0;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		FSIterator iterator = aJCas.getAnnotationIndex(Sentence.type).iterator();
		Parse parse = null;
		
		while(iterator.hasNext()){
			Sentence sentAnn = (Sentence) iterator.next();
			if(sentAnn.getCoveredText().length() == 0){
				continue;
			}
			
			String sent = sentAnn.getCoveredText();
			List<Span> tokens = new ArrayList<Span>();
			List<String> pos = new ArrayList<String>();
			for( ClampToken  token : XmiUtil.selectToken(aJCas, sentAnn.getBegin(), sentAnn.getEnd() ) ) {
				tokens.add( new Span( token.getBegin() - sentAnn.getBegin(), token.getEnd() - sentAnn.getBegin() ) );
				pos.add( token.getPartOfSpeech() );
			}

			parse = parser.parse( sent, tokens.toArray( new Span[ tokens.size() ] ), pos.toArray( new String[ pos.size() ] ) );

			Sentence newSent = null;
			if( sentAnn.getEnd() >= aJCas.getDocumentText().length() ) {
				newSent = new Sentence( aJCas, sentAnn.getBegin(), sentAnn.getEnd() );
			} else {
				newSent = new Sentence( aJCas, sentAnn.getBegin(), sentAnn.getEnd() + 1 );
			}
			TopTreebankNode top = TreeUtils.buildAlignedTree(aJCas, parse, newSent);
			top.addToIndexes();
		}
	}
	
	public static void main( String[] argv ) {
		Document doc = new Document( "/Users/jwang16/git/clampnlp/data/i2b2/train_text/record-82.txt" );

		DocProcessor sentUIMA = new SentDetectorUIMA( ClampSentDetector.getDefault() );
		DocProcessor tokenUIMA = new TokenizerUIMA( ClampTokenizer.getDefault() );
		DocProcessor posTaggerUIMA = new PosTaggerUIMA( OpenNLPPosTagger.getDefault() );
		DocProcessor parserUIMA = new ConstituencyParserUIMA( OpenNLPConstituencyParser.getDefault() );
		
		try {
			sentUIMA.process( doc.getJCas() );
			tokenUIMA.process( doc.getJCas() );
			posTaggerUIMA.process( doc.getJCas() );
			parserUIMA.process( doc.getJCas() );
		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		}
		doc.save( "/Users/jwang16/git/clampnlp/data/i2b2/savedoc.xmi" );
	}
}
