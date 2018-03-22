package gov.nih.nlm.ctx;

public class DictionaryDefinition {

	final private String name;
	final private String fileName;
	final private boolean normalize;
	final private boolean negationDic;
	
	public DictionaryDefinition(String name, String fileName, boolean normalize, boolean negationDic) {
		this.name = name;
		this.fileName = fileName;
		this.normalize = normalize;
		this.negationDic = negationDic;
	}

	public String getName() {
		return name;
	}

	public String getFileName() {
		return fileName;
	}

	public boolean useNormalized() {
		return normalize;
	}

	public boolean isNegationDic() {
		return negationDic;
	}
}
