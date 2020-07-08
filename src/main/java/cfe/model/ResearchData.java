package cfe.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * Class for holding the research data, from which score calculations are made.
 * 
 * @author Jim Mullen
 *
 */
public class ResearchData {
	private static final Log log = LogFactory.getLog(ResearchData.class);
	
	TreeMap<String, List<Research>> data;  // Map of gene to research (publication)
	                                       // For comparison purposes, gene names are converted to upper-case
	
	public ResearchData() {
		log.info("ResearchData object created");
		this.data = new TreeMap<String, List<Research>>(String.CASE_INSENSITIVE_ORDER);
	}
	
	public TreeMap<String, List<Research>> getData() {
	    return this.data;
	}
	
	public void add(String gene) {
		List<Research> researchList = new ArrayList<Research>();
		this.data.put(gene.trim(),  researchList);
	}
	
	public void add(String gene, Research research) {
		
		List<Research> researchList = new ArrayList<Research>();
		
		gene = gene.trim();
		
		if (this.data.containsKey(gene)) {
			researchList = data.get(gene);
		}
		
		researchList.add(research);
		this.data.put(gene,  researchList);
	}
	
	public List<Research> get(String gene) {
		List<Research> researchList = null;
		
		researchList = this.data.get(gene);
		return researchList;
	}
	
	public Set<Research> getUnique(String gene) {
		List<Research> researchList = null;
		
		researchList = this.data.get(gene);
		Set<Research> researchSet = new HashSet<Research>( researchList );
		
		return researchSet;
	}
	
	public Set<String> getGenes() {
		return this.data.keySet();
	}
	
	public void merge(ResearchData mergeData) {
		for (String gene: mergeData.getGenes()) {
			List<Research> mergeResearchList = mergeData.get(gene);
			if (this.data.containsKey(gene)) {
				List<Research> researchList = this.data.get(gene);
				for (Research research: mergeResearchList) {
					researchList.add( research );
				}
				this.data.put(gene,  researchList);
			}
			else {
				this.data.put(gene,  mergeResearchList);
			}
		}
	}
	
	/**
	 * 
	 * @param gene
	 * @param category
	 * @param unique
	 * @return
	 */
	public List<Research> get(String gene, String category) {
		List<Research> list = this.get(gene);
		List<Research> values = new ArrayList<Research>();
		for (Research research: list) {
			if (research.category.equals(category)) {
				values.add(research);
			}
		}
		return values;
	}

	public List<Research> get(String gene, String category, String subcategory) {
		List<Research> list = this.get(gene);
		List<Research> values = new ArrayList<Research>();
		for (Research research: list) {
			if (research.category.equals(category) && research.subcategory.equals(subcategory)) {
				values.add(research);
			}
		}
		return values;
	}

	
	public List<Research> getResearchByScoringCategory(String gene, cfe.enums.ScoringWeights scoringCategory) {
		List<Research> researchList = new ArrayList<Research>();
		switch (scoringCategory) {
		case DISCOVERY:
			researchList = this.get(gene, "DISCOVERY");
			break;
		case PRIORITIZATION:
			researchList = this.get(gene, "PRIORITIZATION");
			break;
		case VALIDATION:
			researchList = this.get(gene, "VALIDATION");
		    break;
		case TESTING:
			researchList = this.get(gene, "TESTING");
			break;
		}
		return researchList;
	}
	

}