package cfe.action;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.enums.Scores;
import cfe.enums.ScoringWeights;
import cfe.enums.ValidationWeights;
import cfe.model.CfeScore;
import cfe.model.CfeScores;

import cfe.model.Discovery;
import cfe.services.DiscoveryService;
import cfe.model.Prioritization;
import cfe.services.PrioritizationService;
import cfe.model.Testing;
import cfe.services.TestingService;
import cfe.model.Validation;
import cfe.services.ValidationService;

import cfe.utils.Authorization;

/**
 * Action that calculates the scores.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class CalculateScores extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 0L;
	private static final Log log = LogFactory.getLog(CalculateScores.class);
	private String score;
	private boolean otherCompleted = false;
    
    CfeScores cfeScores;
    
	private Map<String, Object> session;
	
	//private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();

	public String execute() {
		String status = SUCCESS;

		if (!Authorization.isLoggedIn(session)) {
			status = LOGIN;
		}
		else {
			//-----------------------------------------------------
			// Get input selections
			//-----------------------------------------------------
			//Object diseasesObject = session.get("diseaseSelectors");
			Object weightsObject  = session.get("weights");
			Object validationWeightsObject = session.get("validationWeights");
			

			if (weightsObject == null || validationWeightsObject == null) {
				this.setErrorMessage("Unfortunately, your session has expired. You will need to restart your score calculation.");
				status = ERROR;
			}
			else {

				List<ScoringWeights> weights = (List<ScoringWeights>) weightsObject;
				/*
				for (ScoringWeights weight: weights) {
					if (weight == ScoringWeights.DISCOVERY) {
						
					}
				}
				*/
				
				List<ValidationWeights> validationWeights = (List<ValidationWeights>) validationWeightsObject;
			    
			    this.cfeScores = new CfeScores();
			    
			    this.cfeScores.setWeights(weights);
			    this.cfeScores.setValidationWeights(validationWeights);
				
				try {
				    // Process discovery data
				    List<Discovery> discoveries = DiscoveryService.getAll();
				    for (Discovery discovery: discoveries) {
				        this.cfeScores.setDiscovery(discovery, weights);
				    }
				    
				    // Process prioritization data
				    List<Prioritization> prioritizations = PrioritizationService.getAll();
				    for (Prioritization prioritization: prioritizations) {
				        this.cfeScores.setPrioritization(prioritization, weights);
				    }
				    
				    // Process validation data
			        List<Validation> validations = ValidationService.getAll();
				    for (Validation validation: validations) {
				    	this.cfeScores.setValidation(validation, weights);
				    }
				    
				    List<Testing> testings = TestingService.getAll();
				    for (Testing testing: testings) {
				    	this.cfeScores.setTesting(testing, weights);
				    }

				    this.cfeScores.calculateTotalScores(weights);
				    
				   //log.info("CFE Scores Count: " + this.cfeScores.);
				    
				}
				catch (Exception exception) {
					this.setErrorMessage( exception.getMessage() );
					log.error( exception.getMessage() );
					exception.printStackTrace();
					status = ERROR;	
				}

				session.put("cfeScores", this.cfeScores);
			}
		}
		return status;
	}
	
		
	public void validate() {
		//addActionError( "Score calculation for " + this.score + " is currently not supported." );
	}

	//public Map<String, ScoreResults> getScores() {
	//	return scores;
	//}

	//public void setScores(Map<String, ScoreResults> scores) {
	//	this.scores = scores;
	//}

	public CfeScores getCfeScores() {
		return this.cfeScores;
	}

	public Map<String,CfeScore> getScores() {
		return this.cfeScores.getScores();
	}


	//---------------------------------------------------------------------
	// Getters and Setters
	//---------------------------------------------------------------------
	public void setSession(Map<String, Object> session) {
		this.session = session;
		
	}
	public Map<String, Object> getSession() {
		return session;
	}
	
	public String getScore() {
		return this.score;
	}
 
	public void setScore(String score) {
		this.score = score;
	}
	
	public boolean isOtherCompleted() {
		return otherCompleted;
	}

	public void setOtherCompleted(boolean otherCompleted) {
		this.otherCompleted = otherCompleted;
	}

}
