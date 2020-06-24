package cfe.enums;

public enum ScoringWeights {
	
	DISCOVERY("Discovery", "discoveryScore", 1.0), 
	PRIORITIZATION("Prioritization", "prioritizationScore", 1.0),
	VALIDATION("Validation", "validationScore", 1.0),
	TESTING("Testing", "testingScore", 1.0)
	;
	
	private String label;
	private double score;
	private String name; // This must be in synch with cfe.action.ScoringWeights
	
	private ScoringWeights (String label){ this.label = label; }	
	public String getLabel(){ return this.label; }
	
	private ScoringWeights(String label, String name, double score) {this.score = score; this.label = label; this.name = name;}
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
