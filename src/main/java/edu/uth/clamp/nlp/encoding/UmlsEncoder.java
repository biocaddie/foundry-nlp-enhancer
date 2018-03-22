package edu.uth.clamp.nlp.encoding;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;



/**
 * The Class UmlsEncoder.
 */
public class UmlsEncoder {
	
	static final Logger LOGGER = Logger.getLogger( UmlsEncoder.class.getName() );
	
	/**
	 * Gets the default.
	 *
	 * @return the default
	 */
	public static UmlsEncoder getDefault() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * The Class QueryResult.
	 */
	public class QueryResult {
		    
    		/** The ori_query. */
    		public String ori_query;
		    
    		/** The expanded_query. */
    		public String expanded_query;
		    
    		/** The expanded_query_stemmed. */
    		public String expanded_query_stemmed;
		    
    		/** The cui. */
    		public String cui;
		    
    		/** The semantic. */
    		public String semantic;
		    
    		/** The official. */
    		public String official;
		    
    		/** The term. */
    		public String term;
		    
    		/** The repeat. */
    		public Integer repeat;
		    
    		/** The score. */
    		public float score;

		    /**
    		 * Instantiates a new query result.
    		 */
    		public QueryResult() {
		        this.ori_query = "";
		        this.expanded_query = "";
		        this.expanded_query_stemmed = "";
		        this.cui = "";
		        this.semantic = "";
		        this.official = "";
		        this.term = "";
		        this.repeat = 0;
		        this.score = 0;
		    }    
		}
	
	/** The term_reader. */
	IndexReader term_reader             = null;
    
    /** The term_searcher. */
    IndexSearcher term_searcher         = null;
    
    /** The term_analyzer. */
    Analyzer term_analyzer              = null;
    
    /** The term_parser. */
    QueryParser term_parser             = null;
    
    /**
     * Instantiates a new umls encoder.
     */
    public UmlsEncoder() {
    	
    }
    
    /**
     * Instantiates a new umls encoder.
     *
     * @param modelIndex the model index
     * @throws IOException io errors;
     */
    public UmlsEncoder( String modelIndex ) throws IOException {
    	LOGGER.fine("UmlsEncoder: modelIndex: "+modelIndex);
		term_reader = DirectoryReader.open( FSDirectory.open( new File( modelIndex ) ) );
	    term_searcher = new IndexSearcher( term_reader );
        CharArraySet stopword = new CharArraySet( new HashSet<String>(), false );
	    term_analyzer = new StandardAnalyzer(  stopword );
	    term_parser = new QueryParser( "term", term_analyzer );
        BM25Similarity s = new BM25Similarity();
        term_searcher.setSimilarity( s );
    }
    
    /**
     * Gets the top n.
     *
     * @param entity the entity
     * @param n the n
     * @return the top n
     */
    public List<QueryResult> getTopN( String entity, int n ) {
    	List<QueryResult> ret = new ArrayList<QueryResult>();
    	try {
            int hitsPerPage = 10;
            String newquery = QueryPreprocessor.process( entity );
            if( newquery.isEmpty() ) {
                return ret;
            }

            Query query = null;
            synchronized( term_parser ) {
            	query = term_parser.parse( newquery );
            }
            TopDocs results = term_searcher.search(query, hitsPerPage);
            ScoreDoc[] hits = results.scoreDocs;
            
            int numTotalHits = results.totalHits;
            int start = 0;
            int end = Math.min(numTotalHits, hitsPerPage);
            end = Math.min(hits.length, start + hitsPerPage);
            
            //double maxscore = 0;
            for (int i = start; i < end; i++) {
            	//if( i == 0 ) {
                //    maxscore = hits[0].score;
                //}
                //if( maxscore != hits[i].score ) {
                //    break;
                //}
            	Document doc = term_searcher.doc(hits[i].doc);                
                String cui = doc.get( "cui" );
                String semantic = doc.get( "semantic" );
                String official = doc.get( "official" );
                String term = doc.get( "term" );
                Integer repeat = Integer.parseInt( doc.get( "repeat" ) );

                
                QueryResult result = new QueryResult();
                result.ori_query = entity;
                result.expanded_query = entity;
                result.expanded_query_stemmed = newquery;
                
                result.cui = cui;
                result.semantic = semantic;
                result.official = official;
                result.term = term;
                result.repeat = repeat;
                
                result.score = hits[i].score;
                
                ret.add( result );
                
                if( ret.size() >= n ) {
                	break;
                }
            }            
        } catch ( Exception e ) {
        	LOGGER.severe(e.getMessage());
        	LOGGER.throwing(UmlsEncoder.class.getName(), "getTopN", e);
            e.printStackTrace();
        }
    	
        Collections.sort( ret, new Comparator< QueryResult >() {
        	public int compare(QueryResult o1, QueryResult o2) {
        		if( o2.score > o1.score ) {
                    return 1;
                } else if( o1.score > o2.score ) {
                    return -1;
                } else if( o2.repeat > o1.repeat ) {
                    return 1;
                } else if( o2.repeat < o1.repeat ) { 
                    return -1;
                } else if( o2.term.split(" ").length > o1.term.split(" ").length  ) {
                    return -1;
                } else if( o1.term.split(" ").length > o2.term.split(" ").length) {
                    return 1;
                } else if( o2.expanded_query.length() > o1.expanded_query.length() ) {
                    return -1;
                } else if( o2.expanded_query.length() < o1.expanded_query.length() ) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    	return ret;
    }
    
    
    /**
     * Encode.
     *
     * @param entity the entity
     * @return the string
     */
    public String encode( String entity ) {
    	LOGGER.fine("encode: entity: "+entity);
    	List<QueryResult> ret = getTopN( entity, 3 );
    	if( ret.size() >= 1 ) {
    		return ret.get(0).cui;
    	}
    	return "";
    }



    /**
     * The main method.
     *
     * @param argv the arguments
     */
    public static void main( String[] argv ) {
    	String umlsIndex = "/Users/jwang16/Desktop/Release/Clamp_0.10.16_mac/plugins/ClampGUI_0.10.15/resources/umls_index";
    	try {
			UmlsEncoder encoder = new UmlsEncoder( umlsIndex );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    /**
     * Main2.
     *
     * @param argv the argv
     */
    public static void main2( String argv[] ) {
    	if( argv.length < 3 ) {
			System.out.println( "Usage: index_dir prefered_cui_file query_file" );
			System.exit( -1 );
		}
    	String umlsIndex = argv[0];
    	String queryFileName = argv[1];
    	UmlsEncoder encoder;
		try {
			encoder = new UmlsEncoder( umlsIndex );
			BufferedReader infile = new BufferedReader( new FileReader( queryFileName ) );
			String line = "";
			while( ( line = infile.readLine() ) != null ) {
				String[] splitStr = line.trim().split( "\\t" );
				String entity = splitStr[0];
				String cui = splitStr[1];
				List<QueryResult> retList = encoder.getTopN( entity, 20 );
				String ret = "";
				boolean correct = false;
				boolean covered = false;
				for( QueryResult i : retList ) {
					if( cui.equals( i.cui ) ) {
						covered = true;
						ret += "\t1" + " " + i.cui + " " + i.term + "\t" + i.official + " " + i.repeat + " " + i.score + "\n";
					} else {
						ret += "\t0" + " " + i.cui + " " + i.term + "\t" + i.official + " " + i.repeat + " " + i.score + "\n";
					}
				}
				if( retList.size() >= 1 && retList.get(0).cui.equals( cui ) ) {
					correct = true;
				}
				ret = correct + " " + covered + " " + entity + "\t" + cui + "\t" + QueryPreprocessor.process( entity ) + "\n" + ret;
				System.out.println( ret );				
			}
			infile.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    }

}
