package cfe.model.prioritization;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.prioritization.Tables.TblNames;

@Entity
@Table(name=TblNames.HU_BRAIN_PROT)
public class HuBrainProt extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = -3621218494517028525L;
	

	private String ProteinId;
	private String PaperSymbol;
	private String ProteinName;
	private String MoleculeType;
	private String BrainTissue_CellLine;
	// Change is reserved word in MySQL
	private String CChange;
	private String NumberOfSubjects;
	private String Males;
	private String Females;
	private String SampleSource;
	private String AdditionalInfo;
	private String DescriptiveName;
	private String AuthorId;
	private String Title;
	private String SourceInfo;
	private String AddedBy;
	private Date  DateAdded;
	
	public HuBrainProt()
	{
		super();
		hm.put("Protein ID","ProteinId");
		hm.put("Paper symbol","PaperSymbol");
		hm.put("Genecard Symbol", "GenecardSymbol"); // *****
		hm.put("Protein name","ProteinName");
		hm.put("Type of Molecule","MoleculeType");
		
		// hm.put("Brain Region/ Cell Line","BrainTissue_CellLine");
		// hm.put("Change","CChange");
		
		hm.put("Brain Region/ Cell Line","Tissue");
		hm.put("Change","DirectionChange");
		
		hm.put("Number of subjects","NumberOfSubjects");
		hm.put("Males","Males");
		hm.put("Females","Females");
		hm.put("Sample source(ex: Stanley)","SampleSource");
		hm.put("Additional information","AdditionalInfo");
		hm.put("Descriptive Name from Author","DescriptiveName");
		hm.put("Author ID","AuthorId");
		hm.put("Title","Title");
		hm.put("Source info (TABLES,FULL TEXT, ABSTRACTS OR SUPPLEMENTARY)","SourceInfo");
		hm.put("Added By","AddedBy");
	//	hm.put("Date added","DateAdded");

	}
	public String getProteinId() {
		return ProteinId;
	}
	public void setProteinId(String proteinId) {
		ProteinId = proteinId;
	}
	public String getPaperSymbol() {
		return PaperSymbol;
	}
	public void setPaperSymbol(String paperSymbol) {
		PaperSymbol = paperSymbol;
	}
	public String getProteinName() {
		return ProteinName;
	}
	public void setProteinName(String proteinName) {
		ProteinName = proteinName;
	}
	public String getMoleculeType() {
		return MoleculeType;
	}
	public void setMoleculeType(String moleculeType) {
		MoleculeType = moleculeType;
	}
	public String getBrainTissue_CellLine() {
		return BrainTissue_CellLine;
	}
	public void setBrainTissue_CellLine(String brainTissue_CellLine) {
		BrainTissue_CellLine = brainTissue_CellLine;
	}
	public String getCChange() {
		return CChange;
	}
	public void setCChange(String cChange) {
		CChange = cChange;
	}
	public String getNumberOfSubjects() {
		return NumberOfSubjects;
	}
	public void setNumberOfSubjects(String numberOfSubjects) {
		NumberOfSubjects = numberOfSubjects;
	}
	public String getMales() {
		return Males;
	}
	public void setMales(String males) {
		Males = males;
	}
	public String getFemales() {
		return Females;
	}
	public void setFemales(String females) {
		Females = females;
	}
	public String getSampleSource() {
		return SampleSource;
	}
	public void setSampleSource(String sampleSource) {
		SampleSource = sampleSource;
	}
	public String getAdditionalInfo() {
		return AdditionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		AdditionalInfo = additionalInfo;
	}
	public String getDescriptiveName() {
		return DescriptiveName;
	}
	public void setDescriptiveName(String descriptiveName) {
		DescriptiveName = descriptiveName;
	}
	public String getAuthorId() {
		return AuthorId;
	}
	public void setAuthorId(String authorId) {
		AuthorId = authorId;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getSourceInfo() {
		return SourceInfo;
	}
	public void setSourceInfo(String sourceInfo) {
		SourceInfo = sourceInfo;
	}
	public String getAddedBy() {
		return AddedBy;
	}
	public void setAddedBy(String addedBy) {
		AddedBy = addedBy;
	}
	public Date getDateAdded() {
		return DateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		DateAdded = dateAdded;
	}	
	
/*
	public static String getFieldName(String name)	{
		
		if (hm.isEmpty())	{
			
			initCommonFields();
			hm.put("Protein ID","ProteinId");
			hm.put("Paper symbol","PaperSymbol");
			hm.put("Protein name","ProteinName");
			hm.put("Type of Molecule","MoleculeType");
			hm.put("Brain Region/ Cell Line","BrainTissue_CellLine");
			hm.put("Change","CChange");
			hm.put("Number of subjects","NumberOfSubjects");
			hm.put("Males","Males");
			hm.put("Females","Females");
			hm.put("Sample source(ex: Stanley)","SampleSource");
			hm.put("Additional information","AdditionalInfo");
			hm.put("Descriptive Name from Author","DescriptiveName");
			hm.put("Author ID","AuthorId");
			hm.put("Title","Title");
			hm.put("Source info (TABLES,FULL TEXT, ABSTRACTS OR SUPPLEMENTARY)","SourceInfo");
			hm.put("Added By","AddedBy");
			hm.put("Date added","DateAdded");
		}		
		return hm.get(name.trim());		
	}
	*/
}