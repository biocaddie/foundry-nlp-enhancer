package gov.nih.nlm.ctx;

import java.util.ArrayList;
import java.util.List;


/**
 * A GenericSentence class is a single data structure which can contain a 
 * list of GenericConcepts extracted from a sentence, its text, location in discourse 
 * and probability of being a patient-oriented outcome.
 * 
 *  
 * @author ddemner
 */
public class GenericSentence {
	public String location = "";
	public String txt = "";
	public int pos = 0;
	public double outcomeScore = 0;
	public int outcomeRank = 0;
	public List <GenericConcept> lConcepts = new ArrayList<GenericConcept>();
	
	public GenericSentence(){}
	public GenericSentence(String s){
		txt = s;
	}
	
	public int hashCode() {
		return txt.hashCode();
	}

	public String toString() {
		return  (pos + ". "+ location + ": " + txt +"\n"+lConcepts.toString()+"\n\n");
	}

	public int compareTo(GenericSentence n) {
		return(txt.compareTo(n.txt));
	}
	public boolean equals(GenericSentence n) {
		return (txt.equals(n.txt));
	}
}
