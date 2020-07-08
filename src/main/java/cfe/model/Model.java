package cfe.model;

import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

//import org.hibernate.annotations.Index;

/**
 * Base class for CFE data classes that contains the common fields.
 *
 * @author Jim Mullen
 *
 */
// http://stackoverflow.com/questions/4265454/hibernate-jpa-inheritance-mapping-of-abstract-super-classes
@MappedSuperclass
public abstract class Model {
	
	// Map from MS Access field name to MySQL/Hibernate field name
	private static TreeMap<String,String> classFieldMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	protected TreeMap<String, String> fieldMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	private long id;
	private String probeset;         // Natural key
	private String geneCardsSymbol;
	private String geneTitle;
	private String changeInExpressionInTrackedPhene;
	
	static {
		// Fields common to all CFE data tables
		classFieldMap.put("Probeset", "probeset");
		classFieldMap.put("GeneCards Symbol", "geneCardsSymbol");
		classFieldMap.put("Gene Title", "geneTitle");
		classFieldMap.put("Change in expression in tracked phene", "changeInExpressionInTrackedPhene");
	}

	protected Model()
	{
		// Fields common to all CFE data tables
		this.fieldMap.putAll(classFieldMap);
	}
	
	public String getFieldName(String name)	{
		return fieldMap.get(name.trim());		
	}
	
	public static Set<String> getKeys() {
		Set<String> keys = classFieldMap.keySet();
		return keys;
	}

	
	// Getters and setters -------------------------------------------------------------------------

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	// @Index(name="idx_probeset")
	public String getProbeset() {
	    return this.probeset;	
	}
	
	public void setProbeset(String probeset) {
		this.probeset = probeset;
	}
	
	public String getGeneTitle() {
		return geneTitle;
	}

	public void setGeneTitle(String geneTitle) {
		this.geneTitle = geneTitle;
	}

	public String getGeneCardsSymbol() {
		return geneCardsSymbol;
	}

	public void setGeneCardsSymbol(String geneCardsSymbol) {
		if (geneCardsSymbol == null) geneCardsSymbol = "";
		this.geneCardsSymbol = geneCardsSymbol.trim();
	}

	public String getChangeInExpressionInTrackedPhene() {
		return changeInExpressionInTrackedPhene;
	}

	public void setChangeInExpressionInTrackedPhene(String changeInExpressionInTrackedPhene) {
		this.changeInExpressionInTrackedPhene = changeInExpressionInTrackedPhene;
	}

}
