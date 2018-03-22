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
import org.apache.ctakes.dependency.parser.util.ClearDependencyUtility;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.syntax.TopTreebankNode;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.googlecode.clearnlp.dependency.DEPFeat;
import com.googlecode.clearnlp.dependency.DEPNode;
import com.googlecode.clearnlp.dependency.DEPTree;

import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.core.ClampSentDetector;
import edu.uth.clamp.nlp.core.ClampTokenizer;
import edu.uth.clamp.nlp.core.DictBasedSectionHeaderIdf;
import edu.uth.clamp.nlp.core.OpenNLPPosTagger;
import edu.uth.clamp.nlp.core.SectionHeaderIdf;
import edu.uth.clamp.nlp.core.SpaceTokenizer;
import edu.uth.clamp.nlp.core.SectionHeader;
import edu.uth.clamp.nlp.parser.ClearTKDependencyParser;
import edu.uth.clamp.nlp.parser.OpenNLPConstituencyParser;
import edu.uth.clamp.nlp.structure.ClampSentence;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.structure.XmiUtil;
import edu.uth.clamp.nlp.util.StringUtil;

public class DependencyParserUIMA extends DocProcessor {
	
	ClearTKDependencyParser parser = null;
	
	public DependencyParserUIMA( ClearTKDependencyParser parser ) {
		this.parser = parser;
	}
	
	@Override
	public int loadResource() {
		NLPProcessorConf conf = NLPProcessorConf.createFromFile( processorConf );
    	if( conf == null ) {
    		System.out.println( "conf==[null]" );
    		return -1;
    	}
    	parser = (ClearTKDependencyParser) conf.create();
		return 0;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		FSIterator iterator = aJCas.getAnnotationIndex(Sentence.type).iterator();		
		while(iterator.hasNext()){
			Sentence sentAnn = (Sentence) iterator.next();
			if(sentAnn.getCoveredText().length() == 0){
				continue;
			}
			
			String sent = sentAnn.getCoveredText();
			List<String> tokens = new ArrayList<String>();
			List<String> pos = new ArrayList<String>();
			List<BaseToken> baseTokens = new ArrayList<BaseToken>();
			for( ClampToken  token : XmiUtil.selectToken(aJCas, sentAnn.getBegin(), sentAnn.getEnd() ) ) {
				tokens.add( token.textStr() );
				pos.add( token.getPartOfSpeech() );
				baseTokens.add( (BaseToken)token.getUimaEnt() );
			}

			DEPTree tree = parser.parse( sent, tokens.toArray( new String[tokens.size()]), pos.toArray( new String[pos.size()] ) );
			ArrayList<ConllDependencyNode> nodes = ClearDependencyUtility.convert( aJCas, tree, sentAnn, baseTokens );
			ClearDependencyUtility.addToIndexes( aJCas, nodes );
		}
	}
	
	public static void main( String[] argv ) {
		Document doc = new Document( "/Users/jwang16/git/clampnlp/data/i2b2/train_text/record-82.txt" );

		DocProcessor sentUIMA = new SentDetectorUIMA( ClampSentDetector.getDefault() );
		DocProcessor tokenUIMA = new TokenizerUIMA( ClampTokenizer.getDefault() );
		DocProcessor posTaggerUIMA = new PosTaggerUIMA( OpenNLPPosTagger.getDefault() );
		DocProcessor depUIMA = new DependencyParserUIMA( ClearTKDependencyParser.getDefault() );
		DocProcessor consUIMA = new ConstituencyParserUIMA( OpenNLPConstituencyParser.getDefault() );

		
		try {
			sentUIMA.process( doc.getJCas() );
			tokenUIMA.process( doc.getJCas() );
			posTaggerUIMA.process( doc.getJCas() );
			depUIMA.process( doc.getJCas() );
			consUIMA.process( doc.getJCas() );
		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		}
		doc.save( "/Users/jwang16/git/clampnlp/data/i2b2/savedoc.xmi" );
	}
}
