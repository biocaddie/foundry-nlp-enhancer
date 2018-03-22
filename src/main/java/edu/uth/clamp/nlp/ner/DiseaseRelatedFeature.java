package edu.uth.clamp.nlp.ner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiseaseRelatedFeature implements NERFeatureExtractor {
	static public final DiseaseRelatedFeature INSTANCE = new DiseaseRelatedFeature();
  
	protected static String DiagnosticSuffixes = ".+(algia|emia|ia|itis|megaly|oma|osis|pathy|rrhea|rrhage|rrhagia|sclerosis|uria)";
	protected static String ProceduralSuffixes = ".+(centesis|ectomy|gram|graphy|lysis|plasty|scopy|stomy|therapy|tomy)";
	protected static String Preffixes = "(ab|dys|brady|hemo|hyper|hydro)";
	
	public int extract(NERSentence sent) {
		// extractMultiTokens(sent);
		for (int i = 0; i < sent.length(); i++) {
			extract(sent, i);
		}
		return 0;
	}

	public static String getRegexMatches(String text, String prefix,
			Pattern matchingRegex) {
		String ret = prefix;
		Matcher m = matchingRegex.matcher(text);
		if (m.matches())
			return ret;
		return "NO";
	}

	 

	public synchronized int extract(NERSentence sent, int index) {

		String t = sent.getToken(index);
		String isDiagnosticSuffixes = getRegexMatches(t, "YES",
				Pattern.compile(DiagnosticSuffixes));		
		sent.addFeature(index, new NERFeature("DIAGSUFFIX", isDiagnosticSuffixes)); 
		

		String isProceduralSuffixes = getRegexMatches(t, "YES",
				Pattern.compile(ProceduralSuffixes));		
		sent.addFeature(index, new NERFeature("PROCEDURALSUFFIX", isProceduralSuffixes));
		
		String isPreffixes = getRegexMatches(t, "YES",
				Pattern.compile(Preffixes));		
		sent.addFeature(index, new NERFeature("DISEASEPREFIX", isPreffixes));
		
		
		return 0;
	}
}
