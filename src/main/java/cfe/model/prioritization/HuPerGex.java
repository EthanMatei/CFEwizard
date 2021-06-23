package cfe.model.prioritization;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.prioritization.Tables.TblNames;

@Entity
@Table(name=TblNames.HU_PER_GEX)
// HU-PER(GENE)
public class HuPerGex extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = -2079567414526911136L;
	
	private String AccessionNumber;
	private String PaperSymbol;
	private String GeneName;
	private String TypePeripheralTissue;
	
	// Change is reserved word in MySql
	private String CChange;
	private String NumberOfSubjects;
	private String Males;
	private String Females;
	private String AdditionalInfo;
	private String DescriptiveName;
	private String AuthorId;
	private String Title;
	private String SourceInfo;	
	private String AddedBy;
	private Date  DateAdded;
	
	public HuPerGex()
	{
		super();
		hm.put("Accession Number","AccessionNumber");
		hm.put("Paper Symbol","PaperSymbol");
		
		hm.put("Gene Name","GeneName");
		
		// hm.put("Type of peripheral tissue","TypePeripheralTissue");
		// hm.put("Change","CChange");
		
		hm.put("Type of peripheral tissue","Tissue");
		hm.put("Change","DirectionChange");
		
		hm.put("Number of subjects","NumberOfSubjects");
		hm.put("Males","Males");
		hm.put("Females","Females");
		hm.put("Additional Information","AdditionalInfo");
		//hm.put("Descriptive Name From Author","DescriptiveName");
		hm.put("Author ID","AuthorId");
		hm.put("Title of Paper","Title");
		hm.put("SOURCE INFO (TABLE#, ABSTRACT,FULL TEXT,SUPPLEMENTARY)","SourceInfo");
		hm.put("Added By","AddedBy");
		// hm.put("Date Added","DateAdded"); Text
		hm.put("Descriptive Name From Author", "DescriptiveName"); // <========================== 

	}
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
	
/*
	public static String getFieldName(String name)	{
		
		if (hm.isEmpty())	{
			
			initCommonFields();

				hm.put("Accession Number","AccessionNumber");
				hm.put("Paper Symbol","PaperSymbol");
				
				hm.put("Gene Name","GeneName");
				hm.put("Type of peripheral tissue","TypePeripheralTissue");
				hm.put("Change","CChange");
				hm.put("Number of subjects","NumberOfSubjects");
				hm.put("Males","Males");
				hm.put("Females","Females");
				hm.put("Additional Information","AdditionalInfo");
				
				hm.put("Descriptive Name From Author","DescriptiveName");
				hm.put("Author ID","AuthorId");
				hm.put("Title of Paper","Title");
				hm.put("SOURCE INFO (TABLE#, ABSTRACT,FULL TEXT,SUPPLEMENTARY)","SourceInfo");
				hm.put("Added By","AddedBy");
				hm.put("Date Added","DateAdded");
	
		}		
		return hm.get(name.trim());		
	}

	*/
}