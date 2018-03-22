package edu.uth.clamp.nlp.structure;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.jcas.JCas;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA;
import edu.uth.clamp.nlp.typesystem.ClampRelationUIMA;

public class ClampRelation {
	
	// pre-defined list of attr;
	public static final String ATTR_SEMANTIC_TAG = "semanticTag";
	protected JCas aJCas = null;
	protected ClampRelationUIMA uimaRel = null;
    private Map<String, Object> attributes;
    
    ClampNameEntity entFrom = null;
    ClampNameEntity entTo = null;
	
	public ClampRelation( JCas aJCas, ClampRelationUIMA relation ) {
		this.aJCas = aJCas;
		this.uimaRel = relation;
        attributes = new HashMap<String, Object>();
		fromJson( uimaRel.getAttribute() );
        entFrom = new ClampNameEntity( aJCas, relation.getEntFrom() );
        entTo = new ClampNameEntity( aJCas, relation.getEntTo() );
	}
	
	public ClampRelation( ClampNameEntity entFrom, ClampNameEntity entTo, String sem ) {
		this.aJCas = entFrom.getJCas();
		this.uimaRel = new ClampRelationUIMA( aJCas );
        attributes = new HashMap<String, Object>();
        this.entFrom = entFrom;
        this.entTo = entTo;
        uimaRel.setEntFrom( (ClampNameEntityUIMA)entFrom.getUimaEnt() );
		uimaRel.setEntTo( (ClampNameEntityUIMA)entTo.getUimaEnt() );
		this.setSemanticTag( sem );
		this.uimaRel.addToIndexes();
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

	public ClampNameEntity getEntFrom() {
		return this.entFrom;
	}
	public ClampNameEntity getEntTo() {
		return this.entTo;
	}

	public void getEntFrom( ClampNameEntity entFrom ) {
		this.entFrom = entFrom;
	}
	public void getEntTo( ClampNameEntity entTo ) {
		this.entTo = entTo;
	}

    /**
     * @param key		name of an attribute
     * @param value		value of an attribute
     * @return 
     */
    public int setAttr( String key, Object value ) {
    	// TODO: question: always overwrite the old values?
    	attributes.put( key, value );
    	uimaRel.setAttribute( this.toJson() );
    	return 0;
    }
    
    /**
     * @param key name of an attribute
     * @return	value of this attribute
     */
    public Object getAttr( String key ) {
    	if( attributes.containsKey( key ) ) {
    		return attributes.get( key );
    	} else {
    		return null;
    	}
    }
    
    /**
     * @param key name of an attribute
     * @return this textSection has the attribute or not
     */
    public boolean hasAttr( String key ) {
    	return attributes.containsKey( key );
    }
    
    /**
     * @return all the attributes;
     */
    public Map<String, Object> getAllAttr() {
    	return this.attributes;
    }

	public JCas getJCas() {
		return aJCas;
	}

	public ClampRelationUIMA getUimaRel() {
		return uimaRel;
	}
	
    public String toJson() {
    	Gson gson = new Gson();
    	Map<String, Object> valueMap = new HashMap<String, Object>();
    	for( String attr : this.getAllAttr().keySet() ) {
    		if( this.hasAttr( attr ) ) {
    			valueMap.put( attr, this.getAttr( attr ) );
    		}
    	}
    	return gson.toJson( valueMap );
    }
    
    public void fromJson( String json ) {
    	JsonParser parser = new JsonParser();
		JsonElement element = parser.parse( json );
		element.getAsJsonObject();
		for(Map.Entry<String,JsonElement> item: element.getAsJsonObject().entrySet() ) {
			attributes.put( item.getKey(), item.getValue().getAsString() );
		}
    }
    
    public void clear() {
    	this.uimaRel.removeFromIndexes();
    	this.attributes.clear();
    }
}
