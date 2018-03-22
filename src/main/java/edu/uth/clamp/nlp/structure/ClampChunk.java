package edu.uth.clamp.nlp.structure;

import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.uima.jcas.JCas;

public class ClampChunk extends TextSection {
    public static final String ATTR_CHUNK = "chunktype";

	public ClampChunk(JCas aJCas, Chunk chunk) {
		super( aJCas, chunk );
		String chunktype = ((Chunk)this.uimaEnt).getChunkType();
		setAttr( ATTR_CHUNK, chunktype );
	}
	
	public String getChunkType() {
		return ((Chunk)this.uimaEnt).getChunkType();
	}
	
	public void setChunkType( String chunktype ) {
		((Chunk)this.uimaEnt).setChunkType( chunktype );		
		setAttr( ATTR_CHUNK, chunktype );
	}
}
