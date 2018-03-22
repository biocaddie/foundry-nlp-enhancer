
//
package gov.nih.nlm.nls.metamap.lite.resultformats.mmi;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.metamap.lite.resultformats.ResultFormatter;
/**
 * Alternate Fielded MetaMap Indexing (MMI) Output
 *
 * 
 */

public class MMILike implements ResultFormatter {

  public static void displayEntityList(List<Entity> entityList) 
  {
    Collections.reverse(entityList);
    for (Entity entity: entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(entity.getScore()).append("|")
	.append("|").append(entity.getStart()).append(":").append(entity.getLength()).append("|");
      for (Ev ev: entity.getEvList()) {
	sb.append(ev.getConceptInfo().getPreferredName()).append("|")
	  .append(ev.getConceptInfo().getCUI()).append("|")
	  .append(Arrays.toString(ev.getConceptInfo().getSemanticTypeSet().toArray()).replaceAll("(^\\[)|(\\]$)",""))
	  .append("|")
	  .append(Arrays.toString(ev.getConceptInfo().getSourceSet().toArray()).replaceAll("(^\\[)|(\\]$)",""));
      }
      sb.append("|");

      System.out.println(sb);
    }
  }

  public static void displayEntityList(PrintWriter pw, List<Entity> entityList) 
  {
    Collections.reverse(entityList);
    for (Entity entity: entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(entity.getScore()).append("|")
	.append("|").append(entity.getStart()).append(":").append(entity.getLength()).append("|");
      for (Ev ev: entity.getEvList()) {
	sb.append(ev.getConceptInfo().getPreferredName()).append("|")
	  .append(ev.getConceptInfo().getCUI()).append("|")
	  .append(Arrays.toString(ev.getConceptInfo().getSemanticTypeSet().toArray()).replaceAll("(^\\[)|(\\]$)",""))
	  .append("|")
	  .append(Arrays.toString(ev.getConceptInfo().getSourceSet().toArray()).replaceAll("(^\\[)|(\\]$)",""));
      }
      sb.append("|");

      pw.println(sb);
    }
  }

  public void entityListFormatter(PrintWriter writer,
				  List<Entity> entityList) {
    displayEntityList(writer, entityList);
  }
  public String entityListFormatToString(List<Entity> entityList) {
    return null;
  }
  public void initProperties(Properties properties) {
  }
}
