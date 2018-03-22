package gov.nih.nlm.nls.metamap.lite;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import bioc.BioCAnnotation;
import bioc.BioCPassage;
import bioc.BioCRelation;
import bioc.BioCSentence;
import bioc.BioCLocation;
import bioc.BioCNode;
import bioc.util.CopyConverter;

import gov.nih.nlm.nls.metamap.lite.types.ConceptInfo;
import gov.nih.nlm.nls.metamap.lite.types.Entity;
import gov.nih.nlm.nls.metamap.lite.types.Ev;
import gov.nih.nlm.nls.utils.StringUtils;

/**
 *
 */

public class EntityConverter extends CopyConverter {

  static NER ner = new NER();
  private int i = 0;
  private int ri = 0;
  private int ci = 0;
  private Map<String,String> cuiAnnotationIdMap = new HashMap<String,String>();

  void addEntity(Entity entity, BioCPassage passage) {
    BioCAnnotation bioCEntity = new BioCAnnotation();
    bioCEntity.setID("E" + Integer.toString(i));
    bioCEntity.setText(entity.getText());
    BioCLocation location = new BioCLocation(entity.getStart(), entity.getLength());
    bioCEntity.addLocation(location);
    bioCEntity.putInfon("type", "entity");
    bioCEntity.putInfon("negated", Boolean.toString(entity.isNegated()));
    passage.addAnnotation(bioCEntity);
      
    for (Ev ev: entity.getEvList()) {
      ConceptInfo conceptInfo = ev.getConceptInfo();
      String cui = conceptInfo.getCUI();
      String conceptAnnotationId;
      // if we already have an annotation refering to this concept
      // then use it's reference, otherwise add concept to the cui ->
      // concept annotation map.
      if (this.cuiAnnotationIdMap.containsKey(cui)) {
	conceptAnnotationId = this.cuiAnnotationIdMap.get(cui);
      } else {
	BioCAnnotation conceptAnnotation = new BioCAnnotation();
	conceptAnnotationId = "EC" + Integer.toString(ci);
	conceptAnnotation.setID(conceptAnnotationId);
	conceptAnnotation.putInfon("KnowledgeSource", "UMLS");
	conceptAnnotation.putInfon("cui", cui);
	conceptAnnotation.putInfon("preferredname", conceptInfo.getPreferredName());
	conceptAnnotation.putInfon("semantictypeset", StringUtils.join(conceptInfo.getSemanticTypeSet(), ","));
	conceptAnnotation.putInfon("sourceset", StringUtils.join(conceptInfo.getSourceSet(), ","));
	this.cuiAnnotationIdMap.put(cui, conceptAnnotation.getID());
	passage.addAnnotation(conceptAnnotation);
	ci++;
      }
      // add relation linking concept to entity
      BioCRelation relation = new BioCRelation();
      relation.setID("R" + Integer.toString(ri));
      BioCNode entityNode = new BioCNode(bioCEntity.getID(),"entity");
      BioCNode conceptNode = new BioCNode(conceptAnnotationId,"concept");
      relation.addNode(entityNode);
      relation.addNode(conceptNode);
      passage.addRelation(relation);
    }
  }

  void addEntity(Entity entity, BioCSentence annotatedSentence) {
    BioCAnnotation bioCEntity = new BioCAnnotation();
    bioCEntity.setID("E" + Integer.toString(i));
    bioCEntity.setText(entity.getText());
    BioCLocation location = new BioCLocation(entity.getStart(), entity.getLength());
    bioCEntity.addLocation(location);
    bioCEntity.putInfon("type", "entity");
    bioCEntity.putInfon("negated", Boolean.toString(entity.isNegated()));
    annotatedSentence.addAnnotation(bioCEntity);
      
    for (Ev ev: entity.getEvList()) {
      ConceptInfo conceptInfo = ev.getConceptInfo();
      String cui = conceptInfo.getCUI();
      String conceptAnnotationId;
      // if we already have an annotation refering to this concept
      // then use it's reference, otherwise add concept to the cui ->
      // concept annotation map.
      if (this.cuiAnnotationIdMap.containsKey(cui)) {
	conceptAnnotationId = this.cuiAnnotationIdMap.get(cui);
      } else {
	BioCAnnotation conceptAnnotation = new BioCAnnotation();
	conceptAnnotationId = "EC" + Integer.toString(ci);
	conceptAnnotation.setID(conceptAnnotationId);
	conceptAnnotation.putInfon("KnowledgeSource", "UMLS");
	conceptAnnotation.putInfon("cui", cui);
	conceptAnnotation.putInfon("preferredname", conceptInfo.getPreferredName());
	conceptAnnotation.putInfon("semantictypeset", StringUtils.join(conceptInfo.getSemanticTypeSet(), ","));
	conceptAnnotation.putInfon("sourceset", StringUtils.join(conceptInfo.getSourceSet(), ","));
	this.cuiAnnotationIdMap.put(cui, conceptAnnotation.getID());
	annotatedSentence.addAnnotation(conceptAnnotation);
	ci++;
      }
      // add relation linking concept to entity
      BioCRelation relation = new BioCRelation();
      relation.setID("R" + Integer.toString(ri));
      BioCNode entityNode = new BioCNode(bioCEntity.getID(),"entity");
      BioCNode conceptNode = new BioCNode(conceptAnnotationId,"concept");
      relation.addNode(entityNode);
      relation.addNode(conceptNode);
      annotatedSentence.addRelation(relation);
    }
  }

  /**
    Modify an {@code BioCPassage} and BioCSentence.
    add annotation for short and long form and add a relation between 
 */
 public BioCPassage getPassage(BioCPassage in) {
   BioCPassage out = new BioCPassage();
   out.setOffset( in.getOffset() );
   //out.setText( in.getText() );
   String passageText = in.getText();
   
   if (passageText != null) {
     List <Entity> infos = ner.findEntities(passageText); 
     for (Entity info : infos){  	    	  
       addEntity(info, out);             
     }
   }
		
   out.setInfons( in.getInfons() );
   for ( BioCSentence sentence : in.getSentences() ) {
     out.addSentence( getSentence(sentence) );
   }
   for (BioCAnnotation note : in.getAnnotations() ) {
     out.addAnnotation( getAnnotation(note) );
   }
   for (BioCRelation rel : in.getRelations() ) {
     out.addRelation( rel );
   }
   
   return out;
 }
	
  /**
   * Copy a {@code BioCSentence}.
   */
  public BioCSentence getSentence(BioCSentence in) {
    BioCSentence out = new BioCSentence();
    out.setOffset( in.getOffset() );
    //out.setText( in.getText() );
    
    String sentenceText = in.getText();
    
    List <Entity> infos = ner.findEntities(sentenceText); 
    
    for (Entity info : infos){  	    	  
      addEntity(info, out);             
    }
    
    out.setInfons( in.getInfons() );
    
    for (BioCAnnotation note : in.getAnnotations() ) {
      out.addAnnotation( getAnnotation(note) );
    }
    for (BioCRelation rel : in.getRelations() ) {
      out.addRelation( rel );
    }
    
    return out;
  } 


  void addEntity(BioCPassage currentPassage, Entity info) {
    BioCAnnotation entity = new BioCAnnotation(); 
    String entityId = "E" + i;
    



  }
  void addEntity(BioCSentence currentSentence, Entity info) {
    BioCAnnotation shortForm = new BioCAnnotation(); 

  }
}
