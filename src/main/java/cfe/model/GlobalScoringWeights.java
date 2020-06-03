package cfe.model;

/**
 * Scoring weights class (hu = human, nh = non-human) WORK IN PROGRESS.
 * 
 * @author Jim Mullen
 *
 */
public class GlobalScoringWeights {
	private double huBrainScore;
	private double huPerScore;
	private double huGeneCnvScore;
	private double huGeneAssocScore;
	private double huGeneLinkageScore;
	private double nhBrainScore;
	private double nhPerScore;
	private double nhGeneCnvScore;
	private double nhGeneAssocScore;
	private double nhGeneLinkageScore;
	
	public double getHuBrainScore() {
		return huBrainScore;
	}
	
	public void setHuBrainScore(double huBrainScore) {
		this.huBrainScore = huBrainScore;
	}
	
	public double getHuPerScore() {
		return huPerScore;
	}
	
	public void setHuPerScore(double huPerScore) {
		this.huPerScore = huPerScore;
	}
	
	public double getHuGeneCnvScore() {
		return huGeneCnvScore;
	}
	
	public void setHuGeneCnvScore(double huGeneCnvScore) {
		this.huGeneCnvScore = huGeneCnvScore;
	}
	
	public double getHuGeneAssocScore() {
		return huGeneAssocScore;
	}
	
	public void setHuGeneAssocScore(double huGeneAssocScore) {
		this.huGeneAssocScore = huGeneAssocScore;
	}
	
	public double getHuGeneLinkageScore() {
		return huGeneLinkageScore;
	}
	
	public void setHuGeneLinkageScore(double huGeneLinkageScore) {
		this.huGeneLinkageScore = huGeneLinkageScore;
	}
	
	public double getNhBrainScore() {
		return nhBrainScore;
	}
	
	public void setNhBrainScore(double nhBrainScore) {
		this.nhBrainScore = nhBrainScore;
	}
	
	public double getNhPerScore() {
		return nhPerScore;
	}
	
	public void setNhPerScore(double nhPerScore) {
		this.nhPerScore = nhPerScore;
	}
	
	public double getNhGeneCnvScore() {
		return nhGeneCnvScore;
	}
	
	public void setNhGeneCnvScore(double nhGeneCnvScore) {
		this.nhGeneCnvScore = nhGeneCnvScore;
	}
	
	public double getNhGeneAssocScore() {
		return nhGeneAssocScore;
	}
	
	public void setNhGeneAssocScore(double nhGeneAssocScore) {
		this.nhGeneAssocScore = nhGeneAssocScore;
	}
	
	public double getNhGeneLinkageScore() {
		return nhGeneLinkageScore;
	}
	
	public void setNhGeneLinkageScore(double nhGeneLinkageScore) {
		this.nhGeneLinkageScore = nhGeneLinkageScore;
	}
	
}
