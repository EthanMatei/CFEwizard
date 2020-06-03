package cfe.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.HU_GENE_ASSOC)
public class HuGeneAssoc extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = -5303849499580490198L;
	

	private String PaperSymbol;
	private String GeneName;
	private String SNP;
	private String NumNomSigSNPs;
	private String PValue;
	private String Chromosome;	
	private String OriginOfSample;
	private String AdditionalInfo;
	private String NumberOfSubjects;
	private String Males;
	private String Females;
	private String DescriptiveName;
	private String Author_Date;
	private String Title;
	private String SourceInfo;
	private String AddedBy;
	private Date DateAdded;
	
	public HuGeneAssoc() {
		super();
		hm.put("Paper symbol","PaperSymbol");
		hm.put("Gene name","GeneName");
		hm.put("SNP(Significant)","SNP");
		hm.put("# of Nominally Significant SNPs","NumNomSigSNPs");
		hm.put("P-value","PValue");
		hm.put("Chromosome","Chromosome");
		hm.put("Origin of Sample","OriginOfSample");
		hm.put("Additional information","AdditionalInfo");
		hm.put("Number of Subjects","NumberOfSubjects");
		hm.put("Males","Males");
		hm.put("Females","Females");
		hm.put("Descriptive Name from Author","DescriptiveName");
		hm.put("Author/Date","Author_Date");
		hm.put("Title of Paper","Title");
		hm.put("Source info (Tables #, Abstract, Full Text, Suplementary)","SourceInfo");
		hm.put("Added By","AddedBy");
		//hm.put("Date Added","DateAdded");

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
	public String getSNP() {
		return SNP;
	}
	public void setSNP(String sNP) {
		SNP = sNP;
	}
	public String getNumNomSigSNPs() {
		return NumNomSigSNPs;
	}
	public void setNumNomSigSNPs(String numNomSigSNPs) {
		NumNomSigSNPs = numNomSigSNPs;
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
	public String getOriginOfSample() {
		return OriginOfSample;
	}
	public void setOriginOfSample(String originOfSample) {
		OriginOfSample = originOfSample;
	}
	public String getAdditionalInfo() {
		return AdditionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		AdditionalInfo = additionalInfo;
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
	
/*
	public String getFieldName(String name)	{
		/*
		
		if (hm.isEmpty())	{
			
			initCommonFields();
			hm.put("Paper symbol","PaperSymbol");
			hm.put("Gene name","GeneName");
			hm.put("SNP(Significant)","SNP");
			hm.put("# of Nominally Significant SNPs","NumNomSigSNPs");
			hm.put("P-value","PValue");
			hm.put("Chromosome","Chromosome");
			hm.put("Origin of Sample","OriginOfSample");
			hm.put("Additional information","AdditionalInfo");
			hm.put("Number of Subjects","NumberOfSubjects");
			hm.put("Males","Males");
			hm.put("Females","Females");
			hm.put("Descriptive Name from Author","DescriptiveName");
			hm.put("Author/Date","Author_Date");
			hm.put("Title of Paper","Title");
			hm.put("Source info (Tables #, Abstract, Full Text, Suplementary)","SourceInfo");
			hm.put("Added By","AddedBy");
			hm.put("Date Added","DateAdded");
		}		
		
		return hm.get(name.trim());		
	}
*/
}