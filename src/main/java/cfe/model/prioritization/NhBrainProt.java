package cfe.model.prioritization;

// No Pub Med ID
import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.prioritization.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_BRAIN_PROT)
public class NhBrainProt extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = -4942101400381670607L;
	
	private String ProteinId; 				// Protein ID/Molecule ID *
	private String ProteinName;
	private String MoleculeType;
	private String AnimalType;
	private String Treatment;
	private String BrainTissue_CellLine; 
	
	// Change is reserved word in MySQL
	private String CChange;	
	private String StudyType;
	private String AdditionalInfo;
	private String DescriptiveName;
	private String AuthorId;
	private String SourceInfo;
	private String Title;
	private String AddedBy;
	private Date  DateAdded;
	private String PaperSymbol;
	
	public NhBrainProt(){
		super();
		
		hm.put("Protein ID","ProteinId");
		hm.put("Protein name","ProteinName");
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
		hm.put("Author ID","AuthorId");
		hm.put("Source info","SourceInfo");
		hm.put("Title","Title");
		hm.put("Added By","AddedBy");
		//hm.put("date added","DateAdded"); **
		hm.put("Paper symbol","PaperSymbol");
		hm.put("Disorder", "RelevantDisorder"); // <===================
		hm.put("Sub-Domain", "SubDomain"); // <=======
	}

	public String getProteinId() {
		return ProteinId;
	}

	public void setProteinId(String proteinId) {
		ProteinId = proteinId;
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

	public String getCChange() {
		return CChange;
	}

	public void setCChange(String cChange) {
		CChange = cChange;
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

	public Date getDateAdded() {
		return DateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		DateAdded = dateAdded;
	}

	public String getPaperSymbol() {
		return PaperSymbol;
	}

	public void setPaperSymbol(String paperSymbol) {
		PaperSymbol = paperSymbol;
	}

	
	/*
	public static String getFieldName(String name)	{
		
		if (hm.isEmpty())	{

			hm.put("Protein ID/Molecule ID","ProteinId");
			hm.put("Molecule","Molecule");
			hm.put("Alias","Alias");
			hm.put("Type of Molecule","TypeOfMolecule");
			hm.put("GeneSymbol/GeneCard","GeneSymbol");
			hm.put("Brain Tissue/ Cell Line","BrainTissue_CellLine");
			hm.put("Change","CChange");
			hm.put("Abreviation of Change","ChangeAbbreviation");			
			hm.put("Animal Type","AnimalType");
			hm.put("Type of Study (pharmacological, transgenic, enviromental)","StudyType");
			hm.put("Treatment","Treatment");
			hm.put("Additional informations","AdditionalInfo");			
			hm.put("Psychiatric Domain","PsychiatricDomain");			
			hm.put("Treatment Domain (Treatment only)","SubDomain");
			
			hm.put("Disorder","RelevantDisorder");
			hm.put("Descriptive Name From Author","DescriptiveName");
			hm.put("Author ID","AuthorId");
			hm.put("SOURCE INFO","SourceInfo");
			hm.put("Title","Title");
			hm.put("Added By","AddedBy");
			hm.put("DATE ADDDED","DateAdded");	
		}
		
		return hm.get(name.trim());		
	}

*/

}