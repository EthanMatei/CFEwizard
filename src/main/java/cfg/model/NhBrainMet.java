package cfg.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfg.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_BRAIN_MET)
public class NhBrainMet extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = 7098971991747007105L;
	private String MoleculeId;
	private String PaperSymbol;
	private String MoleculeName;
	private String MoleculeType;
	private String AnimalType;
	private String Treatment;
	private String BrainTissue_CellLine;
	private String StudyType;
	private String AdditionalInfo;
	private String DescriptiveName;
	private String Author_Date;
	private String SourceInfo;
	private String Title;
	private String AddedBy;
	private Date  DateAdded;
	
	// Change is reserved word in MySQL
	private String CChange;

	
	public NhBrainMet()	{
		super();
		hm.put("Molecule ID","MoleculeId");

		hm.put("Paper Symbol","PaperSymbol");
		hm.put("Molecule name","MoleculeName");
		hm.put("Type of Molecule","MoleculeType");
		hm.put("Animal Type","AnimalType");
		hm.put("Treatment","Treatment");
		
		// hm.put("Brain Tissue/ Cell Line","BrainTissue_CellLine");
		// hm.put("Change","CChange");
		
		hm.put("Brain Tissue/ Cell Line","Tissue");
		hm.put("Change","DirectionChange");
		
		hm.put("Type of Study (pharmacological, transgenic, enviromental)","StudyType");
		hm.put("Additional informations","AdditionalInfo");

		hm.put("Descriptive Name From Author","DescriptiveName");
		hm.put("Author ID/Date","Author_Date");
		hm.put("Source info","SourceInfo");
		hm.put("Title","Title");
		hm.put("Added By","AddedBy");
		//hm.put("Date Added","DateAdded");
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


	public String getAnimalType() {
		return AnimalType;
	}


	public void setAnimalType(String animalType) {
		AnimalType = animalType;
	}


	public String getTreatment() {
		return Treatment;
	}


	public void setTreatment(String treatment) {
		Treatment = treatment;
	}


	public String getBrainTissue_CellLine() {
		return BrainTissue_CellLine;
	}


	public void setBrainTissue_CellLine(String brainTissue_CellLine) {
		BrainTissue_CellLine = brainTissue_CellLine;
	}


	public String getStudyType() {
		return StudyType;
	}


	public void setStudyType(String studyType) {
		StudyType = studyType;
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


	public String getAuthor_Date() {
		return Author_Date;
	}


	public void setAuthor_Date(String author_Date) {
		Author_Date = author_Date;
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


	public Date getDateAdded() {
		return DateAdded;
	}


	public void setDateAdded(Date dateAdded) {
		DateAdded = dateAdded;
	}


	public String getCChange() {
		return CChange;
	}


	public void setCChange(String cChange) {
		CChange = cChange;
	}	
}
