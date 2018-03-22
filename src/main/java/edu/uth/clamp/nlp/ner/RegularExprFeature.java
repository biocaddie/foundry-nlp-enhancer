package edu.uth.clamp.nlp.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExprFeature implements NERFeatureExtractor {
	
	public static final RegularExprFeature INSTANCE = new RegularExprFeature();
	
	public static final String reglist = "reglist.txt";
	Map<String, Pattern> regMap = new HashMap<String, Pattern>();
	
	public RegularExprFeature() {
		loadFile( RegularExprFeature.class.getResourceAsStream( reglist ) );
	}
	
	public RegularExprFeature(File value) {
		try {
			loadFile( new FileInputStream( value ) );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void loadFile( InputStream instream ) {
		BufferedReader infile = new BufferedReader( new 
				InputStreamReader(instream ) );
        String line = "";
        try {
            while ((line = infile.readLine()) != null)   {
            	if( line.trim().isEmpty() || line.trim().startsWith( "#" ) ) {
            		continue;
            	}
                String[] splitLine = line.trim().split( "\t" );
                String type = splitLine[0];
                String reg = splitLine[1];
                Pattern p = Pattern.compile( reg );
                regMap.put( type, p );
            }
            infile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
   	}



	public synchronized int extract(NERSentence sent) {
		for( int i = 0; i < sent.length(); i++ ) {
			extract( sent, i );
		}
		return 0;
	}
	public int extract( NERSentence sent, int index ) {
		String token = sent.getToken( index );
		for( String type : regMap.keySet() ) {
			Pattern p = regMap.get( type );
			Matcher m = p.matcher( token );
			if( m.matches() ) {
				sent.addFeature( index, new NERFeature("Reg" + type, "TRUE" ) );
			} else {
				sent.addFeature( index, new NERFeature( "Reg" + type, "FALSE" ) );
			}
		}
		return 0;
	}

}
