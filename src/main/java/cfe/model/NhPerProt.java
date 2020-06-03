package cfe.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_PER_PROT)
public class NhPerProt extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = 5197161948275305295L;
	
	private String ProteinId;
	private String PaperSymbol;
	private String ProteinName;
	private String MoleculeType;
	private String AnimalType;
	private String Treatment;
	private String TypePeripheralTissue;
	private String CChange;
	private String AdditionalInfo;
	private String StudyType;
	private String DescriptiveName;
	private String Author_Date;
	private String SourceInfo;
	private String Title;
	private String AddedBy;
	
	public NhPerProt(){
		/*
		 * 	hm.put("Genecard symbol","");
		 * 	hm.put("Psychiatric domain","");
			hm.put("Sub domain","");
			hm.put("Relevant disorder","");
			hm.put("Pub Med ID","");
		 */
		super();
		hm.put("Protein ID","ProteinId");
		hm.put("Paper Symbol","PaperSymbol");
		hm.put("Protein name","ProteinName");
		hm.put("Type of Molecule","MoleculeType");
		hm.put("Animal Type","AnimalType");
		hm.put("Treatment","Treatment");
		
		//hm.put("Type of Peripheral tissue","TypePeripheralTissue");
		//hm.put("Change","CChange");
		
		hm.put("Type of Peripheral tissue","Tissue");
		hm.put("Change","DirectionChange");
		
		hm.put("Additional information","AdditionalInfo");
		hm.put("Type of Study (pharmacological, enviromental, transgenic)","StudyType");
		hm.put("Descriptive Name From Author","DescriptiveName");
		hm.put("Author/Date","Author_Date");
		hm.put("Title of Paper","Title");
		hm.put("Source info","SourceInfo");
		hm.put("Added By","AddedBy");
		// hm.put("Date added","");
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

	public String getTypePeripheralTissue() {
		return TypePeripheralTissue;
	}

	public void setTypePeripheralTissue(String typePeripheralTissue) {
		TypePeripheralTissue = typePeripheralTissue;
	}

	public String getCChange() {
		return CChange;
	}

	public void setCChange(String cChange) {
		CChange = cChange;
	}

	public String getAdditionalInfo() {
		return AdditionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		AdditionalInfo = additionalInfo;
	}

	public String getStudyType() {
		return StudyType;
	}

	public void setStudyType(String studyType) {
		StudyType = studyType;
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
	
}