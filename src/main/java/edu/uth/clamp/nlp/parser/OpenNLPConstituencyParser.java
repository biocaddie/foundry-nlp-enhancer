package edu.uth.clamp.nlp.parser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.AbstractBottomUpParser;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.parser.chunking.Parser;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.NewlineToken;
import org.apache.ctakes.typesystem.type.syntax.PunctuationToken;
import org.apache.ctakes.typesystem.type.syntax.TerminalTreebankNode;
import org.apache.ctakes.typesystem.type.syntax.TopTreebankNode;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceAccessException;

import edu.uth.clamp.nlp.core.OpenNLPPosTagger;
import edu.uth.clamp.nlp.core.ClampTokenizer;

public class OpenNLPConstituencyParser {
	static final String defaultModel = "ctakes_sharpacq-3.1.bin";
	static OpenNLPConstituencyParser instance = null;
	static public OpenNLPConstituencyParser getDefault() {
		if( instance == null ) {
			instance = new OpenNLPConstituencyParser(
					ClampTokenizer.getDefault(),
					OpenNLPPosTagger.getDefault(),
					OpenNLPConstituencyParser.class.getResourceAsStream( defaultModel ) );
		}
		return instance;
	}
	
	Tokenizer tokenizer = null;
	POSTagger posTagger = null;
	
	
	
	
	Parser parser = null;
	public OpenNLPConstituencyParser() {
	}

	public OpenNLPConstituencyParser( Tokenizer tokenizer,
			POSTagger posTagger,
			InputStream instream ) {
		this.tokenizer = tokenizer;
		this.posTagger = posTagger;
		init( instream );		
	}
	
	public int init( InputStream instream ) {
		try {
			ParserModel model = new ParserModel( instream );
			parser = new Parser(model, AbstractBottomUpParser.defaultBeamSize, AbstractBottomUpParser.defaultAdvancePercentage);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
		  if (instream != null) {
		    try {
		    	instream.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}		return 0;
	}
	
	public Parse parse( String sentence ) {
		Span[]   tokens = tokenizer.tokenizePos( sentence );
		String[] tokenStr = tokenizer.tokenize( sentence );
		String[] pos    = posTagger.tag( tokenStr );
		for( int i = 0; i < tokens.length; i++ ) {
			System.out.println( tokenStr[i] + "\t" + pos[i] );
		}
		return parse( sentence, tokens, pos );
	}
	
	public Parse parse( String sentence, Span[] tokens, String[] pos ) {
		Parse p = new Parse( sentence, new Span(0, sentence.length()), AbstractBottomUpParser.INC_NODE, 0, 0);
		for( int i = 0; i < tokens.length; i++ ) {
			//p.insert(new Parse(sentence, tokens[i], AbstractBottomUpParser.TOK_NODE, 0, i));
			p.insert(new Parse(sentence, tokens[i], pos[i], 0, i));
		}
		Parse ret = parser.parse( p );
		StringBuffer bf = new StringBuffer();
		ret.show( bf );		
		return ret;
	}
	
	static public void main( String[] argv ) {
		OpenNLPConstituencyParser parser = OpenNLPConstituencyParser.getDefault();		
		String sentence = "The quick brown fox jumps over the lazy dog .";
		Parse ret = parser.parse( sentence );
		ret.show();
		
		return;
	}
}
