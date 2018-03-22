package edu.uth.clamp.nlp.configurable.param;

import java.io.File;

public class FileParam extends Param {
	File value = null;
	
	public FileParam(String name, String value, String defaultValue,
			String description) {
		super(name, value, defaultValue, description);
	}
	public File value() {
		if( value == null ) {
			this.value = new File( valueStr );
		}
		return value;
	}
}
