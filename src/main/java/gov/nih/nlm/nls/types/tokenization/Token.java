
//
package gov.nih.nlm.nls.types.tokenization;
import gov.nih.nlm.nls.types.Annotation;
/**
 *
 */

public interface Token extends Annotation {
  String getId();
  String getType();
  int getOffset();
  int getLength();
  String getText();
}
