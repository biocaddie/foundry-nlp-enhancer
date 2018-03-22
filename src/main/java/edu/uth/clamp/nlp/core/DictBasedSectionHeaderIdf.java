package edu.uth.clamp.nlp.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

import edu.uth.clamp.nlp.structure.Document;
import edu.uth.clamp.nlp.util.DFABasedDictionary;
import edu.uth.clamp.nlp.util.StringUtil;

public class DictBasedSectionHeaderIdf implements SectionHeaderIdf {
	public static final String defaultDict = "section_map.txt";

	static SectionHeaderIdf instance = null;
	static public SectionHeaderIdf getDefault() {
		if( instance == null ) {
			instance = new DictBasedSectionHeaderIdf( ClampSentDetector.getDefault(),
					ClampTokenizer.getDefault(),
					DictBasedSectionHeaderIdf.class.getResourceAsStream( defaultDict ) );
		}
		return instance;
	}

	SentenceDetector detector = null;
	Tokenizer tokenizer = null;
	DFABasedDictionary dict = null;
	
	public DictBasedSectionHeaderIdf() {
		detector = null;
		tokenizer = null;
		dict = null;
	}
	public DictBasedSectionHeaderIdf( SentenceDetector detector, 
			Tokenizer tokenizer, InputStream is ) {
		this.detector = detector;
		this.tokenizer = tokenizer;
		init( is );
	}
	
	public void tokenizer( Tokenizer tokenizer ) {
		this.tokenizer = tokenizer;
	}
	public void SentenceDetector( SentenceDetector detector ) {
		this.detector = detector;
	}

	public int init( InputStream is ) {
		dict = new DFABasedDictionary( is );
		return 0;
	}

	public SectionHeader[] idf( String document ) {
		List<Span[]> tokens = new ArrayList<Span[]>();
		for( opennlp.tools.util.Span sent : detector.sentPosDetect( document ) ) {
			List<Span> sentToken = new ArrayList<Span>();
			for( Span token : tokenizer.tokenizePos( document.substring( sent.getStart(), sent.getEnd() ) ) ) {
				sentToken.add( new Span( sent.getStart() + token.getStart(), sent.getStart() + token.getEnd() ) );
			}
			tokens.add( sentToken.toArray( new Span[ sentToken.size() ] ) );
		}
		return idf( document, tokens );
	}
	
	public SectionHeader[] idf( String document,
			List<Span[]> tokens) {
		List<SectionHeader> ret = new ArrayList<SectionHeader>();
		for( Span[] span : tokens ) {
			SectionHeader header = getHeader( document, span );
			if( header != null ) {
				ret.add( header );
			}
		}
		return ret.toArray( new SectionHeader[ ret.size() ] );
	}
	

	SectionHeader getHeader( String document, Span[] sent ) {
		if( !isHeaderSentence( document, sent ) ) {
			return null;
		}
		List<String> tokenStrs = new ArrayList<String>();
		for( Span span : sent ) {
			tokenStrs.add( document.substring( span.getStart(), span.getEnd() ) );
		}
		Vector<edu.uth.clamp.nlp.util.DFABasedDictionary.Span> ret 
			= dict.lookup(tokenStrs.toArray( new String[ tokenStrs.size() ] ) );
		for( edu.uth.clamp.nlp.util.DFABasedDictionary.Span span : ret ) {
			if( span.start() == 0 ) {
				int startPos = sent[ span.start() ].getStart();
				int endPos = sent[ span.end() - 1 ].getEnd();
				return new SectionHeader( startPos, endPos, span.sem() );
			}
		}
		return null;
	}
	
	boolean isHeaderSentence( String document, Span[] sent ) {
		if( sent == null || sent.length == 0 ) {
			return false;
		}
		int s = sent[0].getStart() - 1;
		boolean ret = true;
		while( s >= 0 ) {
			char c = document.charAt(s);
			if( !StringUtil.isSpace(c) ) {
				ret = false;
				break;
			} else if ( !StringUtil.isSentenceEnd(c) ) {
				return true;
			}
		}
		return ret;
	}
	
	public static void main( String[] argv ) {
		Document doc = new Document( "/Users/jwang16/git/clampnlp/data/i2b2/train_text/record-82.txt" );

		SectionHeaderIdf idf = DictBasedSectionHeaderIdf.getDefault();
		for( SectionHeader header : idf.idf( doc.getFileContent() ) ) {
			System.out.println( header.start() + " " + header.end() + " " + header.header() + "\t" + doc.getFileContent().substring( header.start(), header.end() ) );
		}
	}
}
