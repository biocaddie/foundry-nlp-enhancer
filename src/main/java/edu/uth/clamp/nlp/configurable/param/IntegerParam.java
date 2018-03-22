package edu.uth.clamp.nlp.configurable.param;

public class IntegerParam extends Param {
	public IntegerParam(String name, String value, String defaultValue,
			String description) {
		super(name, value, defaultValue, description);
		// TODO Auto-generated constructor stub
	}
	int value;
	public int value() {
		return value;
	}
}
