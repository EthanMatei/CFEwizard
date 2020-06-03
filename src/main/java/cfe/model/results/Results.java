package cfe.model.results;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import cfe.enums.ScoringWeights;
import cfe.model.Research;

/**
 * Class for holding score calculation results.
 * 
 * @author Jim Mullen
 *
 */
public class Results {
	TreeMap<String, Result> results = null;   // Map from gene to result for that gene     
	                                   
	TreeMap<String,TreeSet<String>> geneNames = null;  // Map from gene name to gene name variations
	
	List<String> categoryHeaders;
	
	public Results() {
		this.results = new TreeMap<String, Result>(String.CASE_INSENSITIVE_ORDER);
		this.geneNames = new TreeMap<String,TreeSet<String>>(String.CASE_INSENSITIVE_ORDER);
		
		categoryHeaders = new ArrayList<String>();
	    ScoringWeights[] vals = ScoringWeights.values();
	    for (ScoringWeights weight: vals) {
	    	categoryHeaders.add( weight.toString() );
	    }
	    //Collections.sort( categoryHeaders );
	}

	public void add(String gene, Result result) {
	    this.results.put(gene, result);
	    TreeSet<String> geneNames = new TreeSet<String>();
	    geneNames.add(gene); // Add the gene, in cased there are no research results for this gene
	    
	    // Code for setting up a set of actual gene names used (to be used for output)
	    List<Research> researchList = result.getAllResearch();
	    for (Research research: researchList) {
	    	String geneName = research.getGene();
	    	geneNames.add( geneName );
	    }
    	this.geneNames.put(gene, geneNames);
	}
	
	//----------------------------------------------------
	// Getters and Setters
	//----------------------------------------------------
	public TreeMap<String, Result> getResults() {
		return results;
	}

	public void setResults(TreeMap<String, Result> results) {
		this.results = results;
	}

	public List<String> getCategoryHeaders() {
		return categoryHeaders;
	}

	public void setCategoryHeaders(List<String> categoryHeaders) {
		this.categoryHeaders = categoryHeaders;
	}
	
	public TreeMap<String, TreeSet<String>> getGeneNames() {
		return geneNames;
	}

	public String getGeneNames(String gene) {
	    String geneNames = "";
	    boolean isFirst = true;
		for (String geneName: this.geneNames.get(gene)) {
			if (isFirst) isFirst = false;
			else geneNames += ", ";
			geneNames += geneName;
		}
		
		return geneNames;
	}

}