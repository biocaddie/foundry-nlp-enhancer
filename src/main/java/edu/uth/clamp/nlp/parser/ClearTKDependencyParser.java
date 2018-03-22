package edu.uth.clamp.nlp.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSTagger;
import opennlp.tools.tokenize.Tokenizer;

import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.ConllDependencyNode;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.fit.util.JCasUtil;

import com.googlecode.clearnlp.component.AbstractComponent;
import com.googlecode.clearnlp.dependency.DEPFeat;
import com.googlecode.clearnlp.dependency.DEPNode;
import com.googlecode.clearnlp.dependency.DEPTree;
import com.googlecode.clearnlp.engine.EngineGetter;
import com.googlecode.clearnlp.morphology.AbstractMPAnalyzer;
import com.googlecode.clearnlp.nlp.NLPLib;
import com.googlecode.clearnlp.reader.AbstractReader;

import edu.uth.clamp.nlp.core.ClampTokenizer;
import edu.uth.clamp.nlp.core.OpenNLPPosTagger;

public class ClearTKDependencyParser {
	static final String depModelFile = 	"ctakes-mayo-en-dep-1.3.0.jar";
	static final String engLemmaFile = "ctakes-dictionary-1.3.1.jar";
	static ClearTKDependencyParser instance;	
	
	static public ClearTKDependencyParser getDefault() {
		if( instance == null ) {
			instance = new ClearTKDependencyParser(
					ClampTokenizer.getDefault(),
					OpenNLPPosTagger.getDefault(),
					ClearTKDependencyParser.class.getResourceAsStream( depModelFile ),
					ClearTKDependencyParser.class.getResourceAsStream( engLemmaFile )
					);
		}
		return instance;
	}
	
	Tokenizer tokenizer = null;
	POSTagger posTagger = null;
	AbstractComponent parser = null;
	AbstractMPAnalyzer lemmatizer = null;
	
	public ClearTKDependencyParser( Tokenizer tokenizer, POSTagger posTagger, 
			InputStream modelStream, InputStream lemmaStream ) {
		this.tokenizer = tokenizer;
		this.posTagger = posTagger;
		init( modelStream, lemmaStream );
	}
	
	public int init( InputStream modelStream, InputStream lemmaStream ) {
		try {
			this.parser = EngineGetter.getComponent(modelStream, AbstractReader.LANG_EN, NLPLib.MODE_DEP);
	        this.lemmatizer = EngineGetter.getMPAnalyzer(AbstractReader.LANG_EN, lemmaStream);

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		  if (modelStream != null) {
		    try {
		    	modelStream.close();
		    } catch (IOException e) {
		    }
		  }
		  if (lemmaStream != null) {
			    try {
			    	lemmaStream.close();
			    } catch (IOException e) {
			    }
			  }
		}
		return 0;
	}
	private DEPTree parse(String sentence) {
		String[] tokens = tokenizer.tokenize( sentence );
		String[] pos = posTagger.tag( tokens );
		
		return parse( sentence, tokens, pos );
	}
	
	public DEPTree parse( String sentence, String[] tokens, String[] pos ) {
		DEPTree tree = new DEPTree();
		for( int i = 0; i < tokens.length; i++ ) {
			String lemma = lemmatizer.getLemma( tokens[i], pos[i] );
			DEPNode node = new DEPNode( i + 1, tokens[i], lemma, pos[i], new DEPFeat() );
			tree.add(node);
		}
		tree.initSHeads();
		parser.process( tree );
		
		return tree;
	}
	
	public static void main( String[] argv ) {
		ClearTKDependencyParser parser = ClearTKDependencyParser.getDefault();
		String sentence = "The quick brown fox jumps over the lazy dog .";
		parser.parse( sentence );
	}


	
}
