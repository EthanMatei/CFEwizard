package cfe.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import cfe.action.CalculateScores;
import cfe.enums.CfeScoringWeights;


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
		
		List<CfeScoringWeights> weights = getWeights();
	
		
		CalculateScores calculateScores = new CalculateScores();
		calculateScores.setSession(userSession);
		
		//Results results = calculateScores.getResults();
	}
	
	
	
	
	private List<CfeScoringWeights> getWeights() {
		List<CfeScoringWeights> weights = new ArrayList<CfeScoringWeights>();
		CfeScoringWeights weight;
		weight = cfe.enums.CfeScoringWeights.DISCOVERY;
		weight.setWeight( 1.0 );
	    weights.add(weight);
		
		weight = cfe.enums.CfeScoringWeights.PRIORITIZATION;
		weight.setWeight( 1.0 );
	    weights.add(weight);

		weight = cfe.enums.CfeScoringWeights.VALIDATION;
		weight.setWeight( 1.0 );
	    weights.add(weight);
	    
		weight = cfe.enums.CfeScoringWeights.TESTING;
		weight.setWeight( 1.0 );
	    weights.add(weight);

        return weights;
	}
}
