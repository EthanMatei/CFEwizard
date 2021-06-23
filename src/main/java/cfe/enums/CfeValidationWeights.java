package cfe.enums;

public enum CfeValidationWeights {
	
	NON_STEPWISE("Non-stepwise", "nonStepwiseWeight", 0.0), 
	STEPWISE("Stepwise", "stepwiseWeight", 2.0),
	NOMINAL("Nominal", "nominalWeight", 4.0),
	BONFERRONI("Bonferroni", "bonferroniWeight", 6.0)
	;
	
	private String label;
	private double weight;
	private String name; // This must be in synch with cfe.action.ScoringWeights
	
	private CfeValidationWeights (String label){ this.label = label; }	
	public String getLabel(){ return this.label; }
	
	private CfeValidationWeights(String label, String name, double weight) {
		this.weight = weight;
		this.label = label;
		this.name = name;
	}
	
	public double getWeight() {
		return weight;
	}
	
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
