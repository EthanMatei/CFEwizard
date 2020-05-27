package cfg.model.disease;



public class DiseaseSelector {
	private String psychiatricDomain;
	private boolean psychiatricDomainSelected;
	
	private String psychiatricSubDomain;
	private boolean psychiatricSubDomainSelected;
	
	private String relevantDisorder;
	private boolean relevantDisorderSelected;
	private Double coefficient;
	
	public DiseaseSelector () {
	    this.psychiatricDomain = "";
	    this.psychiatricDomainSelected = false;
	    
	    this.psychiatricSubDomain = "";
	    this.psychiatricSubDomainSelected = false;
	    
	    this.relevantDisorder = "";
	    this.relevantDisorderSelected = false;
	    this.coefficient = 0.0;
	}

	public String getPsychiatricDomain() {
		return psychiatricDomain;
	}

	public void setPsychiatricDomain(String psychiatricDomain) {
		this.psychiatricDomain = psychiatricDomain;
	}

	public boolean isPsychiatricDomainSelected() {
		return psychiatricDomainSelected;
	}

	public void setPsychiatricDomainSelected(boolean psychiatricDomainSelected) {
		this.psychiatricDomainSelected = psychiatricDomainSelected;
	}

	public String getPsychiatricSubDomain() {
		return psychiatricSubDomain;
	}

	public void setPsychiatricSubDomain(String psychiatricSubDomain) {
		this.psychiatricSubDomain = psychiatricSubDomain;
	}

	public boolean isPsychiatricSubDomainSelected() {
		return psychiatricSubDomainSelected;
	}

	public void setPsychiatricSubDomainSelected(boolean psychiatricSubDomainSelected) {
		this.psychiatricSubDomainSelected = psychiatricSubDomainSelected;
	}

	public String getRelevantDisorder() {
		return relevantDisorder;
	}

	public void setRelevantDisorder(String relevantDisorder) {
		this.relevantDisorder = relevantDisorder;
	}

	public boolean isRelevantDisorderSelected() {
		return relevantDisorderSelected;
	}

	public void setRelevantDisorderSelected(boolean relevantDisorderSelected) {
		this.relevantDisorderSelected = relevantDisorderSelected;
	}

	public Double getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(Double coefficient) {
		this.coefficient = coefficient;
	}
	
}