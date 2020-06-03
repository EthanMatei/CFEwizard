package cfe.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
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
	
	private TreeMap<String, TreeSet<String>> genes;    // maps gene name (used for comparison) -> all input gene names
	public static final int MAX_GENES = 40000;
	
	public GeneListInput() {
		genes = new TreeMap<String,TreeSet<String>>(String.CASE_INSENSITIVE_ORDER);
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