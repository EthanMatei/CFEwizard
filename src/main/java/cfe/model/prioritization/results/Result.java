package cfe.model.prioritization.results;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import cfe.enums.prioritization.ScoringWeights;
import cfe.model.prioritization.Research;


/**
 * Class that represents the scoring result for a single gene.
 * 
 * @author Jim Mullen
 *
 */
public class Result {
	private double score;
	private TreeMap <ScoringWeights, CategoryResult> categoryResults;  
	
	
	public Result () {
		this.score = 0.0;
		this.categoryResults = new TreeMap<ScoringWeights, CategoryResult>();
	}
	
	public void add(ScoringWeights scoringCategory, CategoryResult categoryResult) {
		this.categoryResults.put(scoringCategory, categoryResult);
	}
	
	/**
	 * Gets all the research publication data that went into this result.
	 * 
	 * @return research publication data that went into this result
	 */
	public List<Research> getAllResearch() {
		List<Research> researchList = new ArrayList<Research>();
		
		for (ScoringWeights weight: ScoringWeights.values()) {
		    CategoryResult categoryResult = categoryResults.get(weight);
		    researchList.addAll( categoryResult.getResearchList() );
		}
		
		return researchList;
	}

	
	//----------------------------------------------------------
	// Getters and Setters
	//----------------------------------------------------------
	public double getScore() {
		return score;
	}


	public void setScore(double score) {
		this.score = score;
	}

	public TreeMap<ScoringWeights, CategoryResult> getCategoryResults() {
		return categoryResults;
	}

	public void setCategoryResults(
			TreeMap<ScoringWeights, CategoryResult> categoryResults) {
		this.categoryResults = categoryResults;
	}

	
}