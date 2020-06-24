package cfe.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.HU_BRAIN_GEX)
public class Discovery extends CfeData implements Serializable {
	
	private static final long serialVersionUID = 3063001087206669093L;
	
	/**
	 * The convention is the first letter to be lower case
	 * We are writing like this so the the dynamic invocation creates setAccesionNumber instead of
	 * setaccessNumber (for example).
	 */
	public Discovery()	{
		//initCommonFields();
		super();
		fieldMap.put("Accession Number","AccessionNumber");
		fieldMap.put("Paper Symbol","PaperSymbol");
		fieldMap.put("Gene Name","GeneName");
		
		// fieldMap.put("Brain Region/ cell line","BrainRegion");
		// fieldMap.put("Change","CChange");
	
		fieldMap.put("Brain Region/ Cell Line","Tissue");
		fieldMap.put("Change","DirectionChange");
		
		fieldMap.put("Number of subjects","NumberOfSubjects");
		fieldMap.put("Males","Males");
		fieldMap.put("Females","Females");
		fieldMap.put("Sample Source (Ex Stanley)","SampleSource");
		fieldMap.put("Additional information","AdditionalInfo");
		//fieldMap.put("flagged studies","FlaggedStudies");
		fieldMap.put("Descriptive Name From Author","DescriptiveName");
		fieldMap.put("Author ID","AuthorId");
		fieldMap.put("Title","Title");
		fieldMap.put("Source info (TABLE#,ABSTRACT,FULL TEXT, SUPPLEMENTARY)","SourceInfo");
		fieldMap.put("Added By","AddedBy");
		//fieldMap.put("Date added","DateAdded");
		fieldMap.put("Field20","Comments");
		// New
		fieldMap.put("Source info", "SourceInfo");
		fieldMap.put("Author/Date", "AuthorId");
		fieldMap.put("Title of Paper", "Title");
		fieldMap.put("flagged studies", "FlaggedStudies");
		
	}
	private String AccessionNumber;
	private String PaperSymbol;
	private String GeneName;
	private String BrainRegion;
	private String CChange;
	private String NumberOfSubjects;
	private String Males;
	private String Females;
	private String SampleSource;
	private String AdditionalInfo;
	private String FlaggedStudies;
	private String DescriptiveName;
	private String AuthorId;
	private String Title;
	private String SourceInfo;
	private String AddedBy;
	private Date  DateAdded;
	private String Comments; // Field20
	
	public String getAccessionNumber() {
		return AccessionNumber;
	}
	public void setAccessionNumber(String accessionNumber) {
		AccessionNumber = accessionNumber;
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
	public String getBrainRegion() {
		return BrainRegion;
	}
	public void setBrainRegion(String brainRegion) {
		BrainRegion = brainRegion;
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
	public String getFlaggedStudies() {
		return FlaggedStudies;
	}
	public void setFlaggedStudies(String flaggedStudies) {
		FlaggedStudies = flaggedStudies;
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
	public String getComments() {
		return Comments;
	}
	public void setComments(String comments) {
		Comments = comments;
	}
	
	public String getFieldName(String name)	{
		return fieldMap.get(name.trim());		
	}
}