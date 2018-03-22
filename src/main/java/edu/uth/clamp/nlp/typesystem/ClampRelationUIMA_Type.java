
/* First created by JCasGen Tue Jun 16 10:45:20 CDT 2015 */
package edu.uth.clamp.nlp.typesystem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.cas.TOP_Type;

/** Clamp relationships
 * Updated by JCasGen Tue Jun 16 10:50:02 CDT 2015
 * @generated */
public class ClampRelationUIMA_Type extends TOP_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (ClampRelationUIMA_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = ClampRelationUIMA_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new ClampRelationUIMA(addr, ClampRelationUIMA_Type.this);
  			   ClampRelationUIMA_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new ClampRelationUIMA(addr, ClampRelationUIMA_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = ClampRelationUIMA.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
 
  /** @generated */
  final Feature casFeat_entFrom;
  /** @generated */
  final int     casFeatCode_entFrom;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getEntFrom(int addr) {
        if (featOkTst && casFeat_entFrom == null)
      jcas.throwFeatMissing("entFrom", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    return ll_cas.ll_getRefValue(addr, casFeatCode_entFrom);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEntFrom(int addr, int v) {
        if (featOkTst && casFeat_entFrom == null)
      jcas.throwFeatMissing("entFrom", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    ll_cas.ll_setRefValue(addr, casFeatCode_entFrom, v);}
    
  
 
  /** @generated */
  final Feature casFeat_entTo;
  /** @generated */
  final int     casFeatCode_entTo;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getEntTo(int addr) {
        if (featOkTst && casFeat_entTo == null)
      jcas.throwFeatMissing("entTo", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    return ll_cas.ll_getRefValue(addr, casFeatCode_entTo);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setEntTo(int addr, int v) {
        if (featOkTst && casFeat_entTo == null)
      jcas.throwFeatMissing("entTo", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    ll_cas.ll_setRefValue(addr, casFeatCode_entTo, v);}
    
  
 
  /** @generated */
  final Feature casFeat_attribute;
  /** @generated */
  final int     casFeatCode_attribute;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAttribute(int addr) {
        if (featOkTst && casFeat_attribute == null)
      jcas.throwFeatMissing("attribute", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    return ll_cas.ll_getStringValue(addr, casFeatCode_attribute);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAttribute(int addr, String v) {
        if (featOkTst && casFeat_attribute == null)
      jcas.throwFeatMissing("attribute", "edu.uth.clamp.nlp.typesystem.ClampRelationUIMA");
    ll_cas.ll_setStringValue(addr, casFeatCode_attribute, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public ClampRelationUIMA_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_entFrom = jcas.getRequiredFeatureDE(casType, "entFrom", "edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA", featOkTst);
    casFeatCode_entFrom  = (null == casFeat_entFrom) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_entFrom).getCode();

 
    casFeat_entTo = jcas.getRequiredFeatureDE(casType, "entTo", "edu.uth.clamp.nlp.typesystem.ClampNameEntityUIMA", featOkTst);
    casFeatCode_entTo  = (null == casFeat_entTo) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_entTo).getCode();

 
    casFeat_attribute = jcas.getRequiredFeatureDE(casType, "attribute", "uima.cas.String", featOkTst);
    casFeatCode_attribute  = (null == casFeat_attribute) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_attribute).getCode();

  }
}



    