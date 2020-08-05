package cfe.model;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//import org.hibernate.annotations.Index;

/**
 * Class for storing CFE score for a single probeset.
 *
 * @author Jim Mullen
 *
 */
public class CfeScores {
	
	private Map<String, CfeScore> scores;

	public CfeScores() {
		this.scores = new TreeMap<String,CfeScore>();
	}
	
	public void addDiscoveryData(List<Discovery> discoveries) {
		for (Discovery discovery: discoveries) {
			
		}
	}
	
	public void setDiscovery(Discovery discovery) {
		String probeset = discovery.getProbeset();
		CfeScore cfeScore = this.getScore(probeset);
		
		
		double discoveryScore = 0.0;
		Double apScore = discovery.getApScore();
		if (apScore != null) {
			discoveryScore = apScore;
		} else {
			discoveryScore = discovery.getDeScore();
		}
		
		cfeScore.setDiscoveryScore(discoveryScore);
		
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

	public void setPrioritization(Prioritization prioritization) {
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

}
