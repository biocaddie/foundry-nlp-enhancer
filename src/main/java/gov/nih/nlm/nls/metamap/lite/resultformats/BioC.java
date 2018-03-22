
//
package gov.nih.nlm.nls.metamap.lite.resultformats;

import bioc.BioCAnnotation;
import bioc.BioCDocument;
import bioc.BioCLocation;
import bioc.BioCPassage;
import bioc.BioCSentence;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import gov.nih.nlm.nls.metamap.lite.types.Entity;

/**
 *
 */

public class BioC {
  Properties properties;
  public void entityListFormatter(PrintWriter writer,
			   List<Entity> entityList) {
    
  }
  public String entityListFormatToString(List<Entity> entityList) {
    return null;
  }
  void initProperties(Properties properties) {
    this.properties = properties;
  }
}
