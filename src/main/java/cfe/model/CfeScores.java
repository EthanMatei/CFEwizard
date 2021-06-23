package cfe.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cfe.action.CalculateScores;
import cfe.enums.CfeScoringWeights;
import cfe.enums.CfeValidationWeights;

//import org.hibernate.annotations.Index;

/**
 * Class for storing CFE score for a single probeset.
 *
 * @author Jim Mullen
 *
 */
public class CfeScores {
	
	private static final Log log = LogFactory.getLog(CfeScores.class);
	
	private Map<String, CfeScore> scores;
	private List<CfeScoringWeights> weights;
	
	private double discoveryWeight;
	private double prioritizationWeight;
	private double validationWeight;
	private double testingWeight;

	private double nonStepwiseWeight;
	private double stepwiseWeight;
	private double nominalWeight;
	private double bonferroniWeight;
	
	public CfeScores() {
		this.scores = new TreeMap<String,CfeScore>();
		
		this.discoveryWeight      = 1.0;
		this.prioritizationWeight = 1.0;
		this.validationWeight     = 1.0;
		this.testingWeight        = 1.0;
	}
	
	public void setWeights(List<CfeScoringWeights> weights) {
		
		for (CfeScoringWeights weight: weights) {
			switch (weight) {
			    case DISCOVERY:
			    	this.discoveryWeight = weight.getWeight();
			    	break;
			    case PRIORITIZATION:
			    	this.prioritizationWeight = weight.getWeight();
			    	break;
			    case VALIDATION:
			    	this.validationWeight = weight.getWeight();
			    	break;
			    case TESTING:
			    	this.testingWeight = weight.getWeight();
			    	break;
			}
		}
	}
	
	public void setValidationWeights(List<CfeValidationWeights> weights) {
		
		for (CfeValidationWeights weight: weights) {
			switch (weight) {
			    case NON_STEPWISE:
			    	this.nonStepwiseWeight = weight.getWeight();
			    	break;
			    case STEPWISE:
			    	this.stepwiseWeight = weight.getWeight();
			    	break;
			    case NOMINAL:
			    	this.nominalWeight = weight.getWeight();
			    	break;
			    case BONFERRONI:
			    	this.bonferroniWeight = weight.getWeight();
			    	break;
			}
		}
	}
	
	public void setDiscovery(Discovery discovery, List<CfeScoringWeights> weights) {
		String probeset = discovery.getProbeset();
		CfeScore cfeScore = this.getScore(probeset);
		
		double discoveryScore = 0.0;
		discoveryScore = Math.max(discovery.getApScore(), discovery.getDeScore());
		
		cfeScore.setDiscoveryScore(discoveryScore);
		cfeScore.setWeightedDiscoveryScore(discoveryWeight * discoveryScore);
		
		if (cfeScore.getGeneCardsSymbol() == null || cfeScore.getGeneCardsSymbol().trim().equals("")) {
			cfeScore.setGeneCardsSymbol(discovery.getGeneCardsSymbol());
		}
		
		if (cfeScore.getGeneTitle() == null || cfeScore.getGeneTitle().trim().equals("")) {
			cfeScore.setGeneTitle(discovery.getGeneTitle());
		}
		
		if (cfeScore.getChangeInExpressionInTrackedPhene() == null || cfeScore.getChangeInExpressionInTrackedPhene().trim().equals("")) {
			cfeScore.setChangeInExpressionInTrackedPhene(discovery.getChangeInExpressionInTrackedPhene());
		}
		
		this.setScore(cfeScore);		
	}

	
	public void setPrioritization(Prioritization prioritization, List<CfeScoringWeights> weights) {
		String probeset = prioritization.getProbeset();
		CfeScore cfeScore = this.getScore(probeset);
		
		
		double prioritizationScore = 0.0;
		prioritizationScore =
				prioritization.getHuBrainScore()
				+ prioritization.getHuGCnvScore()
				+ prioritization.getHuGeneAssocScore()
				+ prioritization.getHuPerScore()
				+ prioritization.getNhBrainScore()
				+ prioritization.getNhGCnvScore()
				+ prioritization.getNhGeneAssocScore()
				+ prioritization.getNhPerScore()
				;
		
		cfeScore.setPrioritizationScore(prioritizationScore);
		cfeScore.setWeightedPrioritizationScore(prioritizationWeight * prioritizationScore);
		
		if (cfeScore.getGeneCardsSymbol() == null || cfeScore.getGeneCardsSymbol().trim().equals("")) {
			cfeScore.setGeneCardsSymbol(prioritization.getGeneCardsSymbol());
		}
		
		if (cfeScore.getGeneTitle() == null || cfeScore.getGeneTitle().trim().equals("")) {
			cfeScore.setGeneTitle(prioritization.getGeneTitle());
		}
		
		if (cfeScore.getChangeInExpressionInTrackedPhene() == null || cfeScore.getChangeInExpressionInTrackedPhene().trim().equals("")) {
			cfeScore.setChangeInExpressionInTrackedPhene(prioritization.getChangeInExpressionInTrackedPhene());
		}
		
		this.setScore(cfeScore);		
	}
	
	public void setValidation(Validation validation, List<CfeScoringWeights> weights) throws Exception {
		String probeset = validation.getProbeset();
		CfeScore cfeScore = this.getScore(probeset);
		
		double validationScore = 0.0;
		
		String validationType = validation.getValidation();
		if (validationType == null) {
			validationType = "";
		}
		else {
			validationType = validationType.trim().toLowerCase().replaceAll("\\s+", " ");
		}
		
		if (validationType.equals("non-stepwise") || validationType.equals("not stepwise")) {
			validationScore = this.nonStepwiseWeight;
		}
		else if (validationType.equals("stepwise")) {
			validationScore = this.stepwiseWeight;
		}
		else if (validationType.equals("nominal")) {
			validationScore = this.nominalWeight;
		}
		else if (validationType.equals("bonferroni")) {
			validationScore = this.bonferroniWeight;
		}
		else {
			log.info("*** VALIDATION TYPE ERROR, TYPE: " + validationType);
			throw new Exception("Unrecognized validation type \"" + validationType + "\".");
		}
		
		cfeScore.setValidationScore(validationScore);
		cfeScore.setWeightedValidationScore(validationWeight * validationScore);
		
		if (cfeScore.getGeneCardsSymbol() == null || cfeScore.getGeneCardsSymbol().trim().equals("")) {
			cfeScore.setGeneCardsSymbol(validation.getGeneCardsSymbol());
		}
		
		if (cfeScore.getGeneTitle() == null || cfeScore.getGeneTitle().trim().equals("")) {
			cfeScore.setGeneTitle(validation.getGeneTitle());
		}
		
		if (cfeScore.getChangeInExpressionInTrackedPhene() == null || cfeScore.getChangeInExpressionInTrackedPhene().trim().equals("")) {
			cfeScore.setChangeInExpressionInTrackedPhene(validation.getChangeInExpressionInTrackedPhene());
		}
		
		this.setScore(cfeScore);		
	}

	public void setTesting(Testing testing, List<CfeScoringWeights> weights) throws Exception {
		String probeset = testing.getProbeset();
		CfeScore cfeScore = this.getScore(probeset);
		
		double testingScore = 0.0;
		
		testingScore += testing.getSmsLowMoodScore();
		testingScore += testing.getHamdScore();
		testingScore += testing.getFirstYearDepressionScore();
		testingScore += testing.getAllFutureDepression();
		
		cfeScore.setTestingScore(testingScore);
		cfeScore.setWeightedTestingScore(testingWeight * testingScore);
		
		if (cfeScore.getGeneCardsSymbol() == null || cfeScore.getGeneCardsSymbol().trim().equals("")) {
			cfeScore.setGeneCardsSymbol(testing.getGeneCardsSymbol());
		}
		
		if (cfeScore.getGeneTitle() == null || cfeScore.getGeneTitle().trim().equals("")) {
			cfeScore.setGeneTitle(testing.getGeneTitle());
		}
		
		if (cfeScore.getChangeInExpressionInTrackedPhene() == null || cfeScore.getChangeInExpressionInTrackedPhene().trim().equals("")) {
			cfeScore.setChangeInExpressionInTrackedPhene(testing.getChangeInExpressionInTrackedPhene());
		}
		
		this.setScore(cfeScore);		
	}

	public void calculateTotalScores(List<CfeScoringWeights> weights) {
		
	    for (CfeScore score: this.scores.values()) {
	    	score.setTotalScore(
	    	    (score.getDiscoveryScore() * discoveryWeight)
	    	    + (score.getPrioritizationScore() * prioritizationWeight)
	    	    + (score.getValidationScore() * validationWeight)
	    	    + (score.getTestingScore() * testingWeight)
	    	);
	    	this.setScore(score);
	    }
	}
	
	public void addProbesetIfNotExists(String probeset) {
		if (!this.scores.containsKey(probeset)) {
			CfeScore cfeScore = new CfeScore(probeset);
			this.scores.put(probeset, cfeScore);
		}
	}
	
	// Getters and setters -------------------------------------------------------------------------
	
	public Map<String,CfeScore> getScores() {
		return this.scores;
	}
	
	/**
	 * Gets the score for the specified probeset. If the probeset
	 * does not exists, a blank score object is returned.
	 * 
	 * @param probeset
	 * @return
	 */
	public CfeScore getScore(String probeset) {
		CfeScore score = new CfeScore(probeset);
		if (this.scores.containsKey(probeset)) {
			score = this.scores.get(probeset);
		}
		return score;
	}
	
	public void setScore(CfeScore cfeScore) {
		this.scores.put(cfeScore.getProbeset(), cfeScore);
	}

	public double getDiscoveryWeight() {
		return discoveryWeight;
	}

	public double getPrioritizationWeight() {
		return prioritizationWeight;
	}

	public double getValidationWeight() {
		return validationWeight;
	}

	public double getTestingWeight() {
		return testingWeight;
	}

	public double getNonStepwiseWeight() {
		return nonStepwiseWeight;
	}

	public double getStepwiseWeight() {
		return stepwiseWeight;
	}

	public double getNominalWeight() {
		return nominalWeight;
	}

	public double getBonferroniWeight() {
		return bonferroniWeight;
	}

}
