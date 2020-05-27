package cfg.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Table;
import cfg.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.HU_GENE_LINKAGE)
public class HuGeneLinkage extends Model implements ModelInterface, Serializable {

	private static final long serialVersionUID = 2583156600763019810L;

	
	private String Chromosome;
	private String ChromosomalLocus;
	private String ManuscriptMarker;
	private String MapOMapMarkerName;
	private String MapOMapSexAverage;
	private String MapOMapFemaleMapPos;
	private String MapOMapMaleMapPos;
	private String LocationFromPaper;
	private String LODScore;
	private String NPLScore;
	private String PValue;
	private String AdditionalInfo;
	private String OriginOfSample;
	private String NumberOfSubjects;
	private String Males;
	private String Females;
	private String DescriptiveName;
	private String Author_Date;
	private String Title;
	private String SourceInfo;
	private String AddedBy;
	private Date  DateAdded;
	
	public HuGeneLinkage() {
		super();
		hm.put("Chromosome","Chromosome");
		hm.put("Chromosomal locus","ChromosomalLocus");
		hm.put("Marker from Manuscript","ManuscriptMarker");
		hm.put("Map-O-Map Marker name","MapOMapMarkerName");
		hm.put("Map-O-Map Sex-average","MapOMapSexAverage");
	//	hm.put("Map-O-Map Female map position","MapOMapFemaleMapPos"); ****
		hm.put("Map-O-Map Male map position","MapOMapMaleMapPos");
		hm.put("Location From Paper","LocationFromPaper");
		hm.put("LOD","LODScore");
		hm.put("NPL Score","NPLScore");
		hm.put("P-value","PValue");
		hm.put("Additional Information","AdditionalInfo");
		hm.put("Origin of sample","OriginOfSample");
		hm.put("Number of Subjects","NumberOfSubjects");
		hm.put("Males","Males");
		hm.put("Females","Females");
		hm.put("Descriptive Name from Author","DescriptiveName");
		hm.put("Author/Date","Author_Date");
		hm.put("Title of Paper","Title");
		hm.put("Source info (Tables #, Abstracts, Full Text, Supplementary)","SourceInfo");
		hm.put("ADDED BY","AddedBy");
	//	hm.put("Date Added","DateAdded");
		hm.put("Pyschiatric Domain", "PsychiatricDomain"); // <=============================
		hm.put("Descriptive Name From Author","DescriptiveName"); // <========================= (from is different)

	}
	
	public String getChromosome() {
		return Chromosome;
	}
	public void setChromosome(String chromosome) {
		Chromosome = chromosome;
	}
	public String getChromosomalLocus() {
		return ChromosomalLocus;
	}
	public void setChromosomalLocus(String chromosomalLocus) {
		ChromosomalLocus = chromosomalLocus;
	}
	public String getManuscriptMarker() {
		return ManuscriptMarker;
	}
	public void setManuscriptMarker(String manuscriptMarker) {
		ManuscriptMarker = manuscriptMarker;
	}
	public String getMapOMapMarkerName() {
		return MapOMapMarkerName;
	}
	public void setMapOMapMarkerName(String mapOMapMarkerName) {
		MapOMapMarkerName = mapOMapMarkerName;
	}
	public String getMapOMapSexAverage() {
		return MapOMapSexAverage;
	}
	public void setMapOMapSexAverage(String mapOMapSexAverage) {
		MapOMapSexAverage = mapOMapSexAverage;
	}
	public String getMapOMapFemaleMapPos() {
		return MapOMapFemaleMapPos;
	}
	public void setMapOMapFemaleMapPos(String mapOMapFemaleMapPos) {
		MapOMapFemaleMapPos = mapOMapFemaleMapPos;
	}
	public String getMapOMapMaleMapPos() {
		return MapOMapMaleMapPos;
	}
	public void setMapOMapMaleMapPos(String mapOMapMaleMapPos) {
		MapOMapMaleMapPos = mapOMapMaleMapPos;
	}
	public String getLocationFromPaper() {
		return LocationFromPaper;
	}
	public void setLocationFromPaper(String locationFromPaper) {
		LocationFromPaper = locationFromPaper;
	}
	public String getLODScore() {
		return LODScore;
	}
	public void setLODScore(String lOD) {
		LODScore = lOD;
	}
	public String getNPLScore() {
		return NPLScore;
	}
	public void setNPLScore(String nPLScore) {
		NPLScore = nPLScore;
	}
	public String getPValue() {
		return PValue;
	}
	public void setPValue(String pValue) {
		PValue = pValue;
	}
	public String getAdditionalInfo() {
		return AdditionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		AdditionalInfo = additionalInfo;
	}
	public String getOriginOfSample() {
		return OriginOfSample;
	}
	public void setOriginOfSample(String originOfSample) {
		OriginOfSample = originOfSample;
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
	
	public static String getFieldName(String name)	{
		
		if (hm.isEmpty())	{
			
			initCommonFields();
			hm.put("Chromosome","Chromosome");
			hm.put("Chromosomal locus","ChromosomalLocus");
			hm.put("Marker from Manuscript","ManuscriptMarker");
			hm.put("Map-O-Map Marker name","MapOMapMarkerName");
			hm.put("Map-O-Map Sex-average","MapOMapSexAverage");
			hm.put("Map-O-Map Female map position","MapOMapFemaleMapPos");
			hm.put("Map-O-Map Male map position","MapOMapMaleMapPos");
			hm.put("Location From Paper","LocationFromPaper");
			hm.put("LOD","LOD");
			hm.put("NPL Score","NPLScore");
			hm.put("P-value","PValue");
			hm.put("Additional Information","AdditionalInfo");
			hm.put("Origin of sample","OriginOfSample");
			hm.put("Number of Subjects","NumberOfSubjects");
			hm.put("Males","Males");
			hm.put("Females","Females");
			hm.put("Descriptive Name from Author","DescriptiveName");
			hm.put("Author/Date","Author_Date");
			hm.put("Title of Paper","Title");
			hm.put("Source info (Tables #, Abstracts, Full Text, Supplementary)","SourceInfo");
			hm.put("ADDED BY","AddedBy");
			hm.put("Date Added","DateAdded");
		}		
		return hm.get(name.trim());		
	}
	*/
}
