// Signature for all BioC Sentence entity annotators
package gov.nih.nlm.nls.metamap.lite;

import java.util.Set;
import bioc.BioCSentence;
import gov.nih.nlm.nls.metamap.lite.types.Entity;

/**
 *
 */

public interface BioCSentenceEntityAnnotator {
  
  /** 
   * Annotate sentence using entities from entity set adding entity
   * and concept annotations with linking relations.
   *
   * @param docid document id of parent BioCDocument of sentence
   * @param BioCSentence with token and part-of-speech annotations.
   * @return BioCSentence with entity and concept annotations with linking relations.
   */
  BioCSentence annotateSentence(BioCSentence tokenizedSentence, Set<Entity> entitySet);
}
