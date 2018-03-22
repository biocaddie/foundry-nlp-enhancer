package edu.uth.clamp.nlp.encoding;
import java.util.*;

import edu.uth.clamp.nlp.util.StringUtil;

public class QueryPreprocessor {

    static public String process( String query ) {
        String newquery = removeChar( query );
        String ret = "";
        Map<String, Integer> tmpmap = new TreeMap<String, Integer>();
        for( String word: newquery.split( " " ) ) {
        	word = StringUtil.stem( word );
        	tmpmap.put( word.toLowerCase(), 0 );
        }
        for( Object o: tmpmap.keySet() ) {
            ret += (String)o + " ";
        }
        
        return ret;
    }
    
    static public String removeChar( String query ) {
        String newquery = "";        
        for( int i = 0; i < query.length(); i++ ) {
            char c = query.charAt( i );
            if( ( c >= 'A' && c <= 'Z' ) || ( c >= 'a' && c <= 'z' ) || ( c >= '0' && c <= '9' ) ) {
                newquery += c;
            } else if( newquery.length() == 0 ){
                newquery += ' ';
            } else if( newquery.charAt( newquery.length() - 1 ) != ' ' ) {
                newquery += ' ';
            }
        }
        return newquery.toLowerCase();
    }
    
    static public Vector<String> nGram( String query ) {
        Vector<String> ret = new Vector<String>();
        
        String[] words = query.split(" ");
        
        for( int i = 0; i < words.length; i++ ) {
            String unigram = words[i];
            ret.add( unigram );
        }
        for( int i = 1; i < words.length; i++ ) {
            String bigram = words[i-1] + " " + words[i];
            ret.add( bigram );
        }
        for( int i = 2; i < words.length; i++ ) {
            String trigram = words[i-2] + " " + words[i-1] + " " + words[i];
            ret.add( trigram );
        }
        return ret;
    }
    
}
