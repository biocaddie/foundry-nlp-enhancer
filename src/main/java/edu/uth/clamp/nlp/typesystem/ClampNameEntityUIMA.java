

/* First created by JCasGen Tue Apr 28 11:51:10 CDT 2015 */
package edu.uth.clamp.nlp.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Jun 16 10:50:02 CDT 2015
 * XML source: C:/Users/jwang16/git/clampnlp/src/main/resources/edu/uth/clamp/nlp/structure/TypeSystem.xml
 * @generated */
public class ClampNameEntityUIMA extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ClampNameEntityUIMA.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ClampNameEntityUIMA() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ClampNameEntityUIMA(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ClampNameEntityUIMA(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ClampNameEntityUIMA(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: attribute

  /** getter for attribute - gets attributes in json
   * @generated
   * @return value of the feature 
   */
  public String getAttribute() {
    if (ClampNameEntityUIMA_Type.featOkTst && ((ClampNameEntityUIMA_Type)jcasType).casFeat_attribute == null)
      jcasType.jcas.throwFeatMissing("attribute", "edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClampNameEntityUIMA_Type)jcasType).casFeatCode_attribute);}
    
  /** setter for attribute - sets attributes in json 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAttribute(String v) {
    if (ClampNameEntityUIMA_Type.featOkTst && ((ClampNameEntityUIMA_Type)jcasType).casFeat_attribute == null)
      jcasType.jcas.throwFeatMissing("attribute", "edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClampNameEntityUIMA_Type)jcasType).casFeatCode_attribute, v);}    
  }

    