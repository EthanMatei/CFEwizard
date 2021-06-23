package cfe.model.prioritization;

public interface ModelInterface {

	public long getID();
	public String getPsychiatricDomain();
	public String getSubDomain();
	public String getRelevantDisorder();
	public String getGenecardSymbol();
	public double getPubMedID();
	public String getTissue();
	public String getDirectionChange();
}