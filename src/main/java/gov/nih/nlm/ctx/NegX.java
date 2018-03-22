package gov.nih.nlm.ctx;

//import java.util.ArrayList;
import java.util.List;
import java.util.Map;



/**
 * @author ddemner
 */
public class NegX {
	 public NegX(List<GenericConcept> arr, Map<String, String> mS)
	  {
	    /************************************************************************
	     *                                  ALGORITHM:
	     ************************************************************************
	     * (1) Go to first negation phrase in sentence
	     * (2) If phrase is a pseudo-negation phrase,
	     *        (2.1) Skip to next negation phrase
	     * (3) Determine scope of negation phrase
	     * (4) If termination phrase within scope,
	     *        (4.1) Terminate scope before termination phrase
	     * (5) Negate all indexed clinical concepts within scope. 
	     *  
	     ************************************************************************/
	 
	      for(int i=0;i<arr.size();i++)
	      {
	        GenericConcept aConcept = arr.get(i);
	        //if(aConcept.getClass().equals("SimpleToken")) continue;
	        //if(aConcept.getClass().equals("Concept")) continue;
	        //aConcept.print();
	        if(aConcept.isNegexEntry())
	        {
	          //System.out.println("Negation PHrase found");

	          if(aConcept.isPseudoNegationPhrase())
	          {
	            continue;
	          }
	          if(aConcept.isPreUMLSPhrase())
	          {
	            //System.out.println("PreUMLS PHrase found");
	            int scope=i+1;
	            
	            
	            while((scope<i+6)&&(scope<arr.size()))
	            {
	              if( (arr.get(scope).isTerminationEntry())
	                  ||(arr.get(scope).isPreUMLSPhrase())
	                 )
	              {
	                break;
	              }
	              scope++;
	              //System.out.println("scope is: "+scope+" and arr.size is "+arr.size());
	            }
	            
	            //System.out.println(i);
	            //System.out.println("scope is "+scope);
	            //System.out.println(arr.size());
	            //System.out.println("i is: "+i+"["+arr.get(i).getConceptName()+"]");
	            //System.out.println("scope is: "+scope+"["+arr.get(scope).getConceptName()+"]");
	            scope--;
	            for(int j=i;j<=scope;j++)
	            {
	              //System.out.println("j is: "+j);
	              if(arr.get(j).isUMLSConcept())
	              {
	                GenericConcept umlsConceptConsideredForNegation = arr.get(j);
	                if(aConcept.isConditionalPossibilityPhrase())
	                {
	                  umlsConceptConsideredForNegation.setNegexStatus("POSSIBLY_NEGATED");
	                }
	                else
	                {
	                  umlsConceptConsideredForNegation.setNegexStatus("DEFINITELY_NEGATED");
	                  //umlsConceptConsideredForNegation.setNegationFlag(true);
	                }
	                arr.set(j,umlsConceptConsideredForNegation);
	                if(mS!=null)
	                mS.put(umlsConceptConsideredForNegation.getCUI().trim().toUpperCase(), umlsConceptConsideredForNegation.getNegexStatus());
	              }
	            }
	            
	            if(arr.get(scope).isTerminationEntry())
	            {
	              i=scope+1;
	            }
	            else
	            {
	              i=scope;
	            }
	            continue;
	          }
	          if(aConcept.isPostUMLSPhrase())
	          {
	            int scope=i-5;
	            if(scope<0) scope=0;
	            
	            while(scope<i)
	            {
	              if(arr.get(scope).isUMLSConcept())
	              {
	                GenericConcept umlsConceptConsideredForNegation = arr.get(scope);
	                if(aConcept.isConditionalPossibilityPhrase())
	                {
	                  umlsConceptConsideredForNegation.setNegexStatus("POSSIBLY_NEGATED");
	                }
	                else
	                {
	                  umlsConceptConsideredForNegation.setNegexStatus("DEFINITELY_NEGATED");
	                  //umlsConceptConsideredForNegation.setNegationFlag(true);
	                }
	                arr.set(scope,umlsConceptConsideredForNegation);
	                if(mS!=null)
		                mS.put(umlsConceptConsideredForNegation.getCUI().trim().toUpperCase(), umlsConceptConsideredForNegation.getNegexStatus());
	              }
	              scope++;
	            }//end while
	            continue;
	          }//end if
	          
	        }//end if
	        
	      }//end for
	  
	  }
	  

}
