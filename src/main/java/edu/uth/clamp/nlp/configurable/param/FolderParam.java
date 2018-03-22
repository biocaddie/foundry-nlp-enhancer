package edu.uth.clamp.nlp.configurable.param;

import java.io.File;

public class FolderParam extends Param {
	public FolderParam(String name, String value, String defaultValue,
			String description) {
		super(name, value, defaultValue, description);
		// TODO Auto-generated constructor stub
	}

	File value;
	
	public File value() {
		return value;
	}
}
