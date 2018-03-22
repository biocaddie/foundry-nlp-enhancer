package edu.uth.clamp.nlp.uima;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.core.ClampSentDetector;
import edu.uth.clamp.nlp.core.ClampTokenizer;
import edu.uth.clamp.nlp.core.DictBasedSectionHeaderIdf;
import edu.uth.clamp.nlp.core.OpenNLPPosTagger;
import edu.uth.clamp.nlp.core.SectionHeaderIdf;
import edu.uth.clamp.nlp.core.SpaceTokenizer;
import edu.uth.clamp.nlp.core.SectionHeader;
import edu.uth.clamp.nlp.structure.ClampSentence;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.structure.XmiUtil;
import edu.uth.clamp.nlp.util.StringUtil;

public class SectionHeaderIdfUIMA extends DocProcessor {
	
	SectionHeaderIdf idf = null;
	
	public SectionHeaderIdfUIMA( SectionHeaderIdf idf ) {
		this.idf = idf;
	}
	
	@Override
	public int loadResource() {
		NLPProcessorConf conf = NLPProcessorConf.createFromFile( processorConf );
    	if( conf == null ) {
    		System.out.println( "conf==[null]" );
    		return -1;
    	}
    	idf = (SectionHeaderIdf) conf.create();
		return 0;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// 1. prepare data;
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( Sentence.type ).iterator();
		List<Span[]> tokens = new ArrayList<Span[]>();

		while( iter.hasNext() ) {
			Sentence sent = (Sentence)iter.next();
			List<Span> sentToken = new ArrayList<Span>();
			for( ClampToken token : XmiUtil.selectToken(aJCas, sent.getBegin(), sent.getEnd() ) ) {
				sentToken.add( new Span( token.getBegin(), token.getEnd() ) );
			}
			tokens.add( sentToken.toArray( new Span[ sentToken.size() ] ) );
		}
		
		// 2. recognize header;
		SectionHeader[] headers = idf.idf( aJCas.getDocumentText(), tokens );

		// 3. create segments;
		Vector<SectionHeader> headerVec = new Vector<SectionHeader>();
		headerVec.add( new SectionHeader( 0, 0, "NONEHEADER" ) );
		for( SectionHeader header : headers ) {
			headerVec.add( header );
		}		
		for( int i = 1; i < headerVec.size(); i++ ) {
			int s = headerVec.get(i-1).start();
			String sec = headerVec.get(i-1).header();
			int e = headerVec.get(i).start();
			Segment segment = new Segment( aJCas );
			segment.setBegin( s );
			segment.setEnd( e );
			segment.setPreferredText( sec );
			segment.addToIndexes();
		}
		int s = headerVec.get( headerVec.size() - 1 ).start();
		int e = aJCas.getDocumentText().length();
		while( e > 0 ) {
			if( StringUtil.isSpace( aJCas.getDocumentText().charAt( e - 1 ) ) ) {
				e -= 1;
			} else {
				break;
			}
		}
		String sec = headerVec.get( headerVec.size() - 1 ).header();
		Segment segment = new Segment( aJCas );
		segment.setBegin( s );
		segment.setEnd( e );
		segment.setPreferredText( sec );
		segment.addToIndexes();

		// 4. set section header of sentences;
		iter = aJCas.getAnnotationIndex( Segment.type ).iterator();
		while( iter.hasNext() ) {
			Segment seg = ( Segment ) iter.next();
			for( ClampSentence sent : XmiUtil.selectSentence( aJCas, seg.getBegin(), seg.getEnd() ) ) {
				sent.setSectionHeader( seg.getPreferredText() );
			}
		}
	}
	
	public static void main( String[] argv ) {
		Document doc = new Document( "/Users/jwang16/git/clampnlp/data/i2b2/train_text/record-82.txt" );

		DocProcessor sentUIMA = new SentDetectorUIMA( ClampSentDetector.getDefault() );
		DocProcessor tokenUIMA = new TokenizerUIMA( ClampTokenizer.getDefault() );
		DocProcessor posTaggerUIMA = new PosTaggerUIMA( OpenNLPPosTagger.getDefault() );
		DocProcessor idfUIMA = new SectionHeaderIdfUIMA( DictBasedSectionHeaderIdf.getDefault() );
		
		try {
			sentUIMA.process( doc.getJCas() );
			tokenUIMA.process( doc.getJCas() );
			posTaggerUIMA.process( doc.getJCas() );
			idfUIMA.process( doc.getJCas() );
		} catch (AnalysisEngineProcessException e) {
			e.printStackTrace();
		}
		doc.save( "/Users/jwang16/git/clampnlp/data/i2b2/savedoc.xmi" );
	}
}
