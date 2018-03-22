package edu.uth.clamp.nlp.configurable.param;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class Param implements Serializable {
	
	String name;
	String valueStr;
	String defaultValue;
	String description;
	
	public Param( String name, String value, String defaultValue, String description ) {
		this.name = name;
		this.valueStr = value;
		this.defaultValue = defaultValue;
		this.description = description;
	}
	
	public boolean isDefault() {
		return valueStr.equals( defaultValue );
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValueStr() {
		return valueStr;
	}
	public void setValueStr(String value) {
		this.valueStr = value;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		String ret = "";
		ret += "<" + this.getClass().getName() + ">\n";
		ret += "  <name>" + name + "</name>\n";
		ret += "  <valueStr>" + valueStr + "</valueStr>\n";
		ret += "  <defaultValue>" + defaultValue + "</defaultValue>\n";
		ret += "  <description>" + description + "</description>\n";
		ret += "</" + this.getClass().getName() + ">\n";
		return ret;
	}

}

