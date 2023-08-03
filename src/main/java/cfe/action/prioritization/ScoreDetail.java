package cfe.action.prioritization;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.action.SessionAware;

import cfe.action.BaseAction;
import cfe.enums.prioritization.Scores;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.Score;
import cfe.model.prioritization.ScoreResults;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.model.prioritization.results.Results;

/**
 * Action class for displaying score details; WORK IN PROGRESS.
 * 
 * @author Jim Mullen
 *
 */
public class ScoreDetail extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 5103395746551169227L;
	private static final Log log = LogFactory.getLog(ScoreDetail.class);
	private String score;
	private Map<String, ScoreResults> scores;
	private String filename = null;
	private boolean otherCompleted = false;
	
	private Results results;
	
	private Map<String, Object> session;
	
	private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();

	
	public String getFilename()	{
		return this.filename;
	}

	private InputStream fileInputStream;
	 
	public InputStream getFileInputStream() {
		return fileInputStream;
	}

	public String execute() {

		String status = SUCCESS;

		//-----------------------------------------------------
		// Get input selections
		//-----------------------------------------------------
		Object diseasesObject = session.get("diseaseSelectors");
		Object weightsObject  = session.get("weights");
		
		if (diseasesObject == null || weightsObject == null) {
			this.setErrorMessage("Unfortunately, your session has expired. You will need to restart your score calculation.");
			status = ERROR;
		}
		else {
		    diseaseSelectors = (List<DiseaseSelector>) diseasesObject;
		    GeneListInput geneListInput = (GeneListInput) session.get("geneListInput");
		    List<cfe.enums.prioritization.ScoringWeights> weights
		        = (List<cfe.enums.prioritization.ScoringWeights>) weightsObject;
		
		    score = Scores.OTHER.getLabel();
		
		    DiseaseSelection diseaseSelection = new DiseaseSelection(diseaseSelectors);
		    try {
	    	    results = Score.calculate(geneListInput, diseaseSelection, weights);
		    }
		    catch (Exception exception) {
			    this.setErrorMessage( exception.getMessage() );
		        status = ERROR;	
		    }
		
		    //this.scores = ScoringDataService.getScores(score, diseaseSelectors);

		    session.put("scorefile", filename);
		    //session.put("scores", scores);
		    session.put("results", results);
		}
		
		return status;
	}
	
		
	public void validate() {
		
		//JGM log.info("Score: " + this.score);
		
		if (	!this.score.contains(Scores.SUICIDE.getLabel()) && 
				!this.score.contains(Scores.MOOD.getLabel()) &&
				!this.score.contains(Scores.PSYCHOSIS.getLabel()) &&
				!this.score.contains(Scores.OTHER.getLabel())
				)
			addActionError( "Score calculation for " + this.score + " is currently not supported." );

	}
    
	
	public void withSession(Map<String, Object> session) {
        this.session = session;
        
    }


	//---------------------------------------------------------------------
	// Getters and Setters
	//---------------------------------------------------------------------
    
    public Map<String, ScoreResults> getScores() {
        return scores;
    }

    public void setScores(Map<String, ScoreResults> scores) {
        this.scores = scores;
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

	public List<DiseaseSelector> getDiseaseSelectors() {
		return diseaseSelectors;
	}

	public void setDiseaseSelectors(List<DiseaseSelector> diseaseSelectors) {
		this.diseaseSelectors = diseaseSelectors;
	}

	public Results getResults() {
		return results;
	}

	public void setResults(Results results) {
		this.results = results;
	}

}
