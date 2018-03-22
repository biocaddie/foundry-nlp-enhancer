package edu.uth.clamp.nlp.ner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChemicalRelatedFeature implements NERFeatureExtractor {
	static public final ChemicalRelatedFeature INSTANCE = new ChemicalRelatedFeature();

	private static String GREEK = "(alpha|beta|gamma|delta|epsilon|zeta|eta|theta|iota|kappa|lambda|mu|nu|xi|omicron|pi|rho|sigma|tau|upsilon|phi|chi|psi|omega)";
	protected static String ElementAbbrs = "(H|He|Li|Be|B|C|N|O|F|Ne|Na|Mg|Al|Si|P|S|Cl|Ar|K|Ca|Sc|Ti|V|Cr|Mn|Fe|Co|Ni|Cu|Zn|Ga|Ge|As|Se|Br|Kr|Rb|Sr|Y|Zr"
			+ "|Nb|Mo|Tc|Ru|Rh|Pd|Ag|Cd|In|Sn|SbTe|I|Xe|Cs|Ba|La|Ce|Pr|Nd|Pm|Sm|Eu|Gd|Tb|Dy|Ho|Er|Tm|Yb|Lu|Hf|Ta|W|Re|Os|Ir|Pt|Au|Hg|Tl|Pb|Bi|Po|At|Rn"
			+ "|Fr|Ra|Ac|Th|Pa|U|Np|Pu|Am|Cm|Bk|Cf)";

	protected static String Elements = "(hydrogen|helium|lithium|beryllium|boron|carbon|nitrogen|oxygen|fluorine|neon|sodium|magnesium"
			+ "|aluminium|silicon|phosphorus|sulfur|chlorine|argon|potassium|calcium|scandium|titanium|vanadium|chromium|manganese|iron"
			+ "|cobalt|nickel|copper|zinc|gallium|germanium|arsenic|selenium|bromine|krypton|rubidium|strontium|yttrium|zirconium|niobium"
			+ "|molybdenum|technetium|ruthenium|rhodium|palladium|silver|cadmium|indium|tin|antimony|tellurium|iodine|xenon|caesium|barium"
			+ "|lanthanum|cerium|praseodymium|neodymium|promethium|samarium|europium|gadolinium|terbium|dysprosium|holmium|erbium|thulium|ytterbium"
			+ "|lutetium|hafnium|tantalum|tungsten|rhenium|osmium|iridium|platinum|gold|mercury|thallium|lead|bismuth|polonium|astatine|radon"
			+ "|francium|radium|actinium|thorium|protactinium|uranium|neptunium|plutonium|americium|curium|berkelium|californium|einsteinium"
			+ "|fermium|mendelevium|nobelium|lawrencium|rutherfordium|dubnium|seaborgium|bohrium|hassium|meitnerium|darmstadtium|roentgenium"
			+ "|copernicium|ununtrium|flerovium|ununpentium|livermorium|ununseptium|ununoctium)";
	protected static String AminoAcidLong = "(Alanine|Arginine|Asparagine|Aspartic|Cysteine|Glutamine|Glutamic|Glycine|Histidine|Isoleucine"
			+ "|Leucine|Lysine|Methionine|Phenylalanine|Proline|Serine|Threonine|Tryptophan|Tyrosine|Valine)";
	protected static String AminoAcidMed = "(Ala|Arg|Asn|Asp|Cys|Gln|Glu|Gly|His|Ile|Leu|Lys|Met|Phe|Pro|Ser|Thr|Trp|Tyr|Val|Asx|Glx)";
	protected static String AminoAcidShort = "(A|R|N|D|C|Q|E|G|H|I|L|K|M|F|P|S|T|W|Y|V|B|Z)";

	protected static String CHEMalkaneStem = ".*(meth|eth|prop|tetracos).*";
	protected static String CHEMsimpleMultiplier = ".*(di|tri|tetra).*";
	protected static String CHEMtrivialRing = ".*(benzen|pyridin|toluen).*";
	protected static String CHEMinLineSuffix = ".+(yl|ylidyne|oyl|sulfonyl)";
	protected static String CHEMsuffix1 = ".+(one|ol|carboxylic|amide|ate|acid|ium|ylium|ide|uide|iran|olan|inan|pyrid|acrid|amid|keten|formazan|fydrazin)";
	protected static String CHEMsuffix2 = ".+(vir|cillin|mab|olol|tidine|pine|done|sone|nitrate|ximab|zumab|nib|vastatin|prazole|lukast|grel|axine|oxetine|sartan|oxacin|conazole"
			+ "|cillin|cyclin|navir|tryptan|ane|cane|azine|barbital|azelam|zosin|sin|ipramine|etine|oxacin|kinase|place)";

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

	private void extractMultiTokens(NERSentence sent) {
		Pattern pattern = Pattern.compile("\\b" + AminoAcidMed + "-("
				+ AminoAcidMed + "-)*" + AminoAcidMed + "\\b");
		String sentenceText = sent.getOrignialSentTxt();
		Matcher textMatcher = pattern.matcher(sentenceText);
		while (textMatcher.find()) {
			int start = textMatcher.start();
			int end = textMatcher.end();
			// String matchText = sentenceText.substring(start, end);
			// System.out.println("\tPATTERN FOUND: " + sentence.getDocumentId()
			// + "|" + start + "|" + end + "|" + matchText);
			int tagstart = sent.getTokenIndexStart(start);
			int tagend = sent.getTokenIndexEnd(end);
			if (tagstart < 0 || tagend < 0) {
				System.out.println("WARNING: Pattern ignored");
			} else {
				for (int i = tagstart; i <= tagend; i++) {
					sent.addFeature(i, new NERFeature("AMINO_STRING", "YES"));
					// System.out.println("\t\tMarking token TRUE: " +
					// tokens.get(i).getText());
				}
			}
		}

	}

	public synchronized int extract(NERSentence sent, int index) {

		String t = sent.getToken(index);
		String isgreek = getRegexMatches(t, "YES",
				Pattern.compile(GREEK, Pattern.CASE_INSENSITIVE));
		// Case sensitivity has been considered in the below
		String isElementAbbr = getRegexMatches(t, "YES",
				Pattern.compile(ElementAbbrs));
		String isMoleculepart = getRegexMatches(t, "YES",
				Pattern.compile(ElementAbbrs + "+"));
		// Full formulas are multi-token with the fine tokenization
		String isElementName = getRegexMatches(t, "YES",
				Pattern.compile(Elements));
		// Do not need strings of amino acids?
		String isAminoMed = getRegexMatches(t, "YES",
				Pattern.compile(AminoAcidMed, Pattern.CASE_INSENSITIVE));
		String isAminoShort = getRegexMatches(t, "YES",
				Pattern.compile(AminoAcidShort));
		
		sent.addFeature(index, new NERFeature("ISGREEK", isgreek));
		sent.addFeature(index, new NERFeature("ISELABBR", isElementAbbr));
		sent.addFeature(index, new NERFeature("ISELEMET", isElementName));
		sent.addFeature(index,
				new NERFeature("ISMOLECULE_PART", isMoleculepart));
		sent.addFeature(index, new NERFeature("ISAMINO_MED", isAminoMed));
		sent.addFeature(index, new NERFeature("ISAMINO_SHORT", isAminoShort));

		
		String isCHEMalkaneStem = getRegexMatches(t, "YES",
				Pattern.compile(CHEMalkaneStem));
		sent.addFeature(index, new NERFeature("_CHEMALKANESTEM_", isCHEMalkaneStem));
		String isCHEMsimpleMultiplier = getRegexMatches(t, "YES",
				Pattern.compile(CHEMsimpleMultiplier));
		sent.addFeature(index, new NERFeature("_CHEMMULTIPLIER_", isCHEMsimpleMultiplier));
		String isCHEMtrivialRing = getRegexMatches(t, "YES",
				Pattern.compile(CHEMtrivialRing));
		sent.addFeature(index, new NERFeature("_CHEMMTRIVALRING_", isCHEMtrivialRing));
		String isCHEMinLineSuffix = getRegexMatches(t, "YES",
				Pattern.compile(CHEMinLineSuffix));
		sent.addFeature(index, new NERFeature("_CHEMLINESUFFIX_", isCHEMinLineSuffix));
		String isCHEMsuffix1 = getRegexMatches(t, "YES",
				Pattern.compile(CHEMsuffix1));
		sent.addFeature(index, new NERFeature("_CHEMSUFFIX1_", isCHEMsuffix1));
		String isCHEMsuffix2 = getRegexMatches(t, "YES",
				Pattern.compile(CHEMsuffix2));
		sent.addFeature(index, new NERFeature("_CHEMSUFFIX2_", isCHEMsuffix2));

		
		return 0;
	}
}
