package edu.uth.clamp.nlp.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.Span;

public class OpenNLPChunker implements Chunker {
	static Chunker instance = null;
	static public Chunker getDefault() {
		if( instance == null ) {
			instance = new OpenNLPChunker( OpenNLPChunker.class.getResourceAsStream( "en-chunker.bin" ) );
		}
		return instance;
	}
	
	
	public static final String CHUNKO = "O";
	public static final String CHUNKB = "B-";
	public static final String CHUNKI = "I-";
	
	public class Chunk {
		int start = -1;
		int end = -1;
		String type = "";
		public Chunk( int start, int end, String type ) {
			this.start = start;
			this.end = end;
			this.type = type;
		}
		
		public int start() {
			return this.start;
		}
		public int end() {
			return this.end;
		}
		public String type() {
			return this.type;
		}
	}

	ChunkerME chunker = null;
	public OpenNLPChunker() {
		chunker = null;
	}
	public OpenNLPChunker( InputStream instream ) {
		init( instream );
	}
	public int init( InputStream instream ) {
		try {
			ChunkerModel model = new ChunkerModel( instream );
			chunker = new ChunkerME(model);
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
	
	String[] chunkBIO( String[] tokens, String[] tags ) {
		return chunker.chunk( tokens, tags );		
	}

	public List<String> chunk(List<String> toks, List<String> tags) {
		return chunker.chunk(toks, tags);
	}

	public String[] chunk(String[] toks, String[] tags) {
		return chunker.chunk(toks, tags);
	}

	public Span[] chunkAsSpans(String[] toks, String[] tags) {
		return chunker.chunkAsSpans(toks, tags);
	}

	public Sequence[] topKSequences(List<String> sentence, List<String> tags) {
		return chunker.topKSequences(sentence, tags);
	}

	public Sequence[] topKSequences(String[] sentence, String[] tags) {
		return chunker.topKSequences(sentence, tags);
	}

	public Sequence[] topKSequences(String[] sentence, String[] tags,
			double minSequenceScore) {
		return chunker.topKSequences(sentence, tags, minSequenceScore);
	}
	
	public Chunk[] chunk( String sent, Span[] spans, String[] tags ) {
		List<String> tokens = new ArrayList<String>();
		for( int i = 0; i < spans.length; i++ ) {
			tokens.add( sent.substring( spans[i].getStart(), spans[i].getEnd() ) );
		}
		String[] bios = chunkBIO( tokens.toArray( new String[ spans.length ] ), tags );
		
		List<Chunk> ret = new ArrayList<Chunk>();
		int i = 0;
		while( i < bios.length ) {
			String bio = bios[i];
			if( bio.equals( CHUNKO ) ) {
				ret.add( new Chunk( spans[i].getStart(), spans[i].getEnd(), "CHUNKO" ) );
				i++;
			} else if( bio.startsWith( CHUNKB ) ) {
				String type = bio.substring( CHUNKB.length() );
				int start = i;
				int end = i;
				for( int j = i + 1; j < bios.length; j++ ) {
					if( bios[j].equals( CHUNKI + type ) ) {
						end = j;
					} else {
						i = j;
						break;
					}
				}
				ret.add( new Chunk( spans[start].getStart(), spans[end].getEnd(), type ) );
			} else {
				i++;
			}
		}
		return ret.toArray( new Chunk[ ret.size() ] );		
	}
	
	
	/*
	public static void main( String[] argv ) {
		ClampTokenizer tokenizer = new ClampTokenizer( ClampTokenizer.class.getResourceAsStream( "rule.conf" ) );
		OpenNLPPosTagger posTagger = new OpenNLPPosTagger( OpenNLPPosTagger.class.getResourceAsStream( "mipacq_pos.bin" ) );
		OpenNLPChunker chunker = new OpenNLPChunker( OpenNLPChunker.class.getResourceAsStream( "en-chunker.bin" ) );
		String sent = " This is a   \n sentence  05-16-1982. ";
		String[] tokens = tokenizer.tokenize(sent);
		Span[] spans = tokenizer.tokenizePos(sent);
		String[] tags = posTagger.tag( tokens );
		Chunk[] chunks = chunker.chunk( sent, spans, tags );
		System.out.println( "Sent=[" + sent + "]" );
		for( int i = 0; i < tokens.length; i++ ) {
			System.out.println( "Token:\t" + tags[i] + "\t" + tokens[i] );
		}
		for( int i = 0; i < chunks.length; i++ ) {
			System.out.println( "Chunk:\t" + chunks[i].start() + "\t" + chunks[i].end() + "\t" + chunks[i].type() + "\t" + sent.substring( chunks[i].start(), chunks[i].end() ));
		}
	}*/

}
