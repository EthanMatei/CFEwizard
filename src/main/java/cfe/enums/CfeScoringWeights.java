package cfe.enums;

public enum CfeScoringWeights {
	
	DISCOVERY("Discovery", "discoveryWeight", 1.0), 
	PRIORITIZATION("Prioritization", "prioritizationWeight", 1.0),
	VALIDATION("Validation", "validationWeight", 1.0),
	TESTING("Testing", "testingWeight", 1.0)
	;
	
	private String label;
	private double weight;
	private String name; // This must be in synch with cfe.action.ScoringWeights
	
	private CfeScoringWeights (String label){ this.label = label; }	
	public String getLabel(){ return this.label; }
	
	private CfeScoringWeights(String label, String name, double weight) {
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
