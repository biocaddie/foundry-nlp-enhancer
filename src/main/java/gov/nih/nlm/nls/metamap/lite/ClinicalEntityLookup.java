
//
package gov.nih.nlm.nls.metamap.lite;

import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Arrays;

/**
 * Do entity recognition on clincal document.
 */

public class ClinicalEntityLookup {

  /** ignore entire line for these strings: */
  List<String> ignoreEntireLineStringList = new ArrayList<String>();
  /** ignore entire line for these strings: */
  Set<String> ignoreEntireLineStringSet = new HashSet<String>();
  /** List of strings to omit from consideration when recognizing entities: */
  static List<String> ignoreStringList = new ArrayList<String>();
  /** Set of strings to ignore */
  static Set<String> ignoreStringSet = new HashSet<String>();
  /** List of regular expression patterns to omit from consideration when recognizing entities: */
  static List<String> regExpPatternsToIgnore = Arrays.asList( "\\[\\*\\*.\\*\\*\\]");

  public void setIgnoreEntireLineSet(Set<String> stringSet) {
    this.ignoreEntireLineStringList = new ArrayList<String>(stringSet);
    this.ignoreEntireLineStringSet = stringSet;
  }
  public void setIgnoreEntireLineSet(List<String> stringList) {
    this.ignoreEntireLineStringList = stringList;
    this.ignoreEntireLineStringSet = new HashSet<String>(stringList);
  }
  public void setIgnoreEntireLineSet(Collection<String> stringCollection) {
    this.ignoreEntireLineStringList = new ArrayList<String>(stringCollection);
    this.ignoreEntireLineStringSet = new HashSet<String>(stringCollection);
  }

  
}
