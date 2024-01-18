package cfe.model.prioritization;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import cfe.action.BatchAction;
import cfe.model.CfeResults;
import cfe.model.CfeResultsType;
import cfe.utils.DataTable;

import java.util.TreeMap;

/**
 * Class for storing gene lists that are uploaded by users. The Map in this class maps from the
 * upper-case version of the gene name to the actual/input gene name. The key (upper-case name)
 * is used for comparisons, since case-insensitive matching is wanted (and values compared are
 * converted to upper-case). The values (actual/input gene names) are used for display purposes.
 * 
 * @author Jim Mullen
 *
 */
public class GeneListInput {
    
    private static Logger log = Logger.getLogger(GeneListInput.class.getName());
    
	private TreeMap<String, TreeSet<String>> genes;    // maps gene name (used for comparison) -> all input gene names
	public static final int MAX_GENES = 40000;
	
	public GeneListInput() {
		genes = new TreeMap<String,TreeSet<String>>(String.CASE_INSENSITIVE_ORDER);
	}

	/**
	 * Constructs a GeneListInput object from a gene list text file, with one gene per line.
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public GeneListInput(String filename) throws Exception {
	    if (filename == null || filename.trim().isEmpty()) {
	        throw new Exception("No gene list file specified.");
	    }
	    
        genes = new TreeMap<String,TreeSet<String>>(String.CASE_INSENSITIVE_ORDER);
	        
	    BufferedReader br = new BufferedReader(new FileReader(filename));
	    
	    String line, geneCardSymbol;

	    while ((line = br.readLine()) != null) {
	        geneCardSymbol = line.trim();
	             
	        if (geneCardSymbol.equals("")) {
	            ; // skip blank lines
	        }
	        else if (cfe.secure.Security.passesGeneWhiteList(geneCardSymbol))  {
	            this.add(geneCardSymbol);
	        } 
	        else {
	            String message = "Invalid gene name \"" + geneCardSymbol + "\" in gene list.";
	            log.warning( message  + " File: " + filename);
	            br.close();
	            throw new Exception( message );
	        }
	    }
	        
	    br.close();     
	}
	   
	/**
	 * Constructs a GeneListInput object from discovery results.
	 * 
	 * @param discoveryResults the discovery results from which to create the GeneListInput.
	 * @param discoveryScoreCutoff the discovery score cutoff to use for selecting genes (genes with a score >= to the
	 *     cutoff are selected).
	 * @param comparisonThreshold the comparison threshold to use for discovery score comparisons, since checking for equality
	 *     for floating point numbers.
	 *
	 * @throws Exception
	 */
    public GeneListInput(CfeResults discoveryResults, Double discoveryScoreCutoff, Double comparisonThreshold) throws Exception {
        genes = new TreeMap<String,TreeSet<String>>(String.CASE_INSENSITIVE_ORDER);
	
        if (discoveryResults == null) {
            throw new Exception("No discovery results specified for gene list creation.");
        }
        
        if (discoveryScoreCutoff == null || discoveryScoreCutoff <= 0.0) {
            String message = "Discovery score cutoff unset for gene list creation.";
            throw new Exception(message);
        }
        
        if (comparisonThreshold == null) {
            comparisonThreshold = 0.0;
        }
        
        log.info("********************************************* DISCOVERY SCORE CUTOFF FOR PRIORITIZATION: " + discoveryScoreCutoff);
        
	    // String key = "Probe Set ID";
        String key = null;  // Can have duplicates
	    DataTable discoveryScores = discoveryResults.getSheetAsDataTable(CfeResultsType.DISCOVERY_SCORES, key);
	    
	    if (discoveryScores == null) {
	        throw new Exception("Discovery scores were not found in discovery results specified for gene list creation.");
	    }
	    
	    Map<String,String> row = null;
	        
	    for (int i = 0; i < discoveryScores.getNumberOfRows(); i++) {
	        row = discoveryScores.getRowMap(i);
	        
            /*
	        for (String rowKey: row.keySet()) {
	            log.info("    ROW MAP[" + rowKey + "] = " + row.get(rowKey));
	        }
            */

            String deRawScore = "DE Raw Score";
            if (!row.containsKey("deRawScore") || row.get(deRawScore).equalsIgnoreCase("NA")) {
                // If this row doesn't have a DE Raw Score or that score is NA, then skip this row
                continue;
            }

	        String deScore = "DE Score";
	            
	        if (!row.containsKey(deScore)) {
	            String message = "Row for discovery scores does not contain the \"" + deScore + "\" column.";
	            log.severe(message);
	            throw new Exception(message);
	        }
	            
	        String scoreString = row.get(deScore);
	            
	        double score = 0.0;
	            
	        if (scoreString != null) {
	            try {
	                score = Double.parseDouble(scoreString);
	            }
	            catch (NumberFormatException exception) {
	                score = 0.0;    
	            }
	        }
	            
	        // log.info("    ******************* SCORE STRING + SCORE + CUTOFF: \"" + scoreString + "\" " + score + " " + discoveryScoreCutoff);
	        if (score >= discoveryScoreCutoff - comparisonThreshold) {
	            String genecardsSymbol = row.get("Genecards Symbol");
	            this.add(genecardsSymbol);
	            // log.info("*************** ADDED GENE CARD SYMBOL: " + genecardsSymbol + " - cutoff: " + discoveryScoreCutoff + " score: " + score + " comparisonThreshold: " + comparisonThreshold);
	        }
	    }
	    
	    log.info(" ***** GENE LIST SIZE: " + this.genes.size());
	}
	    
	/**
	 * Resets the list of genes (in effect deleting any existing genes).
	 */
	public void reset() {
	    genes = new TreeMap<String,TreeSet<String>>();	
	}
	
	public void add(String gene) throws Exception {
		
		if (this.genes.keySet().size() >= MAX_GENES) {
			throw new GeneListInputTooLargeException("The maximum nuber of genes (" + MAX_GENES
					                                 + ") has been exceeded.");
		}
		else {
			TreeSet<String> geneNames = new TreeSet<String>(); 
			if (this.contains(gene)) {
				geneNames = this.genes.get(gene);
			}
			
			geneNames.add( gene.trim() );
		    this.genes.put(gene.trim(),  geneNames);
		}
	}
	
	public boolean contains(String gene) {
		return this.genes.containsKey( gene.trim() );
	}
	
	public String getGeneNames(String gene) {
		String geneNames = "";
		TreeSet<String> geneNameSet = this.genes.get(gene.trim());
		boolean isFirst = true;
		for (String geneName: geneNameSet) {
			if (isFirst) isFirst = false;
			else geneNames += ", ";
			geneNames += geneName;
		}
		return geneNames;
	}
	
	public List<String> getGeneList() {
		return new ArrayList<String>( this.genes.keySet() );
	}
	
	public int size() {
		return this.genes.keySet().size();
	}
	
}
