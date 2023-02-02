package cfe.calc;


import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
import cfe.utils.DataTable;

/**
 * Class for calculating prioritization scores.
 * 
 * @author Jim Mullen
 *
 */
public class PrioritizationScoresCalc {

    private static final Logger log = Logger.getLogger(PrioritizationScoresCalc.class.getName());
    private String score;
    private Map<String, ScoreResults> scores;
    private boolean otherCompleted = false;

    private Long discoveryId;
    private Double discoveryScoreCutoff;
    private String geneListFileName;

    private Long cfeResultsId;

    private Results results;

    private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();

    public CfeResults calculate(
            CfeResults discoveryResults,
            List<DiseaseSelector> diseaseSelectorsParam,
            List<cfe.enums.prioritization.ScoringWeights> weights,
            GeneListInput geneListInput,
            String geneListFileName,
            Double discoveryScoreCutoff  // Could be null, if gene list not calculated from discovery results
    ) throws Exception {

        log.info("Starting prioritization scoring.");
        log.info("Prioritization scoring Discovery ID: " + this.discoveryId);

        CfeResults cfeResults = null;
        
        try {
            
            this.diseaseSelectors     = diseaseSelectorsParam;
            this.discoveryScoreCutoff = discoveryScoreCutoff;
            this.geneListFileName     = geneListFileName;
            
            this.discoveryId = null;
            if (discoveryResults != null) {
                this.discoveryId = discoveryResults.getCfeResultsId();
                if (this.discoveryId == null) {
                    throw new Exception("Discovery results do not have an ID.");
                }
            }
            
            if (this.diseaseSelectors == null || this.diseaseSelectors.size() <= 0) {
                throw new Exception("No disease selectors provided.");
            }
            
            log.info("Number of disease selectors: " + this.diseaseSelectors.size());


            // If no gene list is specified, then all genes are used
            if (geneListInput == null) {
                geneListInput = new GeneListInput();
            }

            this.score = Scores.OTHER.getLabel();

            DiseaseSelection diseaseSelection = new DiseaseSelection(diseaseSelectors);

            this.results = Score.calculate(geneListInput, diseaseSelection, weights);
            Date generatedTime = new Date();


            // Generate a workbook with the prioritization scores
            XSSFWorkbook workbook = ReportGenerator.generateScoresWorkbook(
                        results, /* scores, */ weights, diseaseSelectors, geneListInput,
                        discoveryId, discoveryScoreCutoff, geneListFileName 
                        );

            // If the gene list was created from Discovery results, include
            // the Discovery results in the workbook
            LinkedHashMap<String,DataTable> discoveryDataTables = new LinkedHashMap<String,DataTable>();
            if (discoveryResults != null) {
                discoveryDataTables = discoveryResults.getDataTables();
            }

            cfeResults = new CfeResults();
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
                
                cfeResults.addCsvAndTextFiles(discoveryResults);
            }
            else {
                cfeResults.setResultsType(CfeResultsType.PRIORITIZATION_SCORES_ONLY);
            }
            CfeResultsService.save(cfeResults);
            this.cfeResultsId = cfeResults.getCfeResultsId();
        } catch (Exception exception) {
            String message = "Prioritization score calculation error: " + exception.getLocalizedMessage();
            throw new Exception(message);
        }

        return cfeResults;
    }
	
	/*	???
	public void validate() {
		
		//JGM log.info("Score: " + this.score);
		
		if (	!this.score.contains(Scores.SUICIDE.getLabel()) && 
				!this.score.contains(Scores.MOOD.getLabel()) &&
				!this.score.contains(Scores.PSYCHOSIS.getLabel()) &&
				!this.score.contains(Scores.OTHER.getLabel())
				)
			addActionError( "Score calculation for " + this.score + " is currently not supported." );

	}
	*/

	public Map<String, ScoreResults> getScores() {
		return scores;
	}

	public void setScores(Map<String, ScoreResults> scores) {
		this.scores = scores;
	}
	


	//---------------------------------------------------------------------
	// Getters and Setters
	//---------------------------------------------------------------------
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
