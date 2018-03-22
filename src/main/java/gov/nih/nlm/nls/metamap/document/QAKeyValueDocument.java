//
package gov.nih.nlm.nls.metamap.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import bioc.BioCDocument;
import bioc.BioCPassage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QAKeyValueDocument implements BioCDocumentLoader {

  public static final Pattern keyValuePattern  = Pattern.compile("([A-Z]+): (.+)");
  
    /** log4j logger instance */
  private final Logger logger = LogManager.getLogger(FreeText.class);

  public BioCDocument loadFileAsBioCDocument(String filename)
    throws FileNotFoundException, IOException
  {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    BioCDocument doc = new BioCDocument();
    String line;
    int offset = 0;
    while ((line = br.readLine()) != null) {
      String[] fields = keyValuePattern.split(line);
      Matcher keyValueMatcher = keyValuePattern.matcher(line);
      while (keyValueMatcher.find()) {
	String key = keyValueMatcher.group(1);
	String value = keyValueMatcher.group(2);
	if (key.equals("MESSAGE")) {
	  BioCPassage message = new BioCPassage();
	  message.setOffset(offset+key.length()+2);
	  message.putInfon("message","message");
	  doc.addPassage(message);
	}
      }
      offset=offset+line.length();
    } /*while*/
    br.close();
    return doc;
  }

  public List<BioCDocument> loadFileAsBioCDocumentList(String filename)
    throws FileNotFoundException, IOException
  {
    BufferedReader br = new BufferedReader(new FileReader(filename));
    List<BioCDocument> docList = readAsBioCDocumentList(br);
    br.close();
    return docList;
  }

  public List<BioCDocument> readAsBioCDocumentList(Reader reader)
    throws IOException {
    List<BioCDocument> docList = new ArrayList<BioCDocument>();
    String line;
    int offset = 0;
    BioCDocument doc = new BioCDocument();
    BufferedReader br;
    if (reader instanceof BufferedReader) {
      br = (BufferedReader)reader;
    } else {
      br = new BufferedReader(reader);
    }
    while ((line = br.readLine()) != null) {
      String[] fields = keyValuePattern.split(line);
      Matcher keyValueMatcher = keyValuePattern.matcher(line);
      while (keyValueMatcher.find()) {
	String key = keyValueMatcher.group(1);
	String value = keyValueMatcher.group(2);
	if (key.equals("MESSAGE")) {
	  BioCPassage message = new BioCPassage();
	  message.setOffset(offset+key.length()+2);
	  message.putInfon("message","message");
	  message.setText(value);
      	  doc.addPassage(message);
	}
      }
      offset=offset+line.length() + 1;
    } /*while*/
    docList.add(doc);
    return docList;
  }
}

