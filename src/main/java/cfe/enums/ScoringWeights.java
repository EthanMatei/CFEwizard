package cfe.enums;

public enum ScoringWeights {
	
	HUBRAIN("Human Brain", "huBrainScore", 4.0), 
	HUPER("Human Peripheral", "huPerScore", 2.0),
	HUGENEASSOC("Human Genetic Association", "huGeneAssocScore", 2.0),
	HUGCNV("Human Genetic CNV", "HuGeneCnvScore", 1.5),
		
	NHBRAIN("NonHuman Brain", "nhBrainScore", 2.0), 
	NHPER("NonHuman Peripheral", "nhPerScore", 1.0),
	NHGENEASSOC("NonHuman Genetic Association", "nhGeneAssocScore", 1.0),
	NHGCNV("NonHuman Genetic CNV", "nhGeneCnvScore", 0.75);
	
	// HUGENELINKAGE("Human Gene Linkage", "huGeneLinkage", 0.5);
	
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
