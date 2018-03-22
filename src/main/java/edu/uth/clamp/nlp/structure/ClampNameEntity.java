package edu.uth.clamp.nlp.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.uima.jcas.JCas;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA;

public class ClampNameEntity extends TextSection {
	
	// pre-defined list of attr;
	public static final String ATTR_SEMANTIC_TAG = "semanticTag";
	public static final String ATTR_ASSERTION = "assertion";
	public static final String ATTR_UMLS_CUI = "umlsCui";
	public static final String ATTR_UMLS_CUI_DESC = "umlsCuiDesc";
	
	
	public ClampNameEntity( JCas aJCas, ClampNameEntityUIMA entity ) {
		super( aJCas, entity );
		fromJson( ((ClampNameEntityUIMA)this.uimaEnt).getAttribute() );
	}
	
    public Vector<ClampToken> getTokens() {
        return XmiUtil.selectToken(aJCas, getBegin(), getEnd());
    }
    
	public ClampNameEntity( JCas aJCas, int start, int end, String sem ) {
		super( aJCas, null );
		this.uimaEnt = new ClampNameEntityUIMA( aJCas );
		this.uimaEnt.setBegin( start );
		this.uimaEnt.setEnd( end );
		uimaEnt.addToIndexes();
		this.setSemanticTag( sem );
	}

	/**
	 * @return semantic type of this entity;
	 */
	public String getSemanticTag() {
		return (String)getAttr( ATTR_SEMANTIC_TAG );
	}

	/**
	 * Set semantic tag of this entity, eg. Problem or Treatment or Test;
	 * @param semanticTag
	 */
	public void setSemanticTag(String semanticTag) {
		setAttr( ATTR_SEMANTIC_TAG, semanticTag );
	}

	/**
	 * @return umls cui of this entity;
	 */
	public String getUmlsCui() {
		return (String)getAttr( ATTR_UMLS_CUI );
	}

	/**
	 * set umls cui of this entity by umls encoder;
	 * @param umlsCui
	 */
	public void setUmlsCui(String umlsCui) {
		setAttr( ATTR_UMLS_CUI, umlsCui );
	}

	/**
	 * @return description of umls cui;
	 */
	public String getUmlsCuiDesc() {
		return (String)getAttr( ATTR_UMLS_CUI_DESC );
	}

	/**
	 * set the description of umls cui;
	 * @param umlsCuiDesc
	 */
	public void setUmlsCuiDesc(String umlsCuiDesc) {
		setAttr( ATTR_UMLS_CUI_DESC, umlsCuiDesc );
	}

	/**
	 * @return assertion info of this entity;
	 */
	public String getAssertion() {
		return (String)getAttr( ATTR_ASSERTION );
	}

	/**
	 * set assertion info of this entity;
	 * @param assertion
	 */
	public void setAssertion(String assertion) {
		setAttr( ATTR_ASSERTION, assertion );
	}
}
