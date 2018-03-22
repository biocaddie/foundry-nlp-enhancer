package edu.uth.biocaddie.ner;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import edu.uth.clamp.nlp.encoding.UmlsEncoder;
import edu.uth.clamp.nlp.ner.NESpan;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.neuinfo.foundry.common.ingestion.DocumentIngestionService;
import org.neuinfo.foundry.common.ingestion.GridFSService;
import org.neuinfo.foundry.common.util.JSONUtils;
import org.neuinfo.foundry.common.util.LRUCache;
import org.neuinfo.foundry.common.util.Utils;
import org.neuinfo.foundry.consumers.plugin.IPlugin;
import org.neuinfo.foundry.consumers.plugin.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by bozyurt on 8/5/16.
 */
public class BiocaddieNLPEnhancer implements IPlugin {
    GridFSService gridFSService;
    DocumentIngestionService dis;
    BioNERecognizer recognizer;
    BigIntegerDictionary BPdict;
    MetaMapLite metaInstance;
    UmlsEncoder encoder;

    static LRUCache<String, String> geneIDCache = new LRUCache<String, String>(5000);
    public final static String NULL_ID = "___NIL___";
    private final static Logger log = Logger.getLogger(BiocaddieNLPEnhancer.class);

    public void setDocumentIngestionService(DocumentIngestionService documentIngestionService) {
        this.dis = documentIngestionService;
    }

    public void setGridFSService(GridFSService gridFSService) {
        this.gridFSService = gridFSService;
    }

    public void initialize(Map<String, String> map) throws Exception {
        this.recognizer = BioNERecognizer.getDefault();
        this.BPdict = BigIntegerDictionary.getDefault();
        this.metaInstance = MetaMapLite.getDefault();
        this.encoder = new UmlsEncoder(Config.UMLSIndex);
        GeneDictionarySearch.getDefault();
    }

    public Result handle(DBObject docWrapper) {
        try {
            BasicDBObject data = (BasicDBObject) docWrapper.get("Data");
            JSONObject json;

            // DBObject siDBO = (DBObject) docWrapper.get("SourceInfo");
            BasicDBObject trDBO = (BasicDBObject) data.get("transformedRec");
            json = JSONUtils.toJSON(trDBO, false);
            String text = extractText(json);
            if (text != null) {
                System.out.println("text:" + text);
                JSONObject nlpFieldsJson = prepNLPFields(text);
                if (nlpFieldsJson != null) {
                    json.put("NLP_Fields", nlpFieldsJson);
                    trDBO.put("NLP_Fields", JSONUtils.encode(nlpFieldsJson, false));
                    return new Result(docWrapper, Result.Status.OK_WITH_CHANGE);
                }
            }
            return new Result(docWrapper, Result.Status.OK_WITHOUT_CHANGE);
        } catch (Throwable t) {
            log.error("handle", t);
            t.printStackTrace();
            // error in NLP enhancement is not fatal (IBO)
            Result r = new Result(docWrapper, Result.Status.OK_WITHOUT_CHANGE);
            return r;
        }
    }


    private JSONObject prepNLPFields(String text) throws Exception {
        long start = System.currentTimeMillis();
        NESpan[] Ret = recognizer.recognize(text);
        long elapsedTime = System.currentTimeMillis() - start;
        System.out.println("recognize elapsed time (msecs): " + elapsedTime);
        List<String> BP = BigIntegerDictionary.unique_NER_BP(BPdict, text);
        List<String> mesh = MetaMapLite.get_mesh(this.metaInstance, text);
        List<String> Genes = new ArrayList<String>();
        List<String> Disease = new ArrayList<String>();
        List<String> Chemical = new ArrayList<String>();
        List<String> GOIDs = new ArrayList<String>();
        List<String> DiseaseIDs = new ArrayList<String>();
        List<String> ChemicalIDs = new ArrayList<String>();
        List<String> GeneIDs = new ArrayList<String>();
        List<String> Meshterms = new ArrayList<String>();
        List<String> MeshIDs = new ArrayList<String>();
        List<String> CLs = new ArrayList<String>();

        List<String> MeshSyns = new ArrayList<String>();
        List<String> GeneSyns = new ArrayList<String>();
        List<String> DiseaseSyns = new ArrayList<String>();
        List<String> ChemicalSyns = new ArrayList<String>();

        ArrayList<String> oriGenes = new ArrayList<String>();
        Set<String> entityNameSet = new HashSet<String>();
        for (NESpan span : Ret) {
            entityNameSet.add(span.sem());
            // get cell lines
            if (span.sem().equals("CL")) {
                String newCL = text.substring(span.start(), span.end()).toLowerCase();
                if (!CLs.contains(newCL)) {
                    CLs.add(newCL);
                }
            }
            // get gene, geneID, gene synonyms
            if (span.sem().equals("GENE")) {
                String newGene = text.substring(span.start(), span.end()).toLowerCase();
                oriGenes.add(newGene.toLowerCase());
                if (!Genes.contains(newGene.toLowerCase())) {
                    if (!GeneDictionarySearch.Cui_dict.containsKey(newGene
                            .toLowerCase())) {
                        continue;
                    }
                    if (newGene.length() < 3) {
                        continue;
                    }
                    if (newGene.length() == 3
                            && newGene.matches(".*[a-z].*")) {
                        continue;
                    }
                    Genes.add(newGene.toLowerCase());
                    String GeneID = GeneDictionarySearch.Cui_dict
                            .get(newGene.toLowerCase());
                    if (GeneID.length() > 0) {
                        GeneIDs.add(GeneID);
                        if (Config.SynonymsFlag) {
                            ArrayList<String> Genesyn = getSynonyms(GeneID);
                            if (Genesyn != null) {
                                GeneSyns.addAll(Genesyn);
                            }
                        }
                    }
                }
            }
            // get Disease, DiseaseIDs, disease synonyms
            if (span.sem().equals("Disease")) {
                String newDisease = text.substring(span.start(), span.end()).toLowerCase();
                if (!Disease.contains(newDisease)) {
                    if (!post_process_rules(newDisease)) {
                        continue;
                    }
                    if (Genes.contains(newDisease)) {
                        continue;
                    }
                    String diseaseID = encoder.encode(newDisease);
                    if (diseaseID.length() < 1) {
                        continue;
                    }
                    Disease.add(newDisease);
                    DiseaseIDs.add(diseaseID);
                    if (Config.SynonymsFlag) {
                        ArrayList<String> diseasesSyn = getSynonyms(diseaseID);
                        if (diseasesSyn != null) {
                            DiseaseSyns.addAll(diseasesSyn);
                        }
                    }
                }
            }
            // get Chemical, ChemicalIDs, chemical synonyms
            if (span.sem().equals("Chemical")) {
                String newChemical = text.substring(span.start(), span.end());
                if (!Chemical.contains(newChemical.toLowerCase())) {
                    if (!post_process_rules(newChemical)) {
                        continue;
                    }
                    if (newChemical.length() <= 3) {
                        continue;
                    }
                    newChemical = newChemical.toLowerCase();
                    if (newChemical.contains("xxxxx")) {
                        continue;
                    }
                    if (compare(newChemical, oriGenes)) {
                        continue;
                    }
                    if (compare(newChemical, BP)) {
                        continue;
                    }
                    String chemicalID = encoder.encode(newChemical);
                    if (chemicalID.length() < 1) {
                        continue;
                    }
                    Chemical.add(newChemical);
                    ChemicalIDs.add(chemicalID);
                    if (Config.SynonymsFlag) {
                        ArrayList<String> Chemicalsyn = getSynonyms(chemicalID);
                        if (Chemicalsyn != null) {
                            ChemicalSyns.addAll(Chemicalsyn);
                        }

                    }
                }
            }
        }
        // System.out.println(entityNameSet);
        // System.out.println("-----------------");

        for (String word : BP) {
            String GOID = BigIntegerDictionary.convert_to_GOID(word);
            if (GOID != null) {
                GOID.length();
                if (GOID.length() > 0) {
                    GOIDs.add(GOID);
                }
            }
        }
        for (String me : mesh) {
            String[] a = me.split("\\|");
            if (!Meshterms.contains(a[1])) {
                Meshterms.add(a[1].toLowerCase());
                MeshIDs.add(a[0]);
                if (Config.SynonymsFlag) {
                    ArrayList<String> meshsyn = getSynonyms(a[0]);
                    if (meshsyn != null) {
                        MeshSyns.addAll(meshsyn);
                    }
                }
            }
        }

        JSONObject jo = new JSONObject();
        constructJsonArray(jo, "gene", unique(Genes));
        constructJsonArray(jo, "GeneID", unique(GeneIDs));
        constructJsonArray(jo, "BP", unique(BP));
        constructJsonArray(jo, "GOID", unique(GOIDs));
        constructJsonArray(jo, "Chemical", unique(Chemical));
        constructJsonArray(jo, "ChemicalID", unique(ChemicalIDs));
        constructJsonArray(jo, "Disease", unique(Disease));
        constructJsonArray(jo, "DiseaseID", unique(DiseaseIDs));
        constructJsonArray(jo, "Meshterm", unique(Meshterms));
        constructJsonArray(jo, "MeshID", unique(MeshIDs));
        constructJsonArray(jo, "CellLine", unique(CLs));
        if (Config.SynonymsFlag) {
            constructJsonArray(jo, "diseaseSynonyms", DiseaseSyns);
            constructJsonArray(jo, "meshSynonyms", MeshSyns);
            constructJsonArray(jo, "chemicalSynonyms", ChemicalSyns);
            constructJsonArray(jo, "geneSynonyms", GeneSyns);
        }

        return jo;
    }

    public static List<String> unique(List<String> list) {
        if (list.isEmpty()) {
            return list;
        }
        Set<String> uniqSet = new HashSet<String>();
        for (Iterator<String> iter = list.iterator(); iter.hasNext(); ) {
            String item = iter.next();
            if (uniqSet.contains(item)) {
                iter.remove();
            } else {
                uniqSet.add(item);
            }
        }
        return list;
    }

    public static void constructJsonArray(JSONObject jo, String fieldName, List<String> valueList) throws JSONException {
        JSONArray list = new JSONArray();
        for (String value : valueList) {
            list.put(value);
        }
        jo.put(fieldName, list);
    }


    public static String convertGeneID(String str) throws UnsupportedEncodingException {
        String cachedResult = geneIDCache.get(str);
        if (cachedResult != null) {
            return cachedResult;
        }
        str = str.replaceAll("%", " ");
        str = str.replaceAll("#", " ");
        str = URLEncoder.encode(str, "UTF-8");

        String urlString = "http://textmining.ls.manchester.ac.uk:8081/?text=" + str + "&species=9606,10090,7227";
        JavaUrlConnectionReader urlReader = new JavaUrlConnectionReader();
        String result = "";
        try {
            String output = urlReader.getUrlContents(urlString);
            String[] lines = output.split("\n");

            for (String line : lines) {
                String[] words = line.split("\t");
                if (words.length < 8) {
                    continue;
                }
                if (result.length() > 0) {
                    result = result + ";" + words[4];
                } else {
                    result = words[4];
                }
            }
        } catch (Exception e) {
            System.out.println("gene ID problem");
        }
        geneIDCache.put(str, result);
        return result;
    }

    private String extractText(JSONObject trJson) throws JSONException {
        //JSONObject dataItem = trJson.getJSONObject("dataItem");
        JSONObject dataItem = trJson.getJSONObject("dataset");
        if (dataItem == null) {
            return null;
        }
        String title = null;
        String description = null;
        if (dataItem.has("title")) {
            title = dataItem.getString("title");
        }
        if (dataItem.has("description")) {
            description = dataItem.getString("description");
            // do some HTML tag cleanup
            if (description.indexOf('<') != -1) {
                description = description.replaceAll("<[^>]+>", "");
                description = description.replaceAll("&nbsp;", " ");
                description = description.replaceAll("&quot;", " ");
            }

        }
        if (title == null && description == null) {
            return null;
        }
        int len = (title != null ? title.length() : 0) + (description != null ? description.length() + 2 : 0);
        StringBuilder sb = new StringBuilder(len);
        sb.append(title);
        if (description != null) {
            sb.append("\n ").append(description);
        }
        return sb.toString().trim();
    }


    /*
     * post process entities to increase precision
     */
    public static boolean post_process_rules(String entity) {
        // remove more than 4 words
        int wordsNum1 = entity.split(" ").length;
        if (wordsNum1 > 4) {
            return false;
        }
        // remove more than 5 tokens
        int wordsNum = entity.split(" |-|\\*|\\.|\\+").length;
        if (wordsNum > 5) {
            return false;
        }
        // remove too long and less than 2 char
        if (entity.length() > 40 || entity.length() <= 2) {
            return false;
        }
        entity = entity.toLowerCase();
        // remove geo file
        if (entity.matches("gds\\d{2,}") || entity.matches("gsm\\d{2,}")
                || entity.matches("gpl\\d{2,}")) {
            return false;
        }
        // remove in format "ab-ab-ed-de-ed"
        if (entity.matches("[a-z0-9]*-[a-z0-9]*-[a-z0-9]*-[a-z0-9]*-[a-z0-9]*")) {
            return false;
        }
        // remove more than 4 digits
        if (entity.matches(".*\\d{4,}.*")) {
            return false;
        }
        return true;
    }

    /*
     * parse content from terminology server and return synonyms
	 */
    public static ArrayList<String> getSynonyms(String cuisID)
            throws IOException {
        String urlString = Config.TerminologyServer + "umls:_" + cuisID;
        String output;
        try {
            output = getURL(urlString);

            JSONParser parser = new JSONParser();
            Object obj = null;
            if (output != null) {
                obj = parser.parse(output);

                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) obj;
                org.json.simple.JSONArray msg = (org.json.simple.JSONArray) jsonObject.get("nodes");
                // System.out.println(jsonObject.get("nodes").getClass().getName());
                @SuppressWarnings("unchecked")
                Iterator<org.json.simple.JSONObject> iterator = msg.iterator();
                while (iterator.hasNext()) {
                    org.json.simple.JSONObject node = iterator.next();
                    String id = (String) node.get("id");
                    if (id.equals("umls:_" + cuisID)) {
                        @SuppressWarnings("unchecked")
                        ArrayList<String> synonyms = (ArrayList<String>) ((org.json.simple.JSONObject) node
                                .get("meta")).get("synonym");
                        return synonyms;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * get url content from terminology server
	 */
    public static String getURL(String urlString) throws IOException {
        URL oracle = new URL(urlString);
        BufferedReader in = null;
        try {
            URLConnection yc = oracle.openConnection();
            in = new BufferedReader(new InputStreamReader(
                    yc.getInputStream()));
            String inputLine;
            StringBuffer sb = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                sb.append(inputLine);
            return sb.toString();
        } finally {
            Utils.close(in);
        }
    }

    /*
         * test if word is part of element in test_value
         */
    public static boolean compare(String word, List<String> test_value) {
        String[] stems = word.split(" |-|\\+|/|\\(|\\)|\\]|\\[");
        if (test_value == null) {
            return false;
        }
        for (int i = 0; i < test_value.size(); i++) {
            String[] stem2 = test_value.get(i).split(
                    " |-|\\+|/|\\(|\\)|\\]|\\[");
            for (int j = 0; j < stems.length; j++) {
                if (Arrays.asList(stem2).contains(stems[j])) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getPluginName() {
        return "BiocaddieNLPEnhancer";
    }


    public static void main(String[] args) throws Exception {
        String text = "Modafinil alters intrinsic functional connectivity of the right posterior insula: a pharmacological resting state fMRI study\n" +
                " <p>Modafinil is employed for the treatment of narcolepsy and has also been, off-label, used to treat cognitive dysfunction in neuropsychiatric disorders. &nbsp;In a previous study, we have reported that single dose administration of modafinil in healthy young subjects enhances fluid reasoning and affects resting state activity in the Fronto Parietal Control (FPC) and Dorsal Attention (DAN) networks. No changes were found in the Salience Network (SN), a surprising result as the network is involved in the modulation of emotional and fluid reasoning. &nbsp;The insula is crucial hub of the SN and functionally divided in anterior and posterior subregions. Using a seed-based approach, we have now analyzed effects of modafinil on the functional connectivity (FC) of insular subregions. Analysis of FC with resting state fMRI (rs-FMRI) revealed increased FC between the right posterior insula and the putamen, the superior frontal gyrus and the anterior cingulate cortex in the modafinil-treated group.Modafinil is considered a putative cognitive enhancer.";

        BiocaddieNLPEnhancer enhancer = new BiocaddieNLPEnhancer();
        enhancer.initialize(new HashMap<String, String>(3));
        long start = System.currentTimeMillis();
        int NUM_TRIALS = 100;
        String jsonStr = null;

        BufferedReader in = null;
        int count = 0;
        try {
            in = Utils.newUTF8CharSetReader("/home/bozyurt/bin/nlp_enhancer_sample_data.txt");
            String line;
            while ((line = in.readLine()) != null) {
                jsonStr = enhancer.prepNLPFields(line).toString(2);
                JSONObject json = new JSONObject(jsonStr);
                JSONArray genes = json.getJSONArray("gene");
                if (genes.length() > 0) {
                    System.out.println(json.toString(2));
                    System.out.println("==================");
                }
                count++;
            }
        } finally {
            Utils.close(in);
        }
        long elapsedTime = System.currentTimeMillis() - start;
        double avgRunTime = elapsedTime / (double) count;
        System.out.println("Avg elapsed time  (msecs):" + avgRunTime);

        System.out.println(jsonStr);
    }
}
