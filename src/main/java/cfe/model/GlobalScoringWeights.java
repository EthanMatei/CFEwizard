package cfe.model;

/**
 * @author Jim Mullen
 *
 */
public class GlobalScoringWeights {
	private double discoveryWeight;
	private double prioritizationWeight;
	private double validationWeight;
	private double testingWeight;
	
	public double getDiscoveryWeight() {
		return discoveryWeight;
	}
	
	public void setDiscoveryWeight(double discoveryWeight) {
		this.discoveryWeight = discoveryWeight;
	}
	
	public double getPrioritizationWeight() {
		return prioritizationWeight;
	}
	
	public void setPrioritizationWeight(double prioritizationWeight) {
		this.prioritizationWeight = prioritizationWeight;
	}
	
	public double getValidationWeight() {
		return validationWeight;
	}
	
	public void setValidationWeight(double validationWeight) {
		this.validationWeight = validationWeight;
	}
	
	public double getTestingWeight() {
		return testingWeight;
	}
	
	public void setTestingWeight(double testingWeight) {
		this.testingWeight = testingWeight;
	}
}
