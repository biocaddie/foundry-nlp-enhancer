package edu.uth.clamp.nlp.ner;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.util.FileUtils;

import edu.uth.clamp.nlp.core.ClampSentDetector;
import edu.uth.clamp.nlp.core.ClampTokenizer;
import edu.uth.clamp.nlp.core.DictBasedSectionHeaderIdf;
import edu.uth.clamp.nlp.core.NewlineSentDetector;
import edu.uth.clamp.nlp.core.OpenNLPPosTagger;
import edu.uth.clamp.nlp.core.SectionHeaderIdf;
import edu.uth.clamp.nlp.core.SpaceTokenizer;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;


public class CRFNameEntityRecognizer extends NameEntityRecognizer {
	static NameEntityRecognizer instance = null;
	static public NameEntityRecognizer getDefault() {
		if( instance == null ) {
			instance = new CRFNameEntityRecognizer(
					ClampSentDetector.getDefault(),
					ClampTokenizer.getDefault(),
					OpenNLPPosTagger.getDefault(),
					DictBasedSectionHeaderIdf.getDefault() );			
			instance.addFeaExtractor( WordShapeFeature.INSTANCE );
			instance.addFeaExtractor( NgramFeature.INSTANCE );
			instance.addFeaExtractor( RegularExprFeature.INSTANCE );
			instance.addFeaExtractor( PrefixSuffixFeature.INSTANCE );
			instance.addFeaExtractor( SentenceFeature.INSTANCE );
			instance.addFeaExtractor( SectionFeature.INSTANCE );
			instance.addFeaExtractor( DictionaryFeature.INSTANCE );
			instance.addFeaExtractor( BrownClusteringFeature.INSTANCE );
			instance.addFeaExtractor( WordEmbeddingFeature.INSTANCE );
			instance.addFeaExtractor( RandomIndexingFeature.INSTANCE );
		}
		return instance;
	}
	Logger logger = Logger.getLogger( CRFNameEntityRecognizer.class.getName() );

	SentenceDetector sentDetector 				= null;
	Tokenizer tokenizer 		  				= null;
	POSTagger posTagger 		  				= null;
	SectionHeaderIdf secIdf                     = null;
	Vector<NERFeatureExtractor> feaExtractors 	= null;	
	
	File modelFile                              = null;
	
	public CRFNameEntityRecognizer() {
		feaExtractors = new Vector<NERFeatureExtractor>();
	}
	
	public CRFNameEntityRecognizer( SentenceDetector sentDetector,
			Tokenizer tokenizer, POSTagger posTagger, SectionHeaderIdf secIdf ) {
		this.sentDetector = sentDetector;
		this.tokenizer = tokenizer;
		this.posTagger = posTagger;
		this.secIdf = secIdf;
		this.feaExtractors = new Vector<NERFeatureExtractor>();
	}

	@Override
	public void setModel( File modelFile ) {
		this.modelFile = modelFile;
	}
	
	@Override
	public NESpan[] recognize( String documentStr ) {
		File featureFile = null;
		try {
			featureFile = File.createTempFile( "ClampFea" + System.currentTimeMillis(), "temp" );
			featureFile.deleteOnExit();
		} catch( IOException e ) {
        	e.printStackTrace();
        }
		return recognize( documentStr, featureFile );
	}
	
	@Override
	public NESpan[] recognize( String document, File featureFile ) {
		List<NESpan> ret = new ArrayList<NESpan>();
		Vector<NERSentence> sentences = new Vector<NERSentence>();
		Span[] sentSpans = sentDetector.sentPosDetect( document );
		for( Span span : sentDetector.sentPosDetect( document ) ) {
			String sentStr = document.substring( span.getStart(), span.getEnd() );
			Span[] tokens = tokenizer.tokenizePos( sentStr );
			String[] tokenStrs = tokenizer.tokenize( sentStr );
			String[] tags = posTagger.tag( tokenStrs );
			sentences.add( new NERSentence( sentStr, tokens, tags, null ) );			
		}
		
		recognize( sentences, featureFile );
		
		for( int i = 0; i < sentences.size(); i++ ) {
			for( NESpan span : sentences.get(i).getPredictionSpan() ) {
				ret.add( new NESpan( sentSpans[i].getStart() + span.start()
						, sentSpans[i].getStart() + span.end()
						, span.sem() ) );
			}
		}
		return ret.toArray( new NESpan[ ret.size() ] );
	}	

	@Override
	public int extractFeature(Vector<NERSentence> sentences, File featureFile) {
		try {
			FileWriter writer = new FileWriter( featureFile );
			for( NERSentence sent : sentences ) {
				sent.setExtractors(feaExtractors);
				sent.extract();
				writer.write( sent.dump() + "\n" );				
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	
	@Override
	public int recognize( Vector<NERSentence> sentences, File featureFile ) {
		// 1. write feature file;
		if( extractFeature( sentences, featureFile ) < 0 ) {
			return -1;			
		}
		
		String ret = CRFUtil.predict( featureFile.getAbsolutePath(), modelFile.getAbsolutePath() ).trim();
		
		int i = 0;
		for( String line : ret.split( "\\n\\n" ) ) {
			String[] lines = line.split( "\\n" );
			for( int j = 0; j < lines.length; j++ ) {
				sentences.get(i).setPrediction(j, lines[j] );
			}
			i += 1;
		}

		return 0;
	}
	
	@Override
	public void addFeaExtractor( NERFeatureExtractor extractor ) {
		feaExtractors.add( extractor );
	}
	
	@Override
	public void setFeaExtractors( Vector<NERFeatureExtractor> extractors ) {
		feaExtractors = extractors;
	}
	
	public static void main( String[] argv ) {
		File modelFile = new File( "/Users/jwang16/git/clampnlp/data/i2b2/train.model" );
		File feaFile = new File( "/Users/jwang16/git/clampnlp/data/i2b2/test.fea" );
		
		NameEntityRecognizer recognizer = CRFNameEntityRecognizer.getDefault();
		recognizer.setModel( modelFile );
		try {
			String text = FileUtils.file2String( new File( "/Users/jwang16/git/clampnlp/data/i2b2/test_text/0020.txt" ) , "UTF-8" );
			NESpan[] ret = recognizer.recognize( text );
			//NESpan[] ret = recognizer.recognize( text, feaFile );
			for( NESpan span : ret ) {
				System.out.println( "RET:" + span.start() + "\t" + span.end() + "\t" + span.sem() + "\t\t" + text.substring( span.start(), span.end() ) );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
