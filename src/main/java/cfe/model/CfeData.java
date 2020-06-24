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
	private String probeSet;         // Natural key
	private String geneCardSymbol;
	private String DirectionChange;

	protected CfeData()
	{
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getId() {
		return id;
	}

	public void setID(long id) {
		this.id = id;
	}

	// @Index(name="idx_probeset")
	public String getProbeSet() {
	    return this.probeSet;	
	}
	
	public void setProbeSet(String probeSet) {
		this.probeSet = probeSet;
	}
	
	public String getGeneCardSymbol() {
		return geneCardSymbol;
	}

	public void setGeneCardSymbol(String geneCardSymbol) {
		if (geneCardSymbol == null) geneCardSymbol = "";
		this.geneCardSymbol = geneCardSymbol.trim();
	}

	public String getDirectionChange() {
		return DirectionChange;
	}

	public void setDirectionChange(String directionChange) {
		DirectionChange = directionChange;
	}
}
