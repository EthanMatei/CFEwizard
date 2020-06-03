package cfe.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_GENE_LINKAGE)
public class NhGeneLinkage extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = 1985092974366226889L;
	private String QTL;
	private String AnimalType;
	private String PValue;
	private String Chromosome;
	private String StudyType;
	private String AdditionalInfo;
	private String DescriptiveName;
	private String Author_Date;	
	private String Title;
	private String SourceInfo;
	private String AddedBy;
	private String LODScore;
	private String QTLLocation;
	
	
	public NhGeneLinkage()	{
		super();
		/*
		hm.put("Psychiatric domain","");
		hm.put("Sub domain","");
		hm.put("Relevant disorder","");
		hm.put("Pub Med ID","");
		 */

		hm.put("QTL","QTL");
		hm.put("Animal type","AnimalType");
		hm.put("P-value","PValue");
		hm.put("Chromosome","Chromosome");
		hm.put("Type of Study","StudyType");
		hm.put("Additional Information","");
		hm.put("Descriptive Name from Author","DescriptiveName");
		hm.put("Author/Date","Author_Date");
		hm.put("Source info","SourceInfo");
		hm.put("Added By","AddedBy");
		// hm.put("Date Added","");
		hm.put("Location of QTL from paper","QTLLocation");		
		hm.put("Title of Paper","Title");
		hm.put("LOD score","LODScore");
	}


	public String getQTL() {
		return QTL;
	}


	public void setQTL(String qTL) {
		QTL = qTL;
	}


	public String getAnimalType() {
		return AnimalType;
	}


	public void setAnimalType(String animalType) {
		AnimalType = animalType;
	}


	public String getPValue() {
		return PValue;
	}


	public void setPValue(String pValue) {
		PValue = pValue;
	}


	public String getChromosome() {
		return Chromosome;
	}


	public void setChromosome(String chromosome) {
		Chromosome = chromosome;
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


	public String getLODScore() {
		return LODScore;
	}


	public void setLODScore(String lODScore) {
		LODScore = lODScore;
	}


	public String getQTLLocation() {
		return QTLLocation;
	}


	public void setQTLLocation(String qTLLocation) {
		QTLLocation = qTLLocation;
	}
}
