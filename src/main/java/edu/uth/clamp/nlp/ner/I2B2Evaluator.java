package edu.uth.clamp.nlp.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.uima.util.FileUtils;

import edu.uth.clamp.nlp.core.DictBasedSectionHeaderIdf;
import edu.uth.clamp.nlp.core.NewlineSentDetector;
import edu.uth.clamp.nlp.core.OpenNLPPosTagger;
import edu.uth.clamp.nlp.core.SpaceTokenizer;
import edu.uth.clamp.nlp.structure.ClampSentence;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.structure.XmiUtil;
import edu.uth.clamp.nlp.uima.PosTaggerUIMA;
import edu.uth.clamp.nlp.uima.SectionHeaderIdfUIMA;
import edu.uth.clamp.nlp.uima.SentDetectorUIMA;
import edu.uth.clamp.nlp.uima.TokenizerUIMA;

public class I2B2Evaluator {
	
	public static void main( String[] argv ) {
		
		String trainText = "data/i2b2/train_text/";
		String trainCon  = "data/i2b2/train_con/";
		String featureFile = "/Users/jwang16/git/clampnlp/data/i2b2/train.fea";
		String modelFile = "/Users/jwang16/git/clampnlp/data/i2b2/train.model";
		
		String testText = "data/i2b2/test_text/";
		String testCon = "data/i2b2/test_con/";
		String testFeature = "/Users/jwang16/git/clampnlp/data/i2b2/test.fea";
		String predictFile = "/Users/jwang16/git/clampnlp/data/i2b2/test.predict";
		
		train( trainText, trainCon, featureFile, modelFile );
		test( testText, testCon, testFeature, modelFile, predictFile );
		evaluate( testFeature, predictFile );

		
		/*
		CRFUtil.train( "/Users/jwang16/git/clampnlp/data/i2b2/crf_train.fea", 
				"/Users/jwang16/git/clampnlp/data/i2b2/crf_train.model", "-a lbfgs -p c2=0.9" );
		String ret = CRFUtil.predict( "/Users/jwang16/git/clampnlp/data/i2b2/crf_test.fea",
				"/Users/jwang16/git/clampnlp/data/i2b2/crf_train.model" );
		try {
			FileWriter predict = new FileWriter( 
					new File( "/Users/jwang16/git/clampnlp/data/i2b2/crf_test.predict" ) );
			predict.write( ret );
			predict.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		evaluate( "/Users/jwang16/git/clampnlp/data/i2b2/crf_test.fea", "/Users/jwang16/git/clampnlp/data/i2b2/crf_test.predict" );
		*/
		
	}
	
	static String extractFeature( File textFile, File conFile ) {
		String ret = "";
		Map<String, String> tokenNEMap = new HashMap<String, String>();		
		try {
			BufferedReader infile = new BufferedReader( new FileReader( conFile ) );
			String line = "";
			while( ( line = infile.readLine() ) != null ) {
				String sem = line.substring( line.lastIndexOf( "||" ) + 2 );
				sem = sem.substring( sem.indexOf( "=" ) + 1 ).replace( "\"", "" );
				
				String offset = line.substring( 0, line.lastIndexOf( "||" ) );
				offset = offset.substring( offset.lastIndexOf( "\"" ) + 2 );
				String[] splitStr = offset.split( "\\s" );
				int pos = splitStr[0].indexOf( ":" );
				int sentStart = Integer.parseInt( splitStr[0].substring( 0, pos ) );
				int tokenStart = Integer.parseInt( splitStr[0].substring( pos + 1 ) );
				pos = splitStr[1].indexOf( ":" );
				int sentEnd = Integer.parseInt( splitStr[1].substring( 0, pos ) );
				int tokenEnd = Integer.parseInt( splitStr[1].substring( pos + 1 ) );					

				String entity = line.substring( 0, line.indexOf( offset ) );
				entity = entity.substring( entity.indexOf( "=\"" ) + 2, entity.lastIndexOf( "\"" ) 	);
				
				for( int i = tokenStart; i <= tokenEnd; i++ ) {
					tokenNEMap.put( sentStart + ":" + i, "I-" + sem );
				}
				tokenNEMap.put( sentStart + ":" + tokenStart, "B-" + sem );
			}
			
			infile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Document doc = new Document( textFile );
		DocProcessor sentProc = new SentDetectorUIMA( new NewlineSentDetector() );
		DocProcessor tokenProc = new TokenizerUIMA( new SpaceTokenizer() );
		DocProcessor posProc = new PosTaggerUIMA( new OpenNLPPosTagger() );
		DocProcessor idfProc = new SectionHeaderIdfUIMA( new DictBasedSectionHeaderIdf() );
		
		
		//tokenProc.loadResource();
		sentProc.process( doc );
		tokenProc.process( doc );
		posProc.process( doc );
		idfProc.process( doc );
		//try {
		//	FileWriter writer = new FileWriter( new File( conFile.getAbsolutePath() + ".fea" ) );
			
		int sentIndex = 1;
		Vector<NERFeatureExtractor> extractors = new Vector<NERFeatureExtractor>();
		extractors.add( WordShapeFeature.INSTANCE );
		extractors.add( NgramFeature.INSTANCE );
		extractors.add( RegularExprFeature.INSTANCE );
		extractors.add( PrefixSuffixFeature.INSTANCE );
		extractors.add( SentenceFeature.INSTANCE );
		extractors.add( SectionFeature.INSTANCE );
		//extractors.add( DictionaryFeature.INSTANCE );
		//extractors.add( BrownClusteringFeature.INSTANCE );
		//extractors.add( WordEmbeddingFeature.INSTANCE );
		//extractors.add( RandomIndexingFeature.INSTANCE );
		
		
		//extractors.add( new DictionaryFeature( new File( "/Users/jwang16/git/clampnlp/src/main/resources/edu/uth/clamp/nlp/ner/problem.txt") ) );
		for( ClampSentence sent : doc.getSentences() ) {
			NERSentence nerSent = new NERSentence( sent );
			nerSent.setExtractors(extractors);

			int tokenIndex = 0;
			for( ClampToken token : XmiUtil.selectToken( doc.getJCas(), sent.getBegin(), sent.getEnd() ) ) {
				String key = sentIndex + ":" + tokenIndex;
				String label = NERInstance.LABELO;
				if( tokenNEMap.containsKey( key ) ) {
					label = tokenNEMap.get( key );
				}
				nerSent.setLabel( tokenIndex, label);
				tokenIndex += 1;
			}
			nerSent.extract();
			
			//writer.write( nerSent.dump() + "\n" );
			
			
			ret += nerSent.dump() + "\n";
			sentIndex += 1;
		}	
		//writer.close();
		//} catch( Exception e ) {
		//	e.printStackTrace();
		//}
		return ret;
	}

	
	static int train( String trainTextDir, String trainConDir, String featureFileName, String modelFileName ) {
		
		try {
			FileWriter featureFile = new FileWriter( new File( featureFileName ) );
			File dir = new File( trainTextDir );
			for( File textFile : dir.listFiles() ) {
				if( textFile.getName().startsWith( "\\." ) ) {
					continue;
				}
				System.out.println( textFile.getName() );
				File conFile = new File( trainConDir + "/" + textFile.getName().replace( ".txt", ".con" ) );
				String ret = extractFeature( textFile, conFile );
				featureFile.write( ret );
			}		
			featureFile.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		CRFUtil.train( featureFileName, modelFileName, "-a lbfgs -p c2=0.9" );
		
		return 0;
	}
	
	static int test( String testTextDir, String testConDir, 
			String featureFileName, String modelFileName, String predictFileName ) {
		try {
			FileWriter featureFile = new FileWriter( new File( featureFileName ) );
			File dir = new File( testTextDir );
			for( File textFile : dir.listFiles() ) {
				if( textFile.getName().startsWith( "\\." ) ) {
					continue;
				}
				System.out.println( textFile.getName() );
				File conFile = new File( testConDir + "/" + textFile.getName().replace( ".txt", ".con" ) );
				String ret = extractFeature( textFile, conFile );
				featureFile.write( ret );
			}		
			featureFile.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		String ret = CRFUtil.predict( featureFileName, modelFileName );

		try {
			FileWriter predict = new FileWriter( new File( predictFileName ) );
			predict.write( ret );
			predict.close();
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return 0;
	}
	
	static int evaluate( String featureFile, String predictFile ) {
		try {
			Vector<String> feature = new Vector<String>();
			BufferedReader infile = new BufferedReader( new FileReader( featureFile ) );
			String line = "";
			while( ( line = infile.readLine() ) != null ) {
				if( line.indexOf( "\t" ) >= 0 ) {
					feature.add( line.substring(0, line.indexOf( "\t" ) ) );
				} else {
					feature.add( "" );
				}
			}
			
			String[] predict = FileUtils.file2String( new File( predictFile ), "UTF-8" ).split( "\\n" );
			infile.close();
			

			Map<String, String> goldNE = new HashMap<String, String>();
			Map<String, String> predNE = new HashMap<String, String>();
			Map<String, Integer[] > performance = new HashMap<String, Integer[]>();
			
			for( int i = 0; i < predict.length; i++ ) {
				if( predict[i].startsWith( "B-" ) ) {
					String sem = predict[i].substring( 2 );
					int start = i;
					int end = i;
					for( int j = i+1; j < predict.length; j++ ) {
						if( predict[j].equals( "I-" + sem ) ) {
							end = j;
						} else {
							break;
						}
					}
					predNE.put( start + ":" + end, sem );
					if( !performance.containsKey( sem ) ) {
						performance.put( sem, new Integer[]{0,0,0} );
					}
					performance.get( sem )[0] += 1;
				}

			}
			for( int i = 0; i < feature.size(); i++ ) {
				if( feature.get(i).startsWith( "B-" ) ) {
					String sem = feature.get(i).substring( 2 );
					int start = i;
					int end = i;
					for( int j = i+1; j < feature.size(); j++ ) {
						if( feature.get(j).equals( "I-" + sem ) ) {
							end = j;
						} else {
							break;
						}
					}
					goldNE.put( start + ":" + end, sem );
					if( !performance.containsKey( sem ) ) {
						performance.put( sem, new Integer[]{0,0,0} );
					}
					performance.get( sem )[1] += 1;
				}
			}
			for( String key : goldNE.keySet() ) {
				String sem = goldNE.get( key );
				if( predNE.containsKey( key ) && predNE.get(key).equals( goldNE.get(key) ) ) {
					performance.get( sem )[2] += 1;
				}
			}
			
			
			//System.out.println( performance.toString() );
			for( String key : performance.keySet() ) {
				float p = performance.get(key)[2] / ((float) performance.get(key)[0] );
				float r = performance.get(key)[2] / ((float) performance.get(key)[1] );
				float f1 = 2 * p * r / ( p + r );
				System.out.println( performance.get( key )[0] 
						+ "\t" + performance.get(key)[1] + "\t" + performance.get(key)[2]
						+ "\t" + p + "\t" + r + "\t" + f1 + "\t" + key );				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}
}
