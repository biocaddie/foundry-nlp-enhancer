
//
package gov.nih.nlm.nls.metamap.lite.types;

/**
 *
 */

public class SpanImpl 
  implements Span
{
  int start;
  int end;
  public SpanImpl(int start, int end) {
    this.start = start;
    this.end = end;
  }
  public int getStart() {
    return this.start;
  }

  public int getEnd() {
    return this.end;
  }

  public String toString() {
    return this.start + "/" + this.end;
  }
}
