package gov.nih.nlm.ctx;


/**
 * @author ddemner
 */
public class Utilities {
	 GenericConcept longestConcept;
	  public Utilities()
	  {
	    //longestConcept = null;  
	    
	  }
	  
	  public void addLongest(GenericConcept t)
	  {
	    
	   // System.out.println("longest concept is"+longestConcept);
	   // System.out.println("TrieHasbable is"+t.getConceptName());
	    if(longestConcept==null)
	    {
	      longestConcept = t;
	    }
	    else if(t.size()>longestConcept.size())
	    {
	   //   System.out.println("Replacing "+longestConcept.getConceptName()+" by "+t);
	      longestConcept = t;
	    }
	      
	    
	  }
	  
	  public GenericConcept getLongest()
	  {
	    return longestConcept;
	  }

}

