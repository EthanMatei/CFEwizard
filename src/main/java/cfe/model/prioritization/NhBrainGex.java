package cfe.model.prioritization;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.prioritization.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_BRAIN_GEX)
public class NhBrainGex extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = -5456133659510133670L;
	
	private String PaperSymbol;
	private String GeneName; 
	private String AnimalType;
	private String Treatment;
	private String BrainTissue_CellLine;
	
	// Change is reserved work in MySql
	private String CChange;	
	private String StudyType;
	private String DescriptiveName;
	private String Author_Date;	
	private String Title;
	private String SourceInfo;
	private String AddedBy; 
	private Date  DateAdded;
	
	public NhBrainGex(){
		super();
		hm.put("Paper Symbol","PaperSymbol");
		hm.put("Gene Name","GeneName");
		hm.put("Animal Type","AnimalType");
		hm.put("Treatment","Treatment");
		
		//hm.put("Brain tissue/cell line","BrainTissue_CellLine");
		// hm.put("Change","CChange");
		
		hm.put("Brain tissue/cell line","Tissue");
		hm.put("Change","DirectionChange");
		
		hm.put("Type of Study (pharmacological, transgenic, enviromental)","StudyType");
		hm.put("Descriptive Name From Author","DescriptiveName");
	
		hm.put("Author/Date","Author_Date");
		hm.put("Title of Paper","Title");
		hm.put("Source info (Table#, Abstract, Supplementary, full text)","SourceInfo");
		hm.put("Added By","AddedBy");
		// hm.put("Date Entered","DateAdded"); **

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
	public Date getDateAdded() {
		return DateAdded;
	}
	public void setDateAdded(Date dateAdded) {
		DateAdded = dateAdded;
	} 
		
	public String getFieldName(String name)	{
		/*
		if (hm.isEmpty())	{
			initCommonFields();
			hm.put("Paper Symbol","PaperSymbol");
			hm.put("Gene Name","GeneName");
			hm.put("Animal Type","AnimalType");
			hm.put("Treatment","Treatment");
			hm.put("Brain tissue/cell line","BrainTissue_CellLine");
			hm.put("Change","CChange");
			hm.put("Type of Study (pharmacological, transgenic, enviromental)","StudyType");
			hm.put("Descriptive Name From Author","DescriptiveName");
		
			hm.put("Author/Date","Author_Date");
			hm.put("Title of Paper","Title");
			hm.put("Source info (Table#, Abstract, Supplementary, full text)","SourceInfo");
			hm.put("Added By","AddedBy");
			hm.put("Date Entered","DateAdded");
		}
		*/
		return hm.get(name.trim());		
	}
}