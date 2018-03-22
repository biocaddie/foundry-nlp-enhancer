package edu.uth.clamp.nlp.ner;

public class NERFeature {
	String name;
	String value;
	
	public NERFeature( String name, String value ) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		// don't allow spaces in the feature value;
		this.value = value.replace("\\s", "_" );
	}
	public String dump() {
		//System.out.println( "name=" + name + " value=" + value );
		this.value = value.replace("\\s", "_" );
		this.value = value.replace( "\\", "\\\\" );
		this.value = value.replace( ":", "\\:" );
		
		if( name.isEmpty() ) {
			return value;
		}
		return name + "=[" + value + "]";
	}
}
