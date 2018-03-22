package gov.nih.nlm.ctx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * A GenericConcept class is a single data structure which can contain a 
 * UMLS Concept OR a Negex Phrase OR a space delimited token.
 * <p>
 * Methods are provided to find out what type of phrase this is as well as
 * shortcut methods such as <code>isUMLSConcept()</code>
 * 
 * <p>Methods are also provided to identify the Negation Status
 * 
 *  
 * @author bashyamv, ddemner
 */
public class GenericConcept 
{
  // GENERIC PARAMETERS
  
  private String conceptName = null;
  private String[] lemmas = null;
  private String phraseType = null;
  private String prefName = null;
 // private String conceptType=null;
  private boolean isNegexPhrase = false;
  private boolean isUMLSConcept = false;
  private boolean isSimpleToken = false;
  private boolean isOtherConcept = false;
  
  // PROPERTIES USED IN TRIEHASHING
  private String firstWord=null;
  private int size;
  private int position;
  private int offset = 0;
  private int length = 0;
  
  private String semanticType = null;
  private List<String> lSemTypes = new ArrayList<String>();
  private String cui=null;
  private String source=null;
  private String[] semGroups = null;
  
  // NEGEX PHRASE PARAMETERS - FOR A NEGEX ENTRY
  boolean isPreUMLSPhrase = false;
  boolean isPseudoNegationPhrase = false;
  boolean isPostUMLSPhrase = false;
  boolean isConditionalPossibilityPhrase = false;
  boolean isConjunction = false;
  private boolean isTerminationEntry=false;
  private String negexType=null;
  
  // NEGATION STATUS CONSTANTS - FOR A UMLS PHRASE
  //private static final String DEFINITELY_NEGATED = "NEGATED";
  //private static final String POSSIBLY_NEGATED = "POSSIBLY_NEGATED";
  //private static final String NOT_NEGATED = "NOT_NEGATED";
  private String negexStatus="NOT_NEGATED";
  private String definition =null;
public boolean ishist;
public String num;
    
  //private boolean negationFlag=false;
  //private boolean terminationFlag=false;
  //private boolean possibleNegationFlag = false;
 public GenericConcept(){
	  
  }
  public GenericConcept(String name){
	  conceptName = name;
  }
    
  public GenericConcept(String c, String n, String l, String [] t) {
		cui = c;
		prefName= n;
		conceptName  = l;
		lSemTypes = Arrays.asList(t);
	}
 
  public GenericConcept cloneDeepCopy()
  {
    GenericConcept aNewGC = new GenericConcept();
    if(isUMLSConcept || isOtherConcept)
    {
      aNewGC.addUMLSEntry(conceptName+"|"+cui.toUpperCase()+"|"+prefName+"|" +semanticType);
      aNewGC.lSemTypes.addAll(lSemTypes);
    }
    else if(isNegexPhrase)
    {
      if(isConditionalPossibilityPhrase)
      {
        aNewGC.addNegationEntry(conceptName+"|"+phraseType+"|"+"cond");
      }
      else
      {
        aNewGC.addNegationEntry(conceptName+"|"+phraseType);
      }
    }
   
    else
    {
      aNewGC.addSimpleToken(conceptName);
    }
    return aNewGC;
      
  }
  
  /***********
   * Method to return the first word in a given term. This method is internally
   * used by the <code>TrieHashTable</code> class
   * @return 
   * <p>the first word in a multiworded term
   * <p> the entire word in a single worded term
   */
  public String getFirstWord()
  {
    return firstWord;
  }
  
  /*********************
   * Returns the string form of the concept name
   * @return
   * <p> the string form of the concept name
   */
  public String getConceptName()
  {
    return conceptName;
  }
  
  /*********************
   * Returns the normalized lemma list
   * @return
   * <p> the lemma list
   */
  public String[] getLemmas()
  {
    return lemmas;
  }
  
  /*********************
   * Prints the conceptName to screen. Useful for in debugging
   * 
   */
 // public void print()
 // {
 //   System.out.print(conceptName);
 // }
  /***************************
   * Locally stores the index of the current concept in the tokenized arraylist
   * @param i the index of the token in the tokenized arralyist
   */
  public void setPosition(int i)
  {
    position=i;
  }
  /*****************************
   * Returns the position of the current concept in the tokenized arraylist
   * 
   * @return the position of the current concept in the tokenized arraylist
   */
  public int getPosition()
  {
    return position;
  }
  /******************
   * Returns the number of space delimited tokens in the String form of the concept.
   * 
   * @return the number of tokens
   */
  public int size()
  {
    return size;
  }
  
  /*
  public void setNegationFlag(boolean b)
  {
    negationFlag=b;
  }
   */
  
  /****************
   * Method used by the Negex algorithm implementation to set the negation
   * status for this specific concept
   * 
   * @param s negation value: <code>NEGATED, POSSIBLY_NEGATED, or NOT_NEGATED</code>
   */
  public void setNegexStatus(String s)
   {
     negexStatus=s;
   }
   
   /**
    * Method used to obtain the negation status of a particular concept
    * after applying the negex algorithm.
    * 
    * @return <code>DEFINITELY_NEGATED</code> OR <code>POSSIBLY_NEGATED</code> OR <code>NOT_NEGATED</code>
    */
   public String getNegexStatus()
   {
     return negexStatus;
   }
  
  /*******************
   * Shortcut Method to check if the finding is definitely negated
   * 
   * @return <code> true </code>  if the concept is definitely negated
   * <p> <code> false </code>  otherwise
   */
   public boolean isDefinitelyNegated()
  {
    return negexStatus.equalsIgnoreCase("DEFINITELY_NEGATED");
  }
  /*******************
   * Shortcut Method to check if the finding is "possibly" negated
   * 
   * @return <code> true </code>  if the concept is possibly negated
   * <p> <code> false </code>  otherwise
   */
   public boolean isPossiblyNegated()
  {
    return negexStatus.equalsIgnoreCase("POSSIBLY_NEGATED");
  }
  /*******************
   * Shortcut Method to check if the finding is definitely or possibly negated
   * 
   * @return <code> true </code>  if the concept is definitely or possibly negated
   * <p> <code> false </code>  otherwise
   */
   public boolean isDefinitelyOrPossiblyNegated()
  {
    return isDefinitelyNegated()||isPossiblyNegated();
  }
  
  /**********************
   * Determines if current NegexEntry is a conditional possiblility phrase
   * 
   * @return <code> true </code>  if the concept is a conditional possibility
   * negex phrase
   * <p> <code> false </code>  otherwise
   */
   public boolean isConditionalPossibilityPhrase()
  {
    return isConditionalPossibilityPhrase;
  }
  
  /***********
   * Add a UMLS entry from the lexicon to the hashtable
   * 
   * @param s takes the form <code>conceptName|cui|semanticType</code>
   * <p> E.g. <code>left lower parathyroid gland|C0229594|anatomy</code>
   */
   
   public void addUMLSEntry(String s)
  {
    String[] arr = s.split("[|]");
    conceptName = arr[0];
    cui = arr[1];
    prefName = arr[2];
    semanticType = arr[3];
    lSemTypes.add(arr[3]);
    
    StringTokenizer st = new StringTokenizer(conceptName);
    
    size = st.countTokens();
    if(st.hasMoreTokens())
      firstWord=st.nextToken();
    if(semanticType.equals("expo") || semanticType.equals("prob")){
    	isOtherConcept=true;
    	isUMLSConcept=false;
    	cui = "OTHER";
    }
    else{
    isUMLSConcept=true;
    isOtherConcept=false;
    }
    isSimpleToken=false;
    isNegexPhrase=false;    
  }
   
   
   /***********
    * Add a UMLS entry with lemmas from the lexicon to the hashtable
    * 
    * @param s takes the form <code>conceptName|lemmas|cui|semanticType</code>
    * <p> E.g. <code>left lower parathyroid gland|C0229594|anatomy</code>
    */
    
    public void addNormalizedUMLSEntry(String s)
   {
     String[] arr = s.split("[|]");
     conceptName = arr[0];
     lemmas = arr[1].split("[ ]+");
     cui = arr[2];
     prefName = arr[3];
     semanticType = arr[4];
     lSemTypes.add(semanticType);
     
     size = lemmas.length;
     
//     StringTokenizer st = new StringTokenizer(conceptName);
//     if(st.hasMoreTokens())
//       firstWord=st.nextToken();
     if (lemmas.length > 0) firstWord = lemmas[0];
     if(semanticType.equals("expo") || semanticType.equals("prob")){
     	isOtherConcept=true;
     	isUMLSConcept=false;
     	cui = "OTHER";
     }
     else{
     isUMLSConcept=true;
     isOtherConcept=false;
     }
     isSimpleToken=false;
     isNegexPhrase=false;    
   }
    
  /*****************************************************
   * Adds a NegexEntry to the lexicon. 
   * 
   * @param s 
   * can be a string of two forms
   * <p><b>Type A <code>negexPhrase|phraseType</code></b>
   * <p>E.g. 1 <code> no increase|pseudoNegation </code>
   * <p>E.g. 2 <code> however|Termination</code>
   * <p>
   * <p><b>Type B <code>negexPhrase|phraseType|phraseSubType</code></b>
   * <p>E.g. 1 <code>rule the patient out for|preUMLS|cond</code>
   * <p>E.g. 2 <code>ought to be ruled out|postUMLS|cond</code>
   * <p><code>phraseType</code> can be one of the following four Strings:
   * <p><code>preUMLS</code>
   * <p><code>postUMLS</code>
   * <p><code>pseudoNegation</code>
   * <p><code>Termination</code>
   * <p>
   * <p><code>phraseSubType</code> is always <code>cond</code> (for a conditional possibility phrase)
   * 
   * For details see Wendy Chapman's paper on the Negex Algorithm
   * 
   * ***************************************************/
  public void addNegationEntry(String s)
  {
    //System.out.println(s);
      
    String[] arr = s.trim().split("[|]");
    conceptName = arr[0];
    phraseType = arr[1];
    cui = "NEG";
    
    //System.out.println(arr.length);
    
    
    if(phraseType.equalsIgnoreCase("preUMLS"))
    {
      isPreUMLSPhrase = true;
      if(arr.length==3)
      {
        isConditionalPossibilityPhrase=true;
        negexType = arr[1]+"|"+arr[2];
      }
      else
      {
        negexType = arr[1];
      }
    }  
      
    if(phraseType.equalsIgnoreCase("postUMLS"))
    {
      isPostUMLSPhrase = true;
      if(arr.length==3)
      {
        isConditionalPossibilityPhrase=true;
        negexType = arr[1]+"|"+arr[2];
      }
      else
      {
        negexType = arr[1];
      }
    }   
    if(phraseType.equalsIgnoreCase("pseudoNegation"))
    {
      isPseudoNegationPhrase = true;
      negexType = phraseType;
    }
    if(phraseType.equalsIgnoreCase("conj"))
    {
        isConjunction = true;
        negexType = phraseType;
    }
    if(phraseType.equalsIgnoreCase("Termination"))
    {
        isTerminationEntry = true;
        negexType = phraseType;
    }
        
    StringTokenizer st = new StringTokenizer(conceptName," \t\"?.!(){[}]:;,");
    size = st.countTokens();
    if(st.hasMoreTokens())
      firstWord=st.nextToken();
    
    isNegexPhrase=true;
    isUMLSConcept=false;
    isSimpleToken=false;
    
    //System.out.println(s);
    //if(s.trim().equalsIgnoreCase("what must be ruled out is|preumls|cond"))
      //System.out.println(conceptName+"\t"+cui+"\t"+negexType);
  }
  
  /*****************************************************
   * Adds a normalized NegexEntry to the lexicon. The difference
   * between this method and <code>addNegationEntry</code> is that
   * this method expect the lemmas of the negation terms as the second 
   * field of the input string.
   * 
   * ***************************************************/
  public void addNormalizedNegationEntry(String s)
  {
    //System.out.println(s);
      
    String[] arr = s.trim().split("[|]");
    conceptName = arr[0];
    lemmas = arr[1].split("[ ]+");
    phraseType = arr[2];
    cui = "NEG";
    
    //System.out.println(arr.length);
    
    
    if(phraseType.equalsIgnoreCase("preUMLS"))
    {
      isPreUMLSPhrase = true;
      if(arr.length==4)
      {
        isConditionalPossibilityPhrase=true;
        negexType = arr[2]+"|"+arr[3];
      }
      else
      {
        negexType = arr[2];
      }
    }  
      
    if(phraseType.equalsIgnoreCase("postUMLS"))
    {
      isPostUMLSPhrase = true;
      if(arr.length==4)
      {
        isConditionalPossibilityPhrase=true;
        negexType = arr[2]+"|"+arr[3];
      }
      else
      {
        negexType = arr[2];
      }
    }   
    if(phraseType.equalsIgnoreCase("pseudoNegation"))
    {
      isPseudoNegationPhrase = true;
      negexType = phraseType;
    }
    if(phraseType.equalsIgnoreCase("conj"))
    {
        isConjunction = true;
        negexType = phraseType;
    }
    if(phraseType.equalsIgnoreCase("Termination"))
    {
        isTerminationEntry = true;
        negexType = phraseType;
    }
        
    StringTokenizer st = new StringTokenizer(conceptName," \t\"?.!(){[}]:;,");
    size = lemmas.length;
    if(st.hasMoreTokens())
      firstWord=st.nextToken();
    
    isNegexPhrase=true;
    isUMLSConcept=false;
    isSimpleToken=false;
    
    //System.out.println(s);
    //if(s.trim().equalsIgnoreCase("what must be ruled out is|preumls|cond"))
      //System.out.println(conceptName+"\t"+cui+"\t"+negexType);
  }
  
  /*************************************************************************
   * Add a whitespace delimited token
   * 
   * <p>The end user typically never has to use this method. This method is used
   * by the CoderInterface class for performing the tokenization
   * 
   * @param s A whitespace delimited word
   * 
   * ***********************************************************************/
  public void addSimpleToken(String s)
  {
    conceptName = s;
    size=1;
    firstWord = s;
    isSimpleToken=true;
    isNegexPhrase=false;
    isUMLSConcept=false;
    isOtherConcept=false;
    cui = "SIMPLE_WORD";
  }
  
  /************************************************************************
   * Returns a unique identifier associated with a string entry in the lexicon
   * @return The concept unique identifier associated with a string entry in the
   * lexicon.
   * 
   ***********************************************************************/
  public String getCUI()
  {
    return cui;
  }
  
  /**************************
   * Returns the semantic type such as <code>anatomy</code> associated with the 
   * string entry
   * @return the semantic type of this concept
   */
  public String getSemanticType()
  {
    return semanticType;
  }
  
  /**********
   * Shortcut method to check if current negex entry is a preUMLSPhrase
   * @return <code> true</code> if the current entry is a preUMLSPhrase,
   * <code>false</code> otherwise
   */ 
  public boolean isPreUMLSPhrase()
  {
    return isPreUMLSPhrase;
  }
  /**********
   * Shortcut method to check if current negex entry is a postUMLSPhrase
   * @return <code> true</code> if the current entry is a postUMLSPhrase,
   * <code>false</code> otherwise
   */ 
  public boolean isPostUMLSPhrase()
  {
    return isPostUMLSPhrase;
  }
  /**********
   * Shortcut method to check if current negex entry is a pseudoNegtionPhrase
   * @return <code> true</code> if the current entry is a pseudoNegationPhrase,
   * <code>false</code> otherwise
   */ 
  public boolean isPseudoNegationPhrase()
  {
    return isPseudoNegationPhrase;
  }
  /**********
   * Shortcut method to check if current concept is a negex phrase
   * @return <code> true</code> if the current concept is a negex phrase,
   * <code>false</code> otherwise
   */ 
  public boolean isNegexEntry()
  {
    return isNegexPhrase;
  }
  /**********
   * Shortcut method to check if current negex entry is a terminationPhrase
   * @return <code> true</code> if the current entry is a terminationPhrase,
   * <code>false</code> otherwise
   */ 
  public boolean isTerminationEntry()
  {
    return isTerminationEntry;
  }
  /**********
   * Shortcut method to check if current concept is a UMLS concept
   * @return <code> true</code> if the current entry is a UMLS concept,
   * <code>false</code> otherwise
   */ 
  public boolean isUMLSConcept()
  {
    return isUMLSConcept;
  }
  public boolean isOtherConcept()
  {
    return isOtherConcept;
  }
  /**********
   * Shortcut method to check if current concept is a simple word
   * @return <code>true</code> if the current entry is a space
   * delimited word, <code>false</code> otherwise
   */ 
  public boolean isSimpleToken()
  {
    return isSimpleToken;
  }
  
  /*********
   * Method to check the type of the NegexPhrase
   * @return a string form representing negexPhraseType and subType information.
   */
  public String getNegexType()
  {
    return negexType;
  }

public void setOffset(int i) {
	offset = i;
}
  
public int getOffset()
{
  return offset;
}

public void setCUI(String cui2) {
	cui = cui2;
	
}

public void setConceptName(String conceptName2) {
	conceptName = conceptName2;
	
}

public void setLemmas(String[] ls) {
	lemmas = ls;
}

public void setSemanticType(String semanticType2) {
	semanticType = semanticType2;
	
}

public void setSize(int size2) {
	size = size2;
	
}

public void setUMLS(boolean concept) {
	isUMLSConcept = concept;
	
}

public void setFirstWord(String firstWord2) {
	firstWord = firstWord2;
	
}

public String getPrefName() {
	
	return prefName;
}


public int hashCode() {
	
	return cui.hashCode();
}

public String toString() {
	
	return conceptName + " " + prefName + " "+ cui + " "+ semanticType + " "+ isUMLSConcept;
}

public int compareTo(GenericConcept  n) {
	
	return (cui.compareTo(n.cui));
}

public boolean equals(GenericConcept  n) {
	
	return (cui.equals(n.cui));
}

public void setLength(int length2) {
	length = length2;
}

public int getLength() {
	return length;
}

public void setLSemTypes(List<String> lSemTypes2) {
	lSemTypes.addAll(lSemTypes2);
}

public List<String> getLSemTypes() {
	return lSemTypes;
}

public void setSemGroups(String[] semGroups2) {
	semGroups = semGroups2;
}

public String[] getSemGroups() {
	return semGroups;
}
public void setSource(String string) {
	source = string;
	
}

public String getSource() {
	
	return source;
}
public void setDefinition(String string) {
	 definition = string;
	
}
public String getDefinition() {
	
	return definition;
}
}
