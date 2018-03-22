
//
package gov.nih.nlm.nls.metamap.lite.resultformats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Properties;

import bioc.BioCCollection;
import bioc.BioCDocument;
import bioc.BioCPassage;
import bioc.BioCAnnotation;
import bioc.BioCRelation;
import bioc.BioCSentence;

import gov.nih.nlm.nls.metamap.lite.types.Entity;
/**
 *
 */

public class BcEvaluate implements ResultFormatter {


  public void entityListFormatter(PrintWriter writer,
				  List<Entity> entityList) {
  }
  
  public String entityListFormatToString(List<Entity> entityList) {
    return null;
  }
  public void initProperties(Properties properties) {
  }

  public static void writeBcEvaluateAnnotations(PrintWriter writer, BioCSentence sentence)
    throws IOException
  {
    writeBcEvaluateAnnotations(writer, sentence);
  }

  public static void writeBcEvaluateAnnotations(PrintWriter writer, BioCDocument document) {
    Set<String> termSet = new HashSet<String>();
    for (BioCPassage passage: document.getPassages()) {
      for (BioCSentence sentence: passage.getSentences()) {
	for (BioCAnnotation annotation: sentence.getAnnotations()) {
	  termSet.add(annotation.getText());
	}
      }
    }
    int rindex = 1;
    for (String term: termSet) {
      System.out.println(document.getID() + "\t" +
			 term + "\t" +
			 rindex + "\t" +
			 0.9);
      writer.println(document.getID() + "\t" +
		     term + "\t");
		     // rindex + "\t" +
		     // 0.9);
      rindex++;
    }
  }

  public static void writeBcEvaluateAnnotations(PrintStream stream, BioCDocument document) 
    throws IOException
  {
    writeBcEvaluateAnnotations(new PrintWriter(new BufferedWriter(new OutputStreamWriter(stream))), document);
  }

  public static void writeBcEvaluateAnnotations(String filename, BioCDocument document) 
    throws IOException
  {
    PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
    writeBcEvaluateAnnotations(pw, document);
    pw.close();
  }
}
