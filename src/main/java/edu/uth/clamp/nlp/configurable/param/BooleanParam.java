package edu.uth.clamp.nlp.configurable.param;




public class BooleanParam extends Param {
	public BooleanParam(String name, String value, String defaultValue,
			String description) {
		super(name, value, defaultValue, description);
		// TODO Auto-generated constructor stub
	}

	boolean value;
	
	public boolean value() {
		return value;
	}
}
