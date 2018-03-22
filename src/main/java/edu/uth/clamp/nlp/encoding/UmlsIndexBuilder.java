package edu.uth.clamp.nlp.encoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class UmlsIndexBuilder {
	static Logger logger = Logger.getLogger( UmlsIndexBuilder.class.getName() );

	// constant
	static String LANGUAGE = "ENG";
	static int maxRestrictionLevel = 100;// use all concepts;
	
	static Map<String, String> cuiSemanticMap = null;
	static Map<String, Integer> sourceRankMap = null;
	static IndexWriter writer = null;
	
	static FileWriter outfile1 = null;
	static FileWriter outfile2 = null;
	
	/**
	 * @param argv command line arguments;
	 * 
	 */
	public static void main( String[] argv ) {
		String mrrank = "data/umls_2015/MRRANK.RRF";
		String mrsty = "data/umls_2015/MRSTY.RRF";
		String mrconso = "data/umls_2015/MRCONSO.RRF";
		String indexDir = "data/umls_2015/umls_index_lucene4";
		//if( argv.length < 4 ) {
		//	System.out.println( "Usage: <MRRANK.RRF> <MRSTY.RRF> <MRCONSO.RRF> <indexDir>" );
		//	mrrank  = argv[0];
		//	mrsty   = argv[1];
		//	mrconso	= argv[2];
		//	System.exit( -1 );
		//}
		
		sourceRankMap = loadRankMap( mrrank );
		cuiSemanticMap = loadCUISemanticMap( mrsty );
		
		try {
			Directory dir = FSDirectory.open( new File( indexDir ) );
	        CharArraySet stopword = new CharArraySet( new HashSet<String>(), false );
	        StandardAnalyzer analyzer = new StandardAnalyzer( stopword );
	        IndexWriterConfig iwc = new IndexWriterConfig( Version.LUCENE_4_10_0, analyzer );
	        
	        BM25Similarity s = new BM25Similarity();
	        iwc.setSimilarity( s );
	        iwc.setOpenMode(OpenMode.CREATE);
	        writer = new IndexWriter(dir, iwc);
	        
			outfile1 = new FileWriter( new File( "data/umls_2015/cui_prefered_terms.txt" ) );
			outfile2 = new FileWriter( new File( "data/umls_2015/cui_indexed_terms.txt" ) );
		
			Map<String, Vector<String>> uniqRecordMap = new HashMap<String, Vector<String>>();
			BufferedReader infile = new BufferedReader( new FileReader( mrconso ) );
			// C0000005|T116|A1.4.1.2.1.7|Amino Acid, Peptide, or Protein|AT17648347||
			String line = "";
			while( ( line = infile.readLine() ) != null ) {
				String[] splitStr = line.trim().split( "\\|" );
				String cui = splitStr[0];
				if( uniqRecordMap.containsKey( cui ) ) {
					uniqRecordMap.get(cui).add( line );
				} else {
					for( String key : uniqRecordMap.keySet() ) {
						processUniqCui( uniqRecordMap.get( key ) );
						logger.info( "cui=[" + cui + "]" );
					}
					uniqRecordMap.clear();
					uniqRecordMap.put( cui, new Vector<String>() );
					uniqRecordMap.get(cui).add( line );
				}				
			}
		
			infile.close();
			writer.close();
			
		} catch( IOException e ) {
            e.printStackTrace();
		}
		
		return;
	}
	
	private static int processUniqCui( Vector<String> recordVec ) {
		String preferTerm = "";
		String cui = "";
		int preferRank = -1;
		Map<String, Integer> termRepeatMap = new HashMap<String, Integer>();
		for( String record : recordVec ) {
			String[] splitStr = record.split( "\\|" );
			//C0000005|ENG|P|L0000005|PF|S0007492|Y|A7755565||M0019694|D012711|MSH|PEN|D012711|(131)I-Macroaggregated Albumin|0|N||
			cui = splitStr[0];
			String lang = splitStr[1];
			if( !lang.equals( LANGUAGE ) ) {
				// ignore none-english terms;
				continue;
			}
			String sourceKey = splitStr[11] + "|" + splitStr[12] + "|" + splitStr[16] + "|";
			String term = splitStr[14];
			String stemTerm = QueryPreprocessor.process( term );
			int rank = 0;
			if( sourceRankMap.containsKey( sourceKey ) ) {
				rank = sourceRankMap.get( sourceKey );
			}
			if( rank > preferRank ) {
				preferTerm = term;
				preferRank = rank;
			}
			if( stemTerm.isEmpty() ) {
				continue;
			}
			if( !termRepeatMap.containsKey( stemTerm ) ) {
				termRepeatMap.put( stemTerm, 0 );				
			}
			termRepeatMap.put( stemTerm, termRepeatMap.get( stemTerm ) + 1 );
		}
		
		try {
			outfile1.write( cui + "\t" + preferTerm + "\t" + cuiSemanticMap.get(cui) + "\t" + QueryPreprocessor.removeChar( preferTerm ).trim() + "\n" );
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for( String record : recordVec ) {
			String[] splitStr = record.split( "\\|" );
			//C0000005|ENG|P|L0000005|PF|S0007492|Y|A7755565||M0019694|D012711|MSH|PEN|D012711|(131)I-Macroaggregated Albumin|0|N||
			cui = splitStr[0];
			String lang = splitStr[1];
			String term = splitStr[14];
			String stemTerm = QueryPreprocessor.process( term );
			if( !lang.equals( LANGUAGE ) ) {
				continue;
			}
			int level = Integer.parseInt( splitStr[15] );
			if( level > maxRestrictionLevel ) {
				// keep restrict level 0 and  1;
				continue;
			}
			if( stemTerm.isEmpty() ) {
				continue;
			}
			
			if( termRepeatMap.containsKey( stemTerm ) ) {
				try {
					Document doc = new Document();
	                doc.add( new StringField( "cui", cui, Field.Store.YES ) );
	                doc.add( new LongField( "repeat", termRepeatMap.get( stemTerm ), Field.Store.YES ) );
	                doc.add( new StringField( "semantic", cuiSemanticMap.get(cui), Field.Store.YES ) );
	                doc.add( new StringField( "official", preferTerm, Field.Store.YES ) );
	                doc.add( new TextField( "term", stemTerm, Field.Store.YES ) );
	                writer.addDocument(doc);
	                
	                outfile2.write( cui
							+ "||" + cuiSemanticMap.get(cui)
							+ "||" + term + "||" + QueryPreprocessor.removeChar( term ) + "||" + stemTerm
							+ "\n" );
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				termRepeatMap.remove( stemTerm );
			}
		}
		
		return 0;
	}
	
	
	
	/**
	 * load semantic types of each cui;
	 * @author jwang16
	 * @param filename  whole path of MRSTY.RRF 
	 * @return <CUI, semantic>
	 */
	private static Map<String, String> loadCUISemanticMap( String filename ) {
		Map<String, String> cuiSemanticMap = new HashMap<String, String>();
		try {
			BufferedReader infile = new BufferedReader( new FileReader( filename ) );
			// C0000005|T116|A1.4.1.2.1.7|Amino Acid, Peptide, or Protein|AT17648347||
			String line = "";
			while( ( line = infile.readLine() ) != null ) {
				String[] splitStr = line.trim().split( "\\|" );
				String cui = splitStr[0];
				String sem = splitStr[3];
				if( cuiSemanticMap.containsKey( cui ) ) {
					cuiSemanticMap.put( cui, cuiSemanticMap.get(cui) + "|" + sem );
				} else {
					cuiSemanticMap.put( cui, sem );
				}				
			}
		
			infile.close();
		} catch( IOException e ) {
			e.printStackTrace();
		}
		logger.info( "load CUISemanticMap finished, size=[" + cuiSemanticMap.size() + "]" );		
		return cuiSemanticMap;
	}
	
	/**
	 * load ranking of each source;
	 * @param filename	whole path of MRRANK.RRF
	 * @return <SourceKey, ranking>
	 */
	private static Map<String, Integer> loadRankMap( String filename ) {
		Map<String, Integer> sourceRankMap = new HashMap<String, Integer>();
		try {
			BufferedReader infile = new BufferedReader( new FileReader( filename ) );
			//0679|MTH|PN|N|
			String line = "";
			while( ( line = infile.readLine() ) != null ) {
				String[] splitStr = line.trim().split( "\\|" );
				int rank = Integer.parseInt( splitStr[0] );
				String sourceKey = line.substring( line.indexOf( "|" ) + 1 );
				sourceRankMap.put( sourceKey, rank );
			}
			infile.close();
		} catch( IOException e ) {
			e.printStackTrace();
		}
		logger.info( "load sourceRankMap finished. size=[" + sourceRankMap.size() + "]" );
		return sourceRankMap;
	}
}
