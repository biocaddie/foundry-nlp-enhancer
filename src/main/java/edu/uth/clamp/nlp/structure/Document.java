package edu.uth.clamp.nlp.structure;

import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.uima.UIMAException;
import org.apache.uima.jcas.JCas;
import org.xml.sax.SAXException;

public class Document {
	Logger logger = Logger.getLogger( Document.class.getName() );

	JCas aJCas = null;
	public String filename = "";
	public Document() {
	}
	
	
	public Document(String filename, String txt)
	{
		this.filename = filename;
		try {
			aJCas = XmiUtil.createJCas1( filename,txt );
		} catch (UIMAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); }
		
	}
	
	
	public Document( String filename ) {
		this.filename = filename;
		if( filename.endsWith( ".xmi" ) ) {
			loadXmiFile( filename );
		} else {
			loadTextFile( filename );
		}
	}
	
	public Document(File textFile) {
		this.filename = textFile.getAbsolutePath();
		if( filename.endsWith( ".xmi" ) ) {
			loadXmiFile( filename );
		} else {
			loadTextFile( filename );
		}	
	}

	public Document(JCas aJCas) {
		this.aJCas = aJCas;
	}

	public int loadTextFile( String filename ){
		this.filename = filename;
		logger.fine( "loadTextFile, filename=[" + filename + "]" );
		try {
			aJCas = XmiUtil.createJCas( filename );
		} catch (UIMAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public int loadXmiFile( String filename ){
		this.filename = filename;
		logger.fine( "loadXmiFile, filename=[" + filename + "]" );
		try {
			aJCas = XmiUtil.loadXmi( filename );
		} catch (UIMAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	public int save( String filename ) {
		try {
			XmiUtil.writeXmi(aJCas, filename);
			return 0;
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public JCas getJCas() {
		return aJCas;
	}
	
	public String getFileName() {
		return filename;
	}
	
	public Vector<ClampSection> getSections() {
		return XmiUtil.selectSection(aJCas, 0, aJCas.getDocumentText().length() );
	}
	public Vector<ClampSentence> getSentences() {
		return XmiUtil.selectSentence(aJCas, 0, aJCas.getDocumentText().length() );
	}
	public Vector<ClampChunk> getChunks() {
		return XmiUtil.selectChunk(aJCas, 0, aJCas.getDocumentText().length() );
	}
	public Vector<ClampToken> getTokens() {
		return XmiUtil.selectToken(aJCas, 0, aJCas.getDocumentText().length() );
	}
	public Vector<ClampNameEntity> getNameEntity() {
		return XmiUtil.selectNE(aJCas, 0, aJCas.getDocumentText().length() );
	}
	
	public Vector<ClampRelation> getRelations() {
		return XmiUtil.selectRelation( aJCas, 0, aJCas.getDocumentText().length() );
	}

	public String getFileContent() {
		return aJCas.getDocumentText();
	}

	public void setJCas(JCas aJCas) {
		this.aJCas = aJCas;		
	}


	
}
