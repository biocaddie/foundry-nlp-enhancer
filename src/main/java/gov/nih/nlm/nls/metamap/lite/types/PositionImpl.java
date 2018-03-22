
//
package gov.nih.nlm.nls.metamap.lite.types;

/**
 *
 */

public class PositionImpl implements Position
{
  int start;
  int end;
  public PositionImpl(int start, int end) {
    this.start = start;
    this.end = end;
  }

  public int getX() {
    return this.start;
  }

  public int getY() {
    return this.end;
  }

  public String toString() {
    return this.start + "/" + this.end;
  }
}
