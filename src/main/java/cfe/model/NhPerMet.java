package cfe.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_PER_MET)
public class NhPerMet extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = -5114041039081382214L;
	private String MoleculeId;
	private String PaperSymbol;
	private String MoleculeName;
	private String MoleculeType;
	private String AnimalType;
	private String Treatment;
	private String TypePeripheralTissue;
	private String CChange;
	private String StudyType;
	private String AdditionalInfo;
	private String DescriptiveName;
	private String Author_Date;
	private String SourceInfo;
	private String Title;
	private String AddedBy;
	
	
	public NhPerMet()	{
		super();
		/*
		hm.put("Psychiatric domain","");
		hm.put("Sub domain","");
		hm.put("Relevant disorder","");
		hm.put("Pub Med ID","");
		hm.put("Genecard symbol","");
		 */
		hm.put("Molecule ID","MoleculeId");
		hm.put("Paper Symbol","PaperSymbol");
		hm.put("Molecule","MoleculeName");
		hm.put("Type of Molecule","MoleculeType");
		hm.put("Animal Type","AnimalType");
		hm.put("Treatment","Treatment");
		
		//hm.put("Type of Peripheral tissue","TypePeripheralTissue");
		///hm.put("Change","CChange");
		hm.put("Type of Peripheral tissue","Tissue");
		hm.put("Change","DirectionChange");	
		
		hm.put("Type of Study (pharmacological, transgenic, enviromental)","StudyType");
		hm.put("Additional informations","AdditionalInfo");

		hm.put("Descriptive Name From Author","DescriptiveName");
		hm.put("Author/Date","Author_Date");
		hm.put("Source info","SourceInfo");
		hm.put("Added By","AddedBy");
		// hm.put("Date Added","");

		hm.put("Title of Paper","Title");
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

}
