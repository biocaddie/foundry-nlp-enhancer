
//
package gov.nih.nlm.nls.types.tokenization;

import gov.nih.nlm.nls.types.Annotation;

/**
 *
 */

public class TokenImpl
  implements Annotation, Token
{
  String text;
  int offset;
  String id = "UNKNOWN";

  public static String TOKENTYPE = "TOKEN";

  public TokenImpl(String text, int offset) {
    this.text = text;
    this.offset = offset;
  }
  public TokenImpl(String text, int offset, String id) {
    this.text = text;
    this.offset = offset;
    this.id = id;
  }

  public String getId() {
    return this.id;
  }

  public String getType() {
    return TOKENTYPE;
  }

  public int getOffset() {
    return this.offset;
  }

  public int getLength() {
    return this.text.length();
  }

  public String getText() {
    return this.text;
  }
}
