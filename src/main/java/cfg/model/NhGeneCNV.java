package cfg.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import cfg.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.NH_GENE_CNV)
public class NhGeneCNV extends Model  implements ModelInterface, Serializable {

	private static final long serialVersionUID = 314929720377229594L;
	private String PaperSymbol;
	private String GeneName;
	private String AnimalType;
	private String CNV;
	private String PValue;
	private String Chromosome;
	private String StudyType;
	private String AdditionalInfo;
	private String DescriptiveName;
	private String Author_Date;
	private String SourceInfo;
	private String AddedBy;
	private String Title;
	
	public NhGeneCNV(){
		super();
		/*
		hm.put("Genecard symbol","");
		hm.put("Psychiatric domain","");
		hm.put("Sub domain","");
		hm.put("Relevant disorder",""); 
		hm.put("Pub Med ID","");
		 */

		hm.put("Paper Symbol","PaperSymbol");
		hm.put("Gene Name","GeneName");
		hm.put("Animal type","AnimalType");
		hm.put("CNV","CNV");
		hm.put("P-value","PValue");
		hm.put("Chromosome","Chromosome");
		hm.put("Type of Study","StudyType");
		hm.put("Additional Information","AdditionalInfo");

		hm.put("Descriptive Name from Author","DescriptiveName");
		hm.put("Author/Date","Author_Date");
		hm.put("Source info","SourceInfo");
		hm.put("Added By","AddedBy");		
		hm.put("Title of Paper","Title");
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

	public String getCNV() {
		return CNV;
	}

	public void setCNV(String cNV) {
		CNV = cNV;
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

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}
}
