package edu.uth.clamp.nlp.structure;

import java.util.HashMap;
import java.util.Map;

import org.apache.ctakes.typesystem.type.relation.Relation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA;

/**
 * Abstract Class for token, phrase, name entity, sentence..
 * 
 * @author CCB clinical NLP group;
 * 
 */
public class TextSection {
	protected JCas aJCas = null;
	protected Annotation uimaEnt = null;
	
    private Map<String, Object> attributes;

    public TextSection( JCas aJCas, Annotation uimaEnt ) {
    	this.aJCas = aJCas;
    	this.uimaEnt = uimaEnt;
        attributes = new HashMap<String, Object>();
    }
    
    //public functions
    /**
     * @return  absolute start offset from the beginning of the note;
     */
    public int getBegin(){
        return uimaEnt.getBegin();
    }
    /**
     * @return  absolute end offset from the beginning of the note;
     */
    public int getEnd(){
        return uimaEnt.getEnd();
    }
    /**
     * @return String of this text section;
     */
    public String textStr(){
        return uimaEnt.getCoveredText();
    }

    /**
     * @param key		name of an attribute
     * @param value		value of an attribute
     * @return 
     */
    public int setAttr( String key, Object value ) {
    	// TODO: question: always overwrite the old values?
    	attributes.put( key, value );
    	if( this.uimaEnt instanceof ClampNameEntityUIMA ) {
    		((ClampNameEntityUIMA)this.uimaEnt).setAttribute( this.toJson() );
    	}
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

	public Annotation getUimaEnt() {
		return uimaEnt;
	}
	
    public String toJson() {
    	Gson gson = new Gson();
    	Map<String, Object> valueMap = new HashMap<String, Object>();
    	valueMap.put( "startPos", Integer.toString( this.getBegin() ) );
    	valueMap.put( "endPos", Integer.toString( this.getEnd() ) );
    	valueMap.put( "text", this.textStr() );
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
			String key = item.getKey();
			if( key.equals( "startPos" ) ) {
				//this.startPos = Integer.parseInt( item.getValue().getAsString() );
				continue;
			} else if( key.equals( "endPos" ) ) {
				//this.endPos = Integer.parseInt( item.getValue().getAsString() );
				continue;
			} else if( key.equals( "text" ) ) {
				//this.textStr = item.getValue().getAsString();
				continue;
			} else {
				attributes.put( item.getKey(), item.getValue().getAsString() );
			}
		}
    }
    
    public void clear() {
    	this.uimaEnt.removeFromIndexes();
    	this.attributes.clear();
    }

}
