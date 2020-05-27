package cfg.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import cfg.action.CalculateScores;
import cfg.enums.ScoringWeights;
import cfg.model.GeneListInput;
import cfg.model.results.Results;


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
		weight = cfg.enums.ScoringWeights.HUBRAIN;
		weight.setScore( 4.0 );
	    weights.add(weight);
		
		weight = cfg.enums.ScoringWeights.HUPER;
		weight.setScore( 2.0 );
	    weights.add(weight);

		weight = cfg.enums.ScoringWeights.HUGENEASSOC;
		weight.setScore( 2.0 );
	    weights.add(weight);
	    
		weight = cfg.enums.ScoringWeights.HUGCNV;
		weight.setScore( 1.75 );
	    weights.add(weight);


		weight = cfg.enums.ScoringWeights.NHBRAIN;
		weight.setScore( 2.0 );
	    weights.add(weight);
		
		weight = cfg.enums.ScoringWeights.NHPER;
		weight.setScore( 1.0 );
	    weights.add(weight);

		weight = cfg.enums.ScoringWeights.NHGENEASSOC;
		weight.setScore( 1.0 );
	    weights.add(weight);
	    
		weight = cfg.enums.ScoringWeights.NHGCNV;
		weight.setScore( 0.5 );
	    weights.add(weight);

        return weights;
	}
}
