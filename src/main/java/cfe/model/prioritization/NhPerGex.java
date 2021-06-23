package cfe.model.prioritization;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.prioritization.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_PER_GEX)
public class NhPerGex extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = 5094649734316966627L;
	
	private String PaperSymbol;
	private String GeneName;
	private String AnimalType;
	private String Treatment;
	private String TypePeripheralTissue; // mode to scoring data
	private String CChange;
	private String StudyType;
	private String DescriptiveName;
	private String Author_Date;
	private String Title;
	private String SourceInfo;
	private String AddedBy;
	//	private Date DateAdded;
	
	public NhPerGex()
	{
		super();
		/*
		 * 		hm.put("Genecard symbol","");
		 * 		hm.put("Psychiatric domain","");
				hm.put("Sub domain","");
				hm.put("Relevant disorder","");
				hm.put("Pub Med ID","");
		 */

		hm.put("Paper Symbol","PaperSymbol");
		hm.put("Gene Name","GeneName");
		hm.put("Animal Type","AnimalType");
		hm.put("Treatment","Treatment");
		
		//hm.put("Type of Peripheral tissue","TypePeripheralTissue");
		//hm.put("Change","CChange");
		
		hm.put("Type of Peripheral tissue","Tissue");
		hm.put("Change","DirectionChange");
		
		hm.put("Type of Study (pharmacological, transgenic, enviromental)","StudyType");

		hm.put("Descriptive Name From Author","DescriptiveName");

		hm.put("Author/Date","Author_Date");
		hm.put("Title of Paper","Title");
		hm.put("Source info","SourceInfo");
		hm.put("Added By","AddedBy");
		// hm.put("Date Added","");
	}

	public String getPaperSymbol() {
		return PaperSymbol;
	}

	public void setPaperSymbol(String paperSymbol) {
		PaperSymbol = paperSymbol;
	}

	public String getGeneName() {
		return GeneName;
	}

	public void setGeneName(String geneName) {
		GeneName = geneName;
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
	

}

	
