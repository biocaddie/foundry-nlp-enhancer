
//
package gov.nih.nlm.nls.metamap.lite.ctx;

import gov.nih.nlm.ctx.GenericConcept;
import gov.nih.nlm.ctx.NegX;
import gov.nih.nlm.nls.types.Sentence;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;

/**
 *
 */

public class ApplyCtxNegX {
  /** Given annotated sentence list with entities, determine negations
   * using CTX's version of NegX.
   */

  public static List<List<String>> applyContextUsingEntities(List<Entity> entityList, 
							     List<Sentence> sentenceList) 
    throws Exception {
    List<GenericConcept> arr = new ArrayList<GenericConcept>();
    Map<String, String> mS = new HashMap<String,String>();
    for (Entity entity: entityList) {
      for (Ev ev: entity.getEvSet()) {
	arr.add(new GenericConcept(ev.getConceptInfo().getCUI(),
				   ev.getConceptInfo().getPreferredName(),
				   ev.getMatchedText(),
				   (String[])ev.getConceptInfo().getSemanticTypeSet().toArray()));
	

	}
      new NegX(arr, null);
      for (GenericConcept c : arr) {
	
      }
    }
    return null;
  }
}
