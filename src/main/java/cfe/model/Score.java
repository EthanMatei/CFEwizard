package cfe.model;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cfe.action.CalculateScores;
import cfe.enums.ScoringWeights;
import cfe.model.results.CategoryResult;
import cfe.model.results.Result;
import cfe.model.results.Results;

public class Score {
	
	private static final Log log = LogFactory.getLog(Score.class);
	
	public static Results calculate(List<cfe.enums.ScoringWeights> weights) 
	        throws Exception {
		
		Results results = new Results();
		PrintWriter writer = null;
		boolean debug = false;

		//--------------------------------------------------------------------------
		// If debug set, try to set up a debug file to write information to
		//--------------------------------------------------------------------------
		if (debug) {
			try {
				File file = new File("score-debug.txt");
				writer = new PrintWriter(file, "UTF-8");
				writer.println("Score Test");
				writer.flush();
				log.info("+++++++++++++++++++++++++++++++++++++++++++++++++ file write did not fail.");
			}
			catch (IOException exception) {
				log.error("********************** COULD NOT WRITE TO SCORING TEST FILE");
			}
		}
		

		//---------------------------------------------------------------
		
		//List<ModelInterface> huBrainGexs     = HuBrainGexService.getSelected(diseaseSelection, geneListInput);
		//researchData.add("HUBRAIN",  "HuBrainGex", huBrainGexs);
		
	
    	//---------------------------------------------------------
		// Get the global scoring weights
		//---------------------------------------------------------
		double discovery        = 0.0;
		double prioritization   = 0.0;
		double validation       = 0.0;
		double testing          = 0.0;
			
		for (cfe.enums.ScoringWeights weight: weights) {
			switch (weight) {
			case DISCOVERY:
				discovery = weight.getScore();
				break;
			case PRIORITIZATION:
				prioritization = weight.getScore();
				break;
			case VALIDATION:
				validation = weight.getScore();
				break;
			case TESTING:
				testing = weight.getScore();
				break;
			}
		}

		return results;
		
	}
	
}