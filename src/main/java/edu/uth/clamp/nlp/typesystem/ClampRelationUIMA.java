

/* First created by JCasGen Tue Jun 16 10:45:20 CDT 2015 */
package edu.uth.clamp.nlp.typesystem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.TOP;


/** Clamp relationships
 * Updated by JCasGen Tue Jun 16 10:50:02 CDT 2015
 * XML source: C:/Users/jwang16/git/clampnlp/src/main/resources/edu/uth/clamp/nlp/structure/TypeSystem.xml
 * @generated */
public class ClampRelationUIMA extends TOP {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ClampRelationUIMA.class);
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
  protected ClampRelationUIMA() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ClampRelationUIMA(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ClampRelationUIMA(JCas jcas) {
    super(jcas);
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
  //* Feature: entFrom

  /** getter for entFrom - gets 
   * @generated
   * @return value of the feature 
   */
  public ClampNameEntityUIMA getEntFrom() {
    if (ClampRelationUIMA_Type.featOkTst && ((ClampRelationUIMA_Type)jcasType).casFeat_entFrom == null)
      jcasType.jcas.throwFeatMissing("entFrom", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    return (ClampNameEntityUIMA)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ClampRelationUIMA_Type)jcasType).casFeatCode_entFrom)));}
    
  /** setter for entFrom - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEntFrom(ClampNameEntityUIMA v) {
    if (ClampRelationUIMA_Type.featOkTst && ((ClampRelationUIMA_Type)jcasType).casFeat_entFrom == null)
      jcasType.jcas.throwFeatMissing("entFrom", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    jcasType.ll_cas.ll_setRefValue(addr, ((ClampRelationUIMA_Type)jcasType).casFeatCode_entFrom, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: entTo

  /** getter for entTo - gets 
   * @generated
   * @return value of the feature 
   */
  public ClampNameEntityUIMA getEntTo() {
    if (ClampRelationUIMA_Type.featOkTst && ((ClampRelationUIMA_Type)jcasType).casFeat_entTo == null)
      jcasType.jcas.throwFeatMissing("entTo", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    return (ClampNameEntityUIMA)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ClampRelationUIMA_Type)jcasType).casFeatCode_entTo)));}
    
  /** setter for entTo - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEntTo(ClampNameEntityUIMA v) {
    if (ClampRelationUIMA_Type.featOkTst && ((ClampRelationUIMA_Type)jcasType).casFeat_entTo == null)
      jcasType.jcas.throwFeatMissing("entTo", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    jcasType.ll_cas.ll_setRefValue(addr, ((ClampRelationUIMA_Type)jcasType).casFeatCode_entTo, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: attribute

  /** getter for attribute - gets attributes in json
   * @generated
   * @return value of the feature 
   */
  public String getAttribute() {
    if (ClampRelationUIMA_Type.featOkTst && ((ClampRelationUIMA_Type)jcasType).casFeat_attribute == null)
      jcasType.jcas.throwFeatMissing("attribute", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ClampRelationUIMA_Type)jcasType).casFeatCode_attribute);}
    
  /** setter for attribute - sets attributes in json 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAttribute(String v) {
    if (ClampRelationUIMA_Type.featOkTst && ((ClampRelationUIMA_Type)jcasType).casFeat_attribute == null)
      jcasType.jcas.throwFeatMissing("attribute", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    jcasType.ll_cas.ll_setStringValue(addr, ((ClampRelationUIMA_Type)jcasType).casFeatCode_attribute, v);}    
  }

    