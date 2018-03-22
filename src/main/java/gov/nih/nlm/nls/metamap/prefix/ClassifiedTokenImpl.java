
//
package gov.nih.nlm.nls.metamap.prefix;

/**
 *
 */

public class ClassifiedTokenImpl 
  implements Token, ClassifiedToken
{
  String text;
  String tokenClass;

  public ClassifiedTokenImpl(String tokenText, String tokenClass) {
    this.text = tokenText;
    this.tokenClass = tokenClass;
  }

  public String getText() {
    return this.text;
  }

  public String getTokenClass() {
    return this.tokenClass;
  }
  public String toString() {
    return this.text + "," + this.tokenClass;
  }
}
