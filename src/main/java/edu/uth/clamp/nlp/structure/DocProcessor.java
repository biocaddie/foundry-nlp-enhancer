package edu.uth.clamp.nlp.structure;

import java.io.IOException;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.resource.ResourceAccessException;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.descriptor.ConfigurationParameter;

public abstract class DocProcessor extends org.apache.uima.fit.component.JCasAnnotator_ImplBase {
	public static final String PARAMCONF = "processorConf";
	@ConfigurationParameter( name = PARAMCONF )
	protected String processorConf;

	UimaContext uimaContext;
    /**
     * initialize a processor;
     * 
     * @param aContext      uima configuration;
     * @return
     */
    public void initialize(UimaContext aContext)
			throws ResourceInitializationException {

		super.initialize(aContext);
		uimaContext = aContext;

		try {
			configInit();
			loadResource();
		} catch (Exception ace) {
			throw new ResourceInitializationException(ace);
		}
	}

	/**
	 * Reads configuration parameters.
	 * 
	 * @throws ResourceAccessException
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	public void configInit() throws ResourceAccessException, IOException{
		processorConf = (String) uimaContext
				.getConfigParameterValue( PARAMCONF );
	}
    
    /**
     * load required resources, eg. dictionaries, wordlist, configurations..
     * @return 0 if succ; -1 if errors;
     */
    public abstract int loadResource();
    
	public int process( Document doc ){
		try {
			this.process( doc.getJCas() );
		} catch( AnalysisEngineProcessException e ) {
			e.printStackTrace();			
		}
		return 0;		
	}
	
	public String getDesc() {
        return this.getClass().getName();
    }
}
