package cfg.model;


public class Research {
	String gene;
	String category;
	String subcategory;
	String psychiatricDomain;
	String subdomain;
	String relevantDisorder;
	String tissue;
	String directionChange;
	String pubMedId;
	
	public boolean equals(Object obj) {
		boolean areEqual = false;
		if (obj != null && obj instanceof Research) {
			Research r = (Research) obj;
			if (gene != null && category != null && subcategory != null 
					&& psychiatricDomain != null && subdomain != null && relevantDisorder != null
					&& tissue != null && directionChange != null && pubMedId != null
					&& gene.equals(r.gene) 
					&& category.equals(r.category)
					&& subcategory.equals(r.subcategory)
					&& psychiatricDomain.equals(r.psychiatricDomain)
					&& subdomain.equals(r.subdomain)
					&& relevantDisorder.equals(r.relevantDisorder)
					&& tissue.equals( r.tissue )
					&& directionChange.equals( r.directionChange )
					&& pubMedId.equals( r.pubMedId )
					) { 
				areEqual = true;
			}
		}
		return areEqual;
	}
	
	public int hashCode() {
	    return (this.pubMedId + this.relevantDisorder + this.category + this.subcategory).hashCode(); 	
	}
	
	//----------------------------------------------
	// Getters and Setters
	//----------------------------------------------
	public String getGene() {
		return gene;
	}
	
	public void setGene(String gene) {
		this.gene = gene;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubcategory() {
		return subcategory;
	}

	public void setSubcategory(String subcategory) {
		this.subcategory = subcategory;
	}

	public String getPsychiatricDomain() {
		return psychiatricDomain;
	}
	
	public void setPsychiatricDomain(String psychiatricDomain) {
		this.psychiatricDomain = psychiatricDomain;
	}
	
	public String getSubdomain() {
		return subdomain;
	}
	
	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
	
	public String getRelevantDisorder() {
		return relevantDisorder;
	}
	
	public void setRelevantDisorder(String relevantDisorder) {
		this.relevantDisorder = relevantDisorder;
	}
	
	public String getTissue() {
		return tissue;
	}
	
	public void setTissue(String tissue) {
		this.tissue = tissue;
	}
	
	public String getDirectionChange() {
		return directionChange;
	}
	
	public void setDirectionChange(String directionChange) {
		this.directionChange = directionChange;
	}
	
	public String getPubMedId() {
		return pubMedId;
	}
	
	public void setPubMedId(String pubMedId) {
		this.pubMedId = pubMedId;
	}
	
}