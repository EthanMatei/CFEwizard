package cfe.model.prioritization.results;

import java.util.List;

import cfe.model.prioritization.Research;


/**
 * Class for storing the results for a category.
 * 
 * @author Jim Mullen
 *
 */
public class CategoryResult {
	double score;
	List<Research> researchList;
	
	public double getScore() {
		return score;
	}
	
	public void setScore(double score) {
		this.score = score;
	}
	
	public List<Research> getResearchList() {
		return researchList;
	}
	
	public void setResearchList(List<Research> researchList) {
		this.researchList = researchList;
	}
	
}