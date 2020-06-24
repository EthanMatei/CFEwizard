package cfe.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import cfe.action.CalculateScores;
import cfe.enums.ScoringWeights;
import cfe.model.GeneListInput;
import cfe.model.results.Results;


/**
 * Test scoring (work in progress).
 * 
 * @author Jim Mullen
 *
 */
public class ScoringTest {
	
	@Rule
	public ErrorCollector collector= new ErrorCollector();
	
	@Test
	public void testGetDynamicScores(){
	
		Map<String, Object> userSession = new HashMap<String,Object>();
		
		GeneListInput geneListInput = new GeneListInput();
		userSession.put("geneListInput", geneListInput);
		
		List<ScoringWeights> weights = getWeights();
	
		
		CalculateScores calculateScores = new CalculateScores();
		calculateScores.setSession(userSession);
		
		Results results = calculateScores.getResults();
	}
	
	
	
	
	private List<ScoringWeights> getWeights() {
		List<ScoringWeights> weights = new ArrayList<ScoringWeights>();
		ScoringWeights weight;
		weight = cfe.enums.ScoringWeights.DISCOVERY;
		weight.setScore( 1.0 );
	    weights.add(weight);
		
		weight = cfe.enums.ScoringWeights.PRIORITIZATION;
		weight.setScore( 1.0 );
	    weights.add(weight);

		weight = cfe.enums.ScoringWeights.VALIDATION;
		weight.setScore( 1.0 );
	    weights.add(weight);
	    
		weight = cfe.enums.ScoringWeights.TESTING;
		weight.setScore( 1.0 );
	    weights.add(weight);

        return weights;
	}
}
