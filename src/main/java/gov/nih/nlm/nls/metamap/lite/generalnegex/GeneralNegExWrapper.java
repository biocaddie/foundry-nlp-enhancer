
//
package gov.nih.nlm.nls.metamap.lite.generalnegex;

import java.io.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.*;
import java.util.Set;
import java.util.Properties;
import java.util.regex.Pattern;

import generalnegex.GenNegEx;
import generalnegex.Sorter;
import gov.nih.nlm.nls.metamap.lite.NegationDetector;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.prefix.ERToken;

/**
 *
 */

public class GeneralNegExWrapper implements NegationDetector {
  GenNegEx g = new GenNegEx();
  boolean negatePossible = false;
  String triggersFile;
  File ruleFile;
  Scanner sc;
  ArrayList<String> rules;
  
  public GeneralNegExWrapper() {
  }
  public GeneralNegExWrapper(Properties properties) {
    initProperties(properties);
  }
  public void initProperties(Properties properties) {
    try {
      this.triggersFile =
	System.getProperty("general.negex.triggersfile",
			   properties.getProperty("general.negex.triggersfile",
						  "data/negex_triggers.txt"));
      this.negatePossible =
	Boolean.parseBoolean(System.getProperty("general.negex.negate.possible",
						properties.getProperty("general.negex.negate.possible",
								       "false")));
      this.ruleFile = new File(triggersFile);
      this.sc = new Scanner(ruleFile);
      this.rules = new ArrayList<String>();
      while (sc.hasNextLine()) {
	rules.add(sc.nextLine());
      }
    } catch (FileNotFoundException fne) {
       throw new RuntimeException(fne);
    } 
  }
  public void detectNegations(Set<Entity> entitySet, String sentence,
			      List<ERToken> tokenList) {
    try {
      for (Entity entity: entitySet) {
	String phrase = entity.getMatchedText();
	String afterNegCheck = g.negCheck(sentence, phrase, this.rules, this.negatePossible);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
