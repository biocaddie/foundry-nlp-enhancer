package edu.uth.clamp.nlp.core;

public class SectionHeader {
	int start = 0;
	int end = 0;
	String header = "";

	public SectionHeader( int start, int end, String header ) {
		this.start = start;
		this.end = end;
		this.header = header;
	}
	
	public void start( int start ) {
		this.start = start;
	}
	public void end( int end ) {
		this.end = end;
	}
	
	public int start() {
		return this.start;
	}
	public int end() {
		return this.end;
	}
	public String header() {
		return this.header;
	}
}
