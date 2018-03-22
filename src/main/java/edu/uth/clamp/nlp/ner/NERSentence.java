package edu.uth.clamp.nlp.ner;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.apache.ctakes.typesystem.type.syntax.Chunk;

import opennlp.tools.util.Span;
import edu.uth.clamp.nlp.structure.ClampSentence;
import edu.uth.clamp.nlp.structure.ClampToken;
import edu.uth.clamp.nlp.structure.XmiUtil;

public class NERSentence {
	Logger logger = Logger.getLogger( NERSentence.class.getName() );
	public static final String LABELO = "O";
	public static final String LABELB = "B-";
	public static final String LABELI = "I-";
	public int sent_offset= 0; //add by junxu  
	
	
	String sentStr = "";
	String sentTokenStr = "";
	String section = "__NIL__";
	Vector<Span>   spans  = new Vector<Span>();
	Vector<String> tokens = new Vector<String>();
	Vector<String> posVec = new Vector<String>();
	Vector<NERInstance> instances = new Vector<NERInstance>();
	Vector<NERFeatureExtractor> extractors = new Vector<NERFeatureExtractor>();
	
	public NERSentence( String sentStr, Span[] tokens, String[] pos, String[] bios, String section ) {
		this.sentStr = sentStr;
		assert( tokens.length == pos.length );
		assert( bios == null || bios.length == tokens.length );
		for( int i = 0; i < tokens.length; i++ ) {
			this.spans.add( tokens[i] );
			this.tokens.add( sentStr.substring( tokens[i].getStart(), tokens[i].getEnd() ) );
			this.posVec.add( pos[i] );
			sentTokenStr += tokens[i] + " ";
			NERInstance ins = new NERInstance();
			if( bios != null ) {
				ins.setLabel( bios[i] );
			}
			instances.add( ins );
		}
		sentTokenStr = sentTokenStr.trim();
		this.section = section;
		if( section == null || section.isEmpty() ) {
			this.section = "__NIL__";
		}
	}
	
	public String getOrignialSentTxt()
	{
		return sentStr;
	}
	
	public void setSection(String sec)
	{
		this.section=sec;
	}
	
	public int getTokenIndexStart(int start)
	{		
		int idx=0;
		int offset = start+this.sent_offset;
		for(Span s:spans)
		{			
			if(s.getStart()==offset)
				return idx;
			idx+=1;
		}
		return -1;
	}
	
	public int getTokenIndexEnd(int end)
	{		
		int idx=0;
		int offset = end+this.sent_offset;
		for(Span s:spans)
		{			
			if(s.getEnd()==offset)
				return idx;
			idx+=1;
		}
		return -1;
	}
	
	
	// TODO: remove the clamp data structure, keep it as simple as possible;
	public NERSentence( ClampSentence sent ) {
		sentStr = sent.textStr();
	    
		this.sent_offset=sent.getBegin(); // add by jun, get the offset of the sent
	    
		for( ClampToken token : XmiUtil.selectToken( sent.getJCas(), sent.getBegin(), sent.getEnd() ) ) {
			String word = token.textStr();
			String pos = token.getPartOfSpeech();
			if( pos == null ) {
				logger.warning( "PartOfSpeech of a token is null. token=[" + word + "]" );
			}
			tokens.add( word );
			spans.add(new Span(token.getBegin(),token.getEnd()));//jun
			posVec.add( pos );
			NERInstance ins = new NERInstance();
			ins.setLabel( token.getBIO() );
			instances.add( ins );
			sentTokenStr += word + " ";
		}
		sentTokenStr = sentTokenStr.trim();
		section = sent.getSectionHeader();
	}

	public NERSentence( String sentStr, Span[] tokens, String[] pos, String[] bios ) {
		this.sentStr = sentStr;
		assert( tokens.length == pos.length );
		assert( bios == null || bios.length == tokens.length );
		for( int i = 0; i < tokens.length; i++ ) {
			this.spans.add( tokens[i] );
			this.tokens.add( sentStr.substring( tokens[i].getStart(), tokens[i].getEnd() ) );
			this.posVec.add( pos[i] );
			sentTokenStr += tokens[i] + " ";
			NERInstance ins = new NERInstance();
			if( bios != null ) {
				ins.setLabel( bios[i] );
			}
			instances.add( ins );
		}
		sentTokenStr = sentTokenStr.trim();
	}
	
	
	public int extract() {
		for( NERFeatureExtractor extractor : extractors ) {
			extractor.extract( this );
		}
		return 0;
	}
	
	public int length() {
		assert( tokens.size() == instances.size() );
		assert( tokens.size() == posVec.size() );
		return tokens.size();
	}
	
	public String getToken( int i ) {
		assert( i < tokens.size() );
		return tokens.get( i );
	}
	
	public int getTokenBegin(int i)
	{
		assert( i < spans.size() );
		return spans.get(i).getStart();
	}
	public int getTokenEnd(int i)
	{
		assert( i < spans.size() );
		return spans.get(i).getEnd();
	}
	
	
	public String getPos( int i ) {
		assert( i < posVec.size() );
		return posVec.get( i );
	}
	public void addFeature( int i, NERFeature fea ) {
		instances.get(i).addFeature( fea );
	}
	public void setLabel( int i, String label ) {
		instances.get(i).setLabel(label);
	}
	public void setPrediction( int i, String prediction ) {
		instances.get(i).setPrediction(prediction);
	}
	public void addExtractor( NERFeatureExtractor extractor ) {
		extractors.add( extractor );
	}
	public void setExtractors( Vector<NERFeatureExtractor> extractors ) {
		this.extractors = extractors;
	}
	
	public NESpan[] getGoldSpan() {
		List<NESpan> ret = new ArrayList<NESpan>();
		int i = 0;
		while( i < instances.size() ) {
			String bio = instances.get(i).getLabel();
			if( bio.startsWith( LABELB ) ) {
				String type = bio.substring( LABELB.length() );
				int start = i;
				int end = i;
				for( int j = i + 1; j < instances.size(); j++ ) {
					if( instances.get(j).getLabel().equals( LABELI + type ) ) {
						end = j;
					} else {
						i = j;
						break;
					}
				}
				NESpan span = new NESpan( spans.get(start).getStart(), spans.get(end).getEnd(), type );
				ret.add( span );
			} else {
				i++;
			}
		}
		return ret.toArray( new NESpan[ ret.size() ] );
	}

	public NESpan[] getPredictionSpan() {
		List<NESpan> ret = new ArrayList<NESpan>();
		int i = 0;
		while( i < instances.size() ) {
			String bio = instances.get(i).getPrediction();
			if( bio.startsWith( LABELB ) ) {
				String type = bio.substring( LABELB.length() );
				int start = i;
				int end = i;
				for( int j = i + 1; j < instances.size(); j++ ) {
					if( instances.get(j).getPrediction().equals( LABELI + type ) ) {
						end = j;
					} else {
						i = j;
						break;
					}
				}
				NESpan span = new NESpan( spans.get(start).getStart(), spans.get(end).getEnd(), type );
				ret.add( span );
				//System.out.println( "Predict:" + span.start() + " " + span.end() + " " + sentStr.substring( span.start(), span.end() ) );
			}
			i++;
		}
		return ret.toArray( new NESpan[ ret.size() ] );
	}
	
	public String dump() {
		String ret = "";
		for( NERInstance ins : instances ) {
			ret += ins.dump() + NERInstance.LINEEND;
		}
		return ret;
	}
	
}
