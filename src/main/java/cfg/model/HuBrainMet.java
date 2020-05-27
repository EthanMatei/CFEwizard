package cfg.model;

// No Pub Med ID

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import cfg.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.HU_BRAIN_MET)
public class HuBrainMet extends Model implements ModelInterface, Serializable {
	
	private static final long serialVersionUID = 4534716580057393776L;
	
	private String MoleculeId;
	private String PaperSymbol;
	private String MoleculeName;
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
	private String SourceInfo;
	private String Title;
	private String AddedBy;
	private Date  DateAdded;
	
	public HuBrainMet() {
		super();
		hm.put("Molecule ID","MoleculeId");
		hm.put("Paper symbol","PaperSymbol");
		hm.put("Molecule name","MoleculeName");
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
		hm.put("Source info (TABLES,FULL TEXT, ABSTRACTS OR SUPPLEMENTARY)","SourceInfo");
		hm.put("Title","Title");
		hm.put("Added By","AddedBy");
		//hm.put("Date added","DateAdded");
	}
	
	public String getMoleculeId() {
		return MoleculeId;
	}
	public void setMoleculeId(String moleculeId) {
		MoleculeId = moleculeId;
	}
	public String getPaperSymbol() {
		return PaperSymbol;
	}
	public void setPaperSymbol(String paperSymbol) {
		PaperSymbol = paperSymbol;
	}
	public String getMoleculeName() {
		return MoleculeName;
	}
	public void setMoleculeName(String moleculeName) {
		MoleculeName = moleculeName;
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
	public String getSourceInfo() {
		return SourceInfo;
	}
	public void setSourceInfo(String sourceInfo) {
		SourceInfo = sourceInfo;
	}
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getAddedBy() {
		return AddedBy;
	}
	public void setAddedBy(String addedBy) {
		AddedBy = addedBy;
	}
	
	public String getFieldName(String name)	{
		/*
		if (hm.isEmpty())	{
			
			initCommonFields();
			hm.put("Molecule ID","MoleculeId");
			hm.put("Paper symbol","PaperSymbol");
			hm.put("Molecule name","MoleculeName");
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
			hm.put("Source info (TABLES,FULL TEXT, ABSTRACTS OR SUPPLEMENTARY)","SourceInfo");
			hm.put("Title","Title");
			hm.put("Added By","AddedBy");
			hm.put("Date added","DateAdded");
		}	
		*/	
		return hm.get(name.trim());		
	}
	public Date getDateAdded() {
		return DateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		DateAdded = dateAdded;
	}
}