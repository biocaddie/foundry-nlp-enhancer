package edu.uth.clamp.nlp.ner;

public class NESpan {
	int start = 0;
	int end   = 0;
	String sem = "";
	
	public NESpan( int start, int end, String sem ) {
		this.start = start;
		this.end = end;
		this.sem = sem;
	}
	
	public int start() {
		return start;
	}
	public int end() {
		return end;
	}
	public String sem() {
		return sem;
	}
	
	public void start( int start ) {
		this.start = start;
	}
	
	public void end( int end ) {
		this.end = end;
	}
	
	public void sem( String sem ) {
		this.sem = sem;
	}
}
