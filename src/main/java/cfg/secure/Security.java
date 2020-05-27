package cfg.secure;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class that supports security.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class Security {

	// Gene is only characters, numbers, and possibly a - somewhere
	// Do NOT use \w because it matches _
	private static final String GENE_WHITELIST = "[a-zA-Z0-9][a-zA-Z0-9_.-]*";
	private static final Pattern genePattern = Pattern.compile(GENE_WHITELIST);
	
	// Scoring weights
	private static final String SCORINGWEIGHTS_WHITELIST = "[0-9.]{1,4}";
	private static final Pattern scoringWeightsPattern = Pattern.compile(SCORINGWEIGHTS_WHITELIST);

	public static boolean passesGeneWhiteList(String gene)	{
		
		Matcher m = genePattern.matcher(gene);
		return m.matches();
	}

	public static boolean passesscoringWeightsWhiteList(String score)	{
		
		Matcher m = scoringWeightsPattern.matcher(score);
		return m.matches();
	}
	
	public static void main(String[] args) {
		String[] genes = {"A.123", "A-123.45", "A_123.45", "A-2-3", "A%123", "A@123", "A#123"};
		for (String gene: genes) {
			System.out.println("\"" + gene + "\" is legal: " + passesGeneWhiteList(gene));
		}
	}
}
