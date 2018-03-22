package gov.nih.nlm.ctx;

import java.util.ArrayList;
import java.util.HashMap;


/******************
 * A Trie implementation backed by a <code> HashMap</code> instance.
 * <p><b>Note</b>:TrieHashTable is not thread safe as it uses 
 * <code>java.util.HashMap</code> and <code>java.util.ArrayList</code> 
 * internally. If you want it to be thread safe, consider reimplementing 
 * it with <code>java.util.Hashtable</code> and <code>java.util.Vector</code>.
 * @author bashyamv. ddemner
 * 
 
 */
public class TrieHashTable 
{
 static HashMap<String,ArrayList<GenericConcept>> hash;
 public TrieHashTable()
  {
    hash = new HashMap<String,ArrayList<GenericConcept>>();
  }
  
 /************************
  * Returns an ArrayList of concepts (<code>GenericConcept</code>) that begin
  * with the current word
  * @param aWord
  * <p> the word to be looked up
  * @return an ArrayList of concepts (<code>GenericConcept</code>) that begin
  * with the current word
  */
 public ArrayList <GenericConcept> beginsWith(String aWord)
  {
    return hash.get(aWord);
  }
  
 /******************************
  * Adds a new lexical entry into the Trie
  * @param aConcept
  * <p>the <code>GenericConcept</code> instance of a lexical entry
  */ 
 public void put(GenericConcept aConcept)
  {
    String aWord = aConcept.getFirstWord();
    if(hash.containsKey(aWord))
    {
        ArrayList <GenericConcept> arr = hash.get(aWord);
        arr.add(aConcept);
        hash.put(aWord, arr);
    }
    else
    {
        ArrayList <GenericConcept>arr = new ArrayList<GenericConcept>();
        arr.add(aConcept);
        hash.put(aWord, arr);
    }
  }
  
 /*******************
  * Returns the number of keys in the hashMap
  * @return
  * <p> the number of unique first words in the lexicon.
  */ 
 public int size()
  {
      return hash.size();
  }
  
}
