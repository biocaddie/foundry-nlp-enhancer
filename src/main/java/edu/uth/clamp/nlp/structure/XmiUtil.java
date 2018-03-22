package edu.uth.clamp.nlp.structure;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.apache.commons.io.output.XmlStreamWriter;
import org.apache.ctakes.typesystem.type.syntax.BaseToken;
import org.apache.ctakes.typesystem.type.syntax.Chunk;
import org.apache.ctakes.typesystem.type.textspan.Segment;
import org.apache.ctakes.typesystem.type.textspan.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.internal.util.XMLUtils;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.jcas.tcas.DocumentAnnotation;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.FileUtils;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.apache.uima.util.XMLSerializer;
import org.uimafit.util.JCasUtil;
import org.xml.sax.SAXException;

import edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA;
import edu.uth.clamp.nlp.typesystem.ClampRelationUIMA;

public class XmiUtil {

	static final String ENCODING = "UTF-8";	
	static TypeSystemDescription tsDesc = null;
	private static void loadTypeSystem() {
		if( tsDesc != null ) {
			return;
		}		
		InputStream is = XmiUtil.class.getResourceAsStream("TypeSystem.xml");
		XMLParser xmlParser = UIMAFramework.getXMLParser();
		File temp;
		try {
			temp = File.createTempFile("tempTypeSystem",".xml");

			FileWriter fileoutput = new FileWriter(temp);
			BufferedReader in = new BufferedReader(new InputStreamReader( is ) );
			String line = null;
			while((line = in.readLine()) != null) {
				fileoutput.write( line );
			}
			fileoutput.flush();
			fileoutput.close();
			temp.deleteOnExit();
			tsDesc = xmlParser.parseTypeSystemDescription(new XMLInputSource(temp));
			tsDesc.resolveImports();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidXMLException e) {
			e.printStackTrace();
		}
	}
	
	public static Vector<ClampSentence> selectSentence( JCas aJCas, int start, int end ) {
		loadTypeSystem();
		Vector<ClampSentence> ret = new Vector<ClampSentence>();
		if( aJCas == null ) {
			return ret;
		}
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( Sentence.type ).iterator();
		while( iter.hasNext() ) {
			Sentence sent = (Sentence)iter.next();
			if( sent.getBegin() >= start && sent.getEnd() <= end ) {
				ret.add( new ClampSentence( aJCas, sent ) );
			}
		}
		return ret;
	}
	
	public static Vector<ClampToken> selectToken( JCas aJCas, int start, int end ) {
		loadTypeSystem();
		Vector<ClampToken> ret = new Vector<ClampToken>();
		if( aJCas == null ) {
			return ret;
		}
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( BaseToken.type ).iterator();
		while( iter.hasNext() ) {
			BaseToken token = (BaseToken)iter.next();
			if( token.getBegin() >= start && token.getEnd() <= end ) {
				ret.add( new ClampToken( aJCas, token ) );
			}
		}
		return ret;
	}
	
	public static Vector<ClampSection> selectSection( JCas aJCas, int start, int end ) {
		loadTypeSystem();
		Vector<ClampSection> ret = new Vector<ClampSection>();
		if( aJCas == null ) {
			return ret;
		}
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( Segment.type ).iterator();
		while( iter.hasNext() ) {
			Segment section = (Segment)iter.next();
			if( section.getBegin() >= start && section.getEnd() <= end ) {
				ret.add( new ClampSection( aJCas, section ) );
			}
		}
		return ret;
	}
	
	public static Vector<ClampChunk> selectChunk( JCas aJCas, int start, int end ) {
		loadTypeSystem();
		Vector<ClampChunk> ret = new Vector<ClampChunk>();
		if( aJCas == null ) {
			return ret;
		}
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( Chunk.type ).iterator();
		while( iter.hasNext() ) {
			Chunk chunk = (Chunk)iter.next();
			if( chunk.getBegin() >= start && chunk.getEnd() <= end ) {
				ret.add( new ClampChunk( aJCas, chunk ) );
			}
		}
		return ret;
	}
	
	public static Vector<ClampNameEntity> selectNE( JCas aJCas, int start, int end ) {
		loadTypeSystem();
		Vector<ClampNameEntity> ret = new Vector<ClampNameEntity>();
		if( aJCas == null ) {
			return ret;
		}
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( ClampNameEntityUIMA.type ).iterator();
		while( iter.hasNext() ) {
			ClampNameEntityUIMA cne = (ClampNameEntityUIMA)iter.next();
			if( cne.getBegin() >= start && cne.getEnd() <= end ) {
				ret.add( new ClampNameEntity( aJCas, cne ) );
			}
		}
		return ret;
	}
	
	public static Vector<ClampRelation> selectRelation(JCas aJCas, int start, int end ) {
		
		loadTypeSystem();
		Vector<ClampRelation> ret = new Vector<ClampRelation>();
		if( aJCas == null ) {
			return ret;
		}
		
		for( ClampRelationUIMA e : JCasUtil.select( aJCas, ClampRelationUIMA.class ) ) {
			ClampNameEntityUIMA from = e.getEntFrom();
			ClampNameEntityUIMA to = e.getEntTo();
			if( from.getBegin() >= start && from.getEnd() <= end && to.getBegin() >= start && to.getEnd() <= end ) {
				ret.add( new ClampRelation(  aJCas, e ) );
			}
		}
		
		
		/*
		FSIterator<TOP> iter = aJCas.getTIndex( ClampNameEntityUIMA.type ).iterator();
		while( iter.hasNext() ) {
			ClampNameEntityUIMA cne = (ClampNameEntityUIMA)iter.next();
			if( cne.getBegin() >= start && cne.getEnd() <= end ) {
				ret.add( new ClampNameEntity( aJCas, cne ) );
			}
		}*/
		return ret;
	}

	
	
	/**
	 * @return an empty JCas;
	 * @throws UIMAException 
	 */
	public static JCas createJCas() throws UIMAException {
		loadTypeSystem();
		return JCasFactory.createJCas( tsDesc );
	}
	
	/**
	 * create a JCas, using given filename and text;
	 * @param filename
	 * @param text
	 * @return
	 * @throws UIMAException 
	 */
	public static JCas createJCas( String filename, String text ) throws UIMAException {
		loadTypeSystem();
		char[] charArray = text.toCharArray();
        while( true ) {
            int pos = XMLUtils.checkForNonXmlCharacters( charArray, 0, charArray.length, false );
            if( pos != -1 ) {
                charArray[pos] = ' ';
            } else {
                break;
            }
        }
        text = String.valueOf( charArray );
        

        
        JCas aJCas = createJCas();
		aJCas.setDocumentText(text);
		
		FSIterator<Annotation> iter = aJCas.getAnnotationIndex( DocumentAnnotation.type ).iterator();
		
        DocumentAnnotation annotation = (DocumentAnnotation)iter.get();
        		
	    return aJCas;
	}
	
	/**
	 * create a JCas, then load file into it;
	 * @param filename
	 * @return
	 * @throws IOException 
	 * @throws UIMAException 
	 */
	public static JCas createJCas( String filename ) throws IOException, UIMAException {
		loadTypeSystem();
	    File file = new File( filename );
	    String text = FileUtils.file2String(file, ENCODING);
	    return createJCas( filename, text );
	}
	
	
	public static JCas createJCas1( String filename, String text ) throws  UIMAException {
		loadTypeSystem();
	    return createJCas( filename, text );
	}
	
	
	public static void writeXmi(JCas aJCas, String filename) throws SAXException, IOException {
		loadTypeSystem();
		FileOutputStream out = null;		
		try {
			// write XMI
			out = new FileOutputStream( new File( filename ) );
			XmiCasSerializer ser = new XmiCasSerializer(aJCas.getCas().getTypeSystem());
			XMLSerializer xmlSer = new XMLSerializer(out, false);
			ser.serialize(aJCas.getCas(), xmlSer.getContentHandler());
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	public static JCas loadXmi( String xmiFileName ) throws UIMAException, IOException {
		loadTypeSystem();
		JCas jCas = JCasFactory.createJCas( tsDesc );
		org.apache.uima.fit.util.CasIOUtil.readJCas(jCas, new File( xmiFileName ) );
		return jCas;
	}

	
	
}
