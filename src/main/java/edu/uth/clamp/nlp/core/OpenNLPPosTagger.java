package edu.uth.clamp.nlp.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.Sequence;

public class OpenNLPPosTagger implements POSTagger {
	static POSTagger instance = null;
	static public POSTagger getDefault() {
		if( instance == null ) {
			instance = new OpenNLPPosTagger( OpenNLPPosTagger.class.getResourceAsStream( "mipacq_pos.bin" ) );
		}
		return instance;
	}

	POSTaggerME posTagger = null;
	public OpenNLPPosTagger() {
		posTagger = null;
	}
	
	public OpenNLPPosTagger( InputStream instream ) {
		init( instream );
	}
	
	public int init( InputStream instream ) {
		try {
			POSModel model = new POSModel( instream );
			posTagger = new POSTaggerME(model);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		} finally {
			if ( instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return -1;
				}
			}
		}
		return 0;
	}

	public List<String> tag(List<String> sentence) {
		return posTagger.tag( sentence );
	}

	public String tag(String sentence) {
		return posTagger.tag(sentence);
	}

	public Sequence[] topKSequences(List<String> sentence) {
		return posTagger.topKSequences( sentence );
	}

	public Sequence[] topKSequences(String[] sentence) {
		return posTagger.topKSequences(sentence);
	}
	
	public String[] tag(String[] sentence) {
		return posTagger.tag( sentence );
	}

	public String[] tag(String[] arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public Sequence[] topKSequences(String[] arg0, Object[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
