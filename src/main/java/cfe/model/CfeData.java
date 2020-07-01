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
abstract class CfeData {
	
	// Map from MS Access field name to MySQL/Hibernate field name
	protected TreeMap<String, String> fieldMap = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);

	private long id;
	private String probeset;         // Natural key
	private String geneCardsSymbol;
	private String changeInExpressionInTrackedPhene;

	protected CfeData()
	{
		// Fields common to all CFE data tables
		fieldMap.put("Probeset", "probeset");
		fieldMap.put("GeneCards Symbol", "geneCardsSymbol");
		fieldMap.put("Gene Title", "geneTitle");
		fieldMap.put("Change in expression in tracked phene", "changeInExpressionInTrackedPhene");
	}
	
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
	
	public String getGenesCardsSymbol() {
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
