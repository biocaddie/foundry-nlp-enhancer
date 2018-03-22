# NLP processing program

## Prerequisites

### For running

* Java 1.7 JRE

### For Development

* Java 1.7 JDK
* Maven 

Getting the code
----------------

    cd $HOME
    git clone https://github.com/<username>/foundry-nlp-enhancer.git
    cd $HOME/foundry-nlp-enhancer

After that expand the UMLS2015 lookup table (which is to big for Github in uncompressed format) locally

    gzip -d $HOME/foundry-nlp-enhancer/src/main/resources/edu/uth/clamp/nlp/ner/UMLS2015.txt.gz

Building
--------



1. The NER code is in edu.uth.biocaddie.ner package in NLP_NER.java.

2. To run the program, you need to have NER supporting data and CLAMP resource files in the system.

3. Change the HomeDir in Config.java to the path where you store the supporting data.

4. Put the CLAMP resource folder under src/main/resources/.

5. The function to process the NLP is "NLP_process_json_files" in NLP_NER.java.

6. Two things can be set in the Config.java. 
   1) To disable cache use in MetamapLite, set MetaMapCache=false but it will reduce the process speed. You can also change the maximum cache size for the MetamapLite.
   2) You can decide to use the terminology server in the process or not by setting SynonymsFlag.
       If SynonymsFlag=true, Config.TerminologyServer should point to the terminology server IP.
       IF SynonymsFlag=true, four more fields will be added to the results (MeshTerm synonyms, disease synonyms, chemical synonyms, gene synonyms).

6. An example of input and output are in the "input_example" and "output" folder.

7. You can use main function in NLP_NER.java to run a test case.
