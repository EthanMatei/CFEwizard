package cfe.model.prioritization;

import java.util.Set;
import java.util.TreeMap;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

//import org.hibernate.annotations.Index;

// http://stackoverflow.com/questions/4265454/hibernate-jpa-inheritance-mapping-of-abstract-super-classes
@MappedSuperclass
abstract class Model {
	
	protected TreeMap<String, String> hm = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	private long ID;
	private String PsychiatricDomain;
	private String SubDomain;
	private String RelevantDisorder;
	private String GenecardSymbol; // Old GeneSymbol;
	private double PubMedID;
	
	// Extra last minute stuff
	// See notes in MSAccessParser.java 
	private String Tissue;
	private String DirectionChange;

	protected Model()
	{
		initCommonFields();
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}
	@Column(length=255)
	public String getPsychiatricDomain() {
		return PsychiatricDomain;
	}

	public void setPsychiatricDomain(String psychiatricDomain) {
		if (psychiatricDomain == null) psychiatricDomain = "";
		PsychiatricDomain = psychiatricDomain.trim();
	}
	@Column(length=150)
	public String getSubDomain() {
		return SubDomain;
	}

	public void setSubDomain(String subDomain) {
		if (subDomain == null) subDomain = "";
		SubDomain = subDomain.trim();
	}
	@Column(length=255)
	public String getRelevantDisorder() {
		return RelevantDisorder;
	}

	public void setRelevantDisorder(String relevantDisorder) {
		if (relevantDisorder == null) relevantDisorder = "";
		RelevantDisorder = relevantDisorder.trim();
	}

	// @Index(name="idx_gcs")
	public String getGenecardSymbol() {
		return GenecardSymbol;
	}

	public void setGenecardSymbol(String genecardSymbol) {
		if (genecardSymbol == null) genecardSymbol = "";
		GenecardSymbol = genecardSymbol.trim();
	}
	
	public double getPubMedID() {
		return PubMedID;
	}

	public void setPubMedID(double pubMedID) {
		PubMedID = pubMedID;
	}
	
	public void setPubMedID(String pubMedID) {
		if (pubMedID == null || pubMedID.length() < 1) pubMedID = "0";
		this.PubMedID = Double.parseDouble(pubMedID);
	}
	private void initCommonFields()	{

		/**
		 * 	Psychiatric domain
			Sub domain
			Relevant disorder
			Genecard symbol
			Pub Med ID
		 */
		hm.put("Psychiatric domain","PsychiatricDomain");
		hm.put("Sub-domain","SubDomain");
		hm.put("Relevant disorder","RelevantDisorder");
		hm.put("Genecard symbol","GenecardSymbol");
		hm.put("Pub Med ID","PubMedID"); 
		
		// Should not be necessary
		hm.put("Psychiatric Domain","PsychiatricDomain");
		hm.put("Sub-domain","SubDomain");
		hm.put("Relevant Disorder","RelevantDisorder");
		hm.put("Genecard Symbol","GenecardSymbol");
		// Change
		hm.put("Sub domain", "SubDomain");
		hm.put("sub domain", "SubDomain");
	}

	// Must be public so that reflection can find it
	public Boolean validate (String name) {
		
		return Boolean.valueOf(hm.containsKey(name));
	}
	
	public String getFieldName(String name)	{
		return hm.get(name.trim());		
	}
	
	// Did not like the name getkeys()
	public Set<String> retrieveKeys()
	{
		return hm.keySet();
	}
	
	public Boolean validColumns(Set<String> columns) {
		
		StringBuffer str = new StringBuffer(100);
		
		Boolean b = Boolean.TRUE;
		
		for (String key : hm.keySet()) {
			
			if (columns.contains(key) == false) {
				
				str.append(key).append(", ");
				
				b = Boolean.FALSE;
				
			}
		}
		
		return  b; //Boolean.valueOf(columns.containsAll(hm.keySet()));
	}
	public String getTissue() {
		return Tissue;
	}
	public void setTissue(String tissue) {
		Tissue = tissue;
	}
	public String getDirectionChange() {
		return DirectionChange;
	}
	public void setDirectionChange(String directionChange) {
		DirectionChange = directionChange;
	}
}
