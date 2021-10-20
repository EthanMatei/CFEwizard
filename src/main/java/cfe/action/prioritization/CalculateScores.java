package cfe.action.prioritization;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;


import cfe.action.BaseAction;

import cfe.enums.prioritization.Scores;
import cfe.model.CfeResults;
import cfe.model.CfeResultsType;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.Score;
import cfe.model.prioritization.ScoreResults;
import cfe.model.prioritization.disease.DiseaseSelection;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.model.prioritization.reports.ReportGenerator;
import cfe.model.prioritization.results.Results;
import cfe.services.CfeResultsService;
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
	private Map<String, ScoreResults> scores;
	private boolean otherCompleted = false;
	
	private Results results;
	
	private Map<String, Object> session;
	
	private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();

	public String execute() {
		String status = SUCCESS;

		if (!Authorization.isLoggedIn(session)) {
			status = LOGIN;
		}
		else {
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
				List<cfe.enums.prioritization.ScoringWeights> weights = (List<cfe.enums.prioritization.ScoringWeights>) weightsObject;

				score = Scores.OTHER.getLabel();

				DiseaseSelection diseaseSelection = new DiseaseSelection(diseaseSelectors);
				try {
					results = Score.calculate(geneListInput, diseaseSelection, weights);
					Date generatedTime = new Date();
					
					// Generate a workbook with the prediction scores
					XSSFWorkbook workbook = ReportGenerator.generateScoresWorkbook(results, scores, weights, diseaseSelectors);
					
					CfeResults cfeResults = new CfeResults();
					cfeResults.setResultsSpreadsheet(workbook);
					cfeResults.setResultsType(CfeResultsType.PREDICTION_SCORES);
					cfeResults.setGeneratedTime(generatedTime);
					CfeResultsService.save(cfeResults);
				}
				catch (Exception exception) {
					this.setErrorMessage( exception.getMessage() );
					status = ERROR;	
				}

				session.put("results", results);
			}
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

	public Map<String, ScoreResults> getScores() {
		return scores;
	}

	public void setScores(Map<String, ScoreResults> scores) {
		this.scores = scores;
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
