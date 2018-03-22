package edu.uth.clamp.nlp.uima;

import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.Chunker;

import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.XmiUtil;

public class NameEntityUIMA extends DocProcessor {
	public static final String CHUNKO = "O";
	public static final String CHUNKB = "B-";
	public static final String CHUNKI = "I-";
	
	Chunker chunker = null;
	
	public NameEntityUIMA( Chunker chunker ) {
		this.chunker = chunker;
	}
	
	@Override
	public int loadResource() {
		NLPProcessorConf conf = NLPProcessorConf.createFromFile( processorConf );
    	if( conf == null ) {
    		System.out.println( "conf==[null]" );
    		return -1;
    	}
    	chunker = (Chunker) conf.create();
		return 0;
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( Sentence.type ).iterator();
		// for each sentences
		while( iter.hasNext() ) {
			Sentence sent = (Sentence)iter.next();
			List<ClampToken> baseTokenList = new ArrayList<ClampToken>();
			List<String> tokenList = new ArrayList<String>();
			List<String> posList = new ArrayList<String>();
			// get tokens
			for( ClampToken token : XmiUtil.selectToken(aJCas, sent.getBegin(), sent.getEnd() ) ) {
				tokenList.add( token.textStr() );
				posList.add( token.getPartOfSpeech() );
				baseTokenList.add( token );
			}
			
			// run chunker;
			String[] tokens = tokenList.toArray( new String[ tokenList.size() ] );
			String[] tags = posList.toArray( new String[ posList.size() ] );
			String[] bios = chunker.chunk( tokens, tags );
			int i = 0;
			while( i < bios.length ) {
				String bio = bios[i];
				if( bio.equals( CHUNKO ) ) {
					Chunk chunk = new Chunk( aJCas );
					chunk.setBegin( sent.getBegin() + baseTokenList.get(i).getBegin() );
					chunk.setEnd( sent.getBegin() + baseTokenList.get(i).getEnd() );	
					chunk.setChunkType( "CHUNKO" );
					i++;
				} else if( bio.startsWith( CHUNKB ) ) {
					String type = bio.substring( CHUNKB.length() );
					int start = i;
					int end = i;
					for( int j = i + 1; j < bios.length; j++ ) {
						if( bios[j].equals( CHUNKI + type ) ) {
							end = j;
						} else {
							i = j;
							break;
						}
					}
					Chunk chunk = new Chunk( aJCas );
					chunk.setBegin( sent.getBegin() + baseTokenList.get(start).getBegin() );
					chunk.setEnd( sent.getBegin() + baseTokenList.get(end).getEnd() );	
					chunk.setChunkType( type );
				} else {
					i++;
				}
			}
		}
		return;
	}
}
