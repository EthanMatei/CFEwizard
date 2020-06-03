package cfe.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.HU_BRAIN_GEX)
public class HuBrainGex extends Model implements ModelInterface, Serializable {
	
	private static final long serialVersionUID = 3063001087206669093L;
	
	/**
	 * The convention is the first letter to be lower case
	 * We are writing like this so the the dynamic invocation creates setAccesionNumber instead of
	 * setaccessNumber (for example).
	 */
	public HuBrainGex()	{
		//initCommonFields();
		super();
		hm.put("Accession Number","AccessionNumber");
		hm.put("Paper Symbol","PaperSymbol");
		hm.put("Gene Name","GeneName");
		
		// hm.put("Brain Region/ cell line","BrainRegion");
		// hm.put("Change","CChange");
	
		hm.put("Brain Region/ Cell Line","Tissue");
		hm.put("Change","DirectionChange");
		
		hm.put("Number of subjects","NumberOfSubjects");
		hm.put("Males","Males");
		hm.put("Females","Females");
		hm.put("Sample Source (Ex Stanley)","SampleSource");
		hm.put("Additional information","AdditionalInfo");
		//hm.put("flagged studies","FlaggedStudies");
		hm.put("Descriptive Name From Author","DescriptiveName");
		hm.put("Author ID","AuthorId");
		hm.put("Title","Title");
		hm.put("Source info (TABLE#,ABSTRACT,FULL TEXT, SUPPLEMENTARY)","SourceInfo");
		hm.put("Added By","AddedBy");
		//hm.put("Date added","DateAdded");
		hm.put("Field20","Comments");
		// New
		hm.put("Source info", "SourceInfo");
		hm.put("Author/Date", "AuthorId");
		hm.put("Title of Paper", "Title");
		hm.put("flagged studies", "FlaggedStudies");
		
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
			/*			
		if (hm.isEmpty())	{
			initCommonFields();
			hm.put("Accession Number","AccessionNumber");
			hm.put("Paper Symbol","PaperSymbol");
			hm.put("Gene Name","GeneName");
			hm.put("Brain Region/ cell line","BrainRegion");
			hm.put("Change","CChange");
			hm.put("Number of subjects","NumberOfSubjects");
			hm.put("Males","Males");
			hm.put("Females","Females");
			hm.put("Sample Source (Ex Stanley)","SampleSource");
			hm.put("Additional information","AdditionalInfo");
			hm.put("flagged studies","FlaggedStudies");
			hm.put("Descriptive Name From Author","DescriptiveName");
			hm.put("Author ID","AuthorId");
			hm.put("Title","Title");
			hm.put("Source info (TABLE#,ABSTRACT,FULL TEXT, SUPPLEMENTARY)","SourceInfo");
			hm.put("Added By","AddedBy");
			hm.put("Date added","DateAdded");
			hm.put("Field20","Comments");
		}
		*/
		return hm.get(name.trim());		
	}
}