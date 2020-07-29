package cfe.model;

//import org.hibernate.annotations.Index;

/**
 * Class for storing CFE score for a single probeset.
 *
 * @author Jim Mullen
 *
 */
public class CfeScore {
	
	private String probeset;         // Natural key
	private String geneCardsSymbol;
	private String geneTitle;
	private String changeInExpressionInTrackedPhene;
	
    private double discoveryScore;
    private double prioritizationScore;
    private double validationScore;
    private double testingScore;
    private double cfeScore; // total score

	protected CfeScore() {
	}
	
	// Getters and setters -------------------------------------------------------------------------
	
	public String getProbeset() {
		return probeset;
	}

	public void setProbeset(String probeset) {
		this.probeset = probeset;
	}

	public String getGeneCardsSymbol() {
		return geneCardsSymbol;
	}

	public void setGeneCardsSymbol(String geneCardsSymbol) {
		this.geneCardsSymbol = geneCardsSymbol;
	}

	public String getGeneTitle() {
		return geneTitle;
	}

	public void setGeneTitle(String geneTitle) {
		this.geneTitle = geneTitle;
	}

	public String getChangeInExpressionInTrackedPhene() {
		return changeInExpressionInTrackedPhene;
	}

	public void setChangeInExpressionInTrackedPhene(String changeInExpressionInTrackedPhene) {
		this.changeInExpressionInTrackedPhene = changeInExpressionInTrackedPhene;
	}

	public double getDiscoveryScore() {
		return discoveryScore;
	}

	public void setDiscoveryScore(double discoveryScore) {
		this.discoveryScore = discoveryScore;
	}

	public double getPrioritizationScore() {
		return prioritizationScore;
	}

	public void setPrioritizationScore(double prioritizationScore) {
		this.prioritizationScore = prioritizationScore;
	}

	public double getValidationScore() {
		return validationScore;
	}

	public void setValidationScore(double validationScore) {
		this.validationScore = validationScore;
	}

	public double getTestingScore() {
		return testingScore;
	}

	public void setTestingScore(double testingScore) {
		this.testingScore = testingScore;
	}

	public double getCfeScore() {
		return cfeScore;
	}

	public void setCfeScore(double cfeScore) {
		this.cfeScore = cfeScore;
	}

}
