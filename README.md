# NLP enhancer plugin for Foundry-ES pipeline management system 

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

After that expand the UMLS2015 lookup table (which is too big for Github in uncompressed format) locally

    gzip -d $HOME/foundry-nlp-enhancer/src/main/resources/edu/uth/clamp/nlp/ner/UMLS2015.txt.gz

Building
--------

First, you need to install local packages in the `lib` directory to your local Maven repository by running

    sh ./install-maven-artifacts.sh

After that run the following to build the Foundry NLP enhancer    

    sh ./build_jar.sh


Deployment
----------

The enhancer plugin jar and third patry dependencies needs to be copied to the Foundry-ES plugin and lib directories.
The Foundry-ES plugin directory is specified in the `config.yml` file under `Foundry-ES/bin` directory. E.g.

    pluginDir: "/var/data/foundry-es/foundry_plugins/plugins"

The corresponding lib directory for the above plugin directory would be `/var/data/foundry-es/foundry_plugins/lib`.

Copy the  `$HOME/foundry-nlp-enhancer/target/NLP_processor_010517-1.0.jar` file to the `pluginDir` specified in the 
`config.yml` file. 


The following third party libraries needs to be under Foundry-ES plugin lib directory;

    bioc-1.0.1.jar
    cleartk-eval-2.0.0.jar
    cleartk-ml-2.0.0.jar
    cleartk-ml-liblinear-2.0.0.jar
    cleartk-ml-libsvm-2.0.0.jar
    cleartk-ml-opennlp-maxent-2.0.0.jar
    cleartk-named-entity-0.6.6.jar
    cleartk-token-0.9.0.jar
    cleartk-type-system-1.2.0.jar
    cleartk-util-0.9.2.jar
    commons-io-2.2.jar
    context-2012.jar
    ctakes-assertion-3.2.1.jar
    ctakes-assertion-res-3.2.1.jar
    ctakes-chunker-3.2.2.jar
    ctakes-chunker-res-3.2.2.jar
    ctakes-constituency-parser-3.2.2.jar
    ctakes-constituency-parser-res-3.2.2.jar
    ctakes-context-tokenizer-3.2.2.jar
    ctakes-core-3.2.2.jar
    ctakes-core-res-3.2.2.jar
    ctakes-dependency-parser-3.2.2.jar
    ctakes-dependency-parser-res-3.2.2.jar
    ctakes-dictionary-lookup-3.2.2.jar
    ctakes-dictionary-lookup-res-3.2.2.jar
    ctakes-lvg-3.2.2.jar
    ctakes-lvg-res-3.2.2.jar
    ctakes-ne-contexts-3.2.2.jar
    ctakes-ne-contexts-res-3.2.2.jar
    ctakes-pos-tagger-3.2.2.jar
    ctakes-pos-tagger-res-3.2.2.jar
    ctakes-relation-extractor-3.2.2.jar
    ctakes-relation-extractor-res-3.2.2.jar
    ctakes-type-system-3.2.0.jar
    ctakes-utils-3.2.2.jar
    dom4j-1.6.1.jar
    ini4j-0.5.2.jar
    json-simple-1.1.1.jar
    jVinci-2.6.0.jar
    jwnl-1.3.3.jar
    liblinear-1.94.jar
    log4j-api-2.1.jar
    log4j-core-2.1.jar
    lucene-analyzers-common-4.10.0.jar
    lucene-core-4.10.0.jar
    lucene-queries-4.10.0.jar
    lucene-queryparser-4.10.0.jar
    lucene-sandbox-4.10.0.jar
    lvg2010dist-0.0.1.jar
    nlp-2.4.C.jar
    opennlp-maxent-3.0.3.jar
    opennlp-tools-1.5.3.jar
    ruta-core-2.2.1.jar
    ruta-core-ext-2.2.1.jar
    ruta-ep-engine-2.2.1.jar
    uimafit-1.4.0.jar
    uimafit-core-2.1.0.jar
    uimaj-adapter-vinci-2.6.0.jar
    uimaj-core-2.6.0.jar
    uimaj-cpe-2.6.0.jar
    uimaj-document-annotation-2.6.0.jar
    uimaj-examples-2.4.0.jar
    uimaj-tools-2.4.0.jar


Foundry-ES pipeline setup
-------------------------

The NLP enhancer can be configured to be used in the pipeline via the `config.yml` file. An example 
configuration is shown below;


```YAML
pluginDir: "/var/data/foundry-es/foundry_plugins/plugins"
database:
    host: "<mongo-db-host>"
    port: 27017
    db: biocaddie
    collection: records
mq:
    brokerURL: "tcp://localhost:61616?wireFormat.maxInactivityDuration=0"

workflow:
    "BioCaddie Workflow":
        - transform
        - nlp

consumers:
    - uuidGen:
         class: org.neuinfo.foundry.consumers.jms.consumers.plugins.DocIDAssigner
         status: id_assigned
    - transform:
         class: org.neuinfo.foundry.consumers.jms.consumers.plugins.TransformationEnhancer
         status: transformed
         addResourceInfo: false
    - nlp:
         class: edu.uth.biocaddie.ner.BiocaddieNLPEnhancer
         status: nlp_enhanced
```

Notes
-----

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
