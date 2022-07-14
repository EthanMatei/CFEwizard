package cfe.action.prioritization;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;


import cfe.action.BaseAction;

import cfe.enums.prioritization.Scores;
import cfe.model.CfeResults;
import cfe.model.CfeResultsFile;
import cfe.model.CfeResultsFileType;
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
import cfe.utils.DataTable;

/**
 * Action that calculates the scores.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class CalculateScores extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 0L;
	private static final Logger log = Logger.getLogger(CalculateScores.class.getName());
	private String score;
	private Map<String, ScoreResults> scores;
	private boolean otherCompleted = false;
	
	private Long discoveryId;
	private Double discoveryScoreCutoff;
	private String geneListFileName;
	
	private Long cfeResultsId;
	
	private Results results;
	
	private Map<String, Object> session;

	private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();

	public String execute() {
	    log.info("Starting prioritization scoring.");
	    log.info("************** prioritization socring Discovery ID: " + this.discoveryId);
	    String status = SUCCESS;

	    if (!Authorization.isLoggedIn(session)) {
	        status = LOGIN;
	    }
	    else {
	        try {
	            //-----------------------------------------------------
	            // Get input selections
	            //-----------------------------------------------------
	            Object diseasesObject = session.get("diseaseSelectors");
	            Object weightsObject  = session.get("weights");

	            if (diseasesObject == null || weightsObject == null) {
	                status = ERROR;
	                throw new Exception("Unfortunately, your session has expired. You will need to restart your score calculation.");
	            }
	            else {
	                diseaseSelectors = (List<DiseaseSelector>) diseasesObject;
	                log.info("Number of disease selectors: " + diseaseSelectors.size());

	                GeneListInput geneListInput = (GeneListInput) session.get("geneListInput");
	                if (geneListInput == null) {
	                    geneListInput = new GeneListInput();
	                }

	                List<cfe.enums.prioritization.ScoringWeights> weights
	                    = (List<cfe.enums.prioritization.ScoringWeights>) weightsObject;

	                score = Scores.OTHER.getLabel();

	                DiseaseSelection diseaseSelection = new DiseaseSelection(diseaseSelectors);

	                results = Score.calculate(geneListInput, diseaseSelection, weights);
	                Date generatedTime = new Date();
	               
	                
	                // Generate a workbook with the prioritization scores
	                XSSFWorkbook workbook = ReportGenerator.generateScoresWorkbook(
	                        results, scores, weights, diseaseSelectors, geneListInput,
	                        discoveryId, discoveryScoreCutoff, geneListFileName 
	                        );

	                // If the gene list was created from Discovery results, include
	                // the Discovery results in the workbook
	                CfeResults discoveryResults = null;
	                LinkedHashMap<String,DataTable> discoveryDataTables = new LinkedHashMap<String,DataTable>();
	                if (discoveryId != null && discoveryId > 0) {
	                    discoveryResults = CfeResultsService.get(discoveryId);
	                    discoveryDataTables = discoveryResults.getDataTables();
	                }
	                    
	                CfeResults cfeResults = new CfeResults();
	                cfeResults.setResultsSpreadsheet(workbook);
	                
	                LinkedHashMap<String,DataTable> dataTables = discoveryDataTables;
	                dataTables.putAll(cfeResults.getDataTables());
	                
	                workbook = DataTable.createWorkbook(dataTables);
                    cfeResults.setResultsSpreadsheet(workbook);
                    
	                cfeResults.setGeneratedTime(generatedTime);
	                if (discoveryResults != null) {
	                    // If there are discovery results, integrate discovery results into prioritization results
	                    cfeResults.setResultsType(CfeResultsType.PRIORITIZATION_SCORES);
	                    cfeResults.setPhene(discoveryResults.getPhene());
	                    cfeResults.setLowCutoff(discoveryResults.getLowCutoff());
	                    cfeResults.setHighCutoff(discoveryResults.getHighCutoff());
	                    CfeResultsFile cfeFile = discoveryResults.getFile(CfeResultsFileType.DISCOVERY_R_SCRIPT_LOG);
	                    if (cfeFile != null) {
	                        cfeResults.addTextFile(CfeResultsFileType.DISCOVERY_R_SCRIPT_LOG, cfeFile.getContentAsString());
	                    }
	                }
	                else {
	                    cfeResults.setResultsType(CfeResultsType.PRIORITIZATION_SCORES_ONLY);
	                }
	                CfeResultsService.save(cfeResults);
	                this.cfeResultsId = cfeResults.getCfeResultsId();

	                session.put("results", results);
	            }
	        } catch (Exception exception) {
	            if (status == SUCCESS) {
	                status = ERROR;
	            }
                String message = "Prioritization score calculation error: " + exception.getLocalizedMessage();
                log.severe(message);
                this.setErrorMessage(message);
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                this.setExceptionStack(stackTrace);
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


    public Long getDiscoveryId() {
        return discoveryId;
    }


    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }


    public Double getDiscoveryScoreCutoff() {
        return discoveryScoreCutoff;
    }

    public void setDiscoveryScoreCutoff(Double discoveryScoreCutoff) {
        this.discoveryScoreCutoff = discoveryScoreCutoff;
    }

    public String getGeneListFileName() {
        return geneListFileName;
    }

    public void setGeneListFileName(String geneListFileName) {
        this.geneListFileName = geneListFileName;
    }


    public Long getCfeResultsId() {
        return cfeResultsId;
    }


    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

}
