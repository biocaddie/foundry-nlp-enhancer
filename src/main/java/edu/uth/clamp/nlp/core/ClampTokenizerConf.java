package edu.uth.clamp.nlp.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import edu.uth.clamp.nlp.configurable.NLPProcessorConf;
import edu.uth.clamp.nlp.configurable.param.BooleanParam;
import edu.uth.clamp.nlp.configurable.param.FileParam;
import edu.uth.clamp.nlp.configurable.param.FolderParam;
import edu.uth.clamp.nlp.configurable.param.IntegerParam;
import edu.uth.clamp.nlp.configurable.param.Param;
import edu.uth.clamp.nlp.configurable.param.RuleParam;
import edu.uth.clamp.nlp.configurable.param.StringParam;
import edu.uth.clamp.nlp.structure.DocProcessor;
import edu.uth.clamp.nlp.uima.TokenizerUIMA;
import edu.uth.clamp.nlp.util.ClampConstants;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

public class ClampTokenizerConf extends NLPProcessorConf {
	public FileParam confFile;
	
	final String defaultConfFile = "defaultTokenRule.conf";

	public ClampTokenizerConf() {
		super( ClampConstants.Tokenizer, ClampConstants.ClampTokenizer );
		requireComp( ClampConstants.SentDetector );
		confFile = new FileParam( "Rule file",
				defaultConfFile,
				defaultConfFile,
				"This file contains all the delimeters and rules which is used by Clamp Tokenizer." );
	}

	@Override
	public Tokenizer create() {
		try {
			ClampTokenizer tokenizer = new ClampTokenizer();
			InputStream instream = null;
			if( confFile.getValueStr().equals( defaultConfFile ) ) {
				instream = ClampTokenizerConf.class.getResourceAsStream( defaultConfFile );
			} else {
				instream = new FileInputStream( confFile.value() );
			}
			tokenizer.init(instream);
			return tokenizer;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
