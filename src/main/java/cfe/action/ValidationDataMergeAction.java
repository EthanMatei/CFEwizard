package cfe.action;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.action.SessionAware;

import cfe.model.CfeResults;
import cfe.model.CfeResultsNewestFirstComparator;
import cfe.model.CfeResultsType;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.DataTable;

public class ValidationDataMergeAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ValidationDataMergeAction.class.getName());

	private Map<String, Object> webSession;

    private String errorMessage;

	private List<CfeResults> discoveryScores;
	private List<CfeResults> prioritizationScores;
    
    private Long discoveryId;	
	private Long prioritizationId;

	private Date scoresGeneratedTime;

    private Long cfeResultsId;
    private CfeResults cfeResults;
	

    public ValidationDataMergeAction() {
        this.setCurrentTab("Other Functions");
        this.setCurrentSubTab("Merge with Discovery Scores");
    }
    
	/**
	 * Select validation data (cohorts + discovery and prioritization scores)
	 * @return
	 * @throws Exception
	 */
	public String mergeDataSelection() throws Exception {
	    String result = SUCCESS;
	    
	    if (!Authorization.isAdmin(webSession)) {
	        result = LOGIN;
	    } else {
	        this.discoveryScores  = CfeResultsService.getMetadata(CfeResultsType.DISCOVERY_SCORES);
	        Collections.sort(this.discoveryScores, new CfeResultsNewestFirstComparator());
	           
	        this.prioritizationScores = CfeResultsService.getMetadata(CfeResultsType.PRIORITIZATION_SCORES_ONLY);
	        Collections.sort(this.prioritizationScores, new CfeResultsNewestFirstComparator());
	    }
	    
	    return result;
	}
	
	public String mergeData() throws Exception {
	    String result = SUCCESS;
	    
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else if (discoveryId == null) {
            this.setErrorMessage("No discovery scores selected.");
            result = INPUT;
        }
        else if (prioritizationId == null) {
            this.setErrorMessage("No prioritization scores selected.");
            result = INPUT;
        }
        else {
            try {

                ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
                // NEED TO GET PHENE FROM validationDataId
                
                CfeResults discoveryData = CfeResultsService.get(discoveryId);
                if (discoveryData == null) {
                    throw new Exception("Could not get discovery data with ID " + discoveryId + ".");
                }
                
                CfeResults prioritizationData = CfeResultsService.get(prioritizationId);
                if (prioritizationData == null) {
                    throw new Exception("Could not get prioritization data with ID " + prioritizationId + ".");
                }
                
                LinkedHashMap<String,DataTable> dataTables = discoveryData.getDataTables();
                dataTables.putAll(prioritizationData.getDataTables());
                
                XSSFWorkbook workbook = DataTable.createWorkbook(dataTables);
                if (workbook == null) {
                    throw new Exception("Could not create workbook for discovery and prioritization data.");
                }
                
                String phene = discoveryData.getPhene();
                Double lowCutoff = discoveryData.getLowCutoff();
                Double highCutoff = discoveryData.getHighCutoff();

                this.scoresGeneratedTime = new Date();
                
                // Save the results in the database
                this.cfeResults = new CfeResults(
                        workbook,
                        CfeResultsType.PRIORITIZATION_SCORES,
                        this.scoresGeneratedTime,
                        phene,
                        lowCutoff, highCutoff
                );
                
                this.cfeResults.setDiscoveryRScriptLog(discoveryData.getDiscoveryRScriptLog());
                log.info("Added discovery R script log text to cfeResults.");
                
                this.cfeResults.addCsvAndTextFiles(discoveryData);
                
                CfeResultsService.save(this.cfeResults);
                
                this.cfeResultsId = this.cfeResults.getCfeResultsId();
                log.info("CFE Results ID for merged data = " + cfeResultsId);
                if (this.cfeResultsId < 1) {
                    throw new Exception("Merge results id is not >= 1: " + cfeResultsId);
                }
            }
            catch (Exception exception) {
                if (result == SUCCESS) result = ERROR;
                String message = "Validation scoring specification failed: " + exception.getLocalizedMessage();
                this.setErrorMessage(message);
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                this.setExceptionStack(stackTrace);
                log.severe(message);
                log.severe(stackTrace);
            }
        }

        log.info("merge data status = \"" + result + "\"");
	    return result;
	}
	

	
	public void withSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public void validate() {
	}

	public Map<String, Object> getSession() {
		return webSession;
	}
	
    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    public CfeResults getCfeResults() {
        return cfeResults;
    }

    public void setCfeResults(CfeResults cfeResults) {
        this.cfeResults = cfeResults;
    }

    public Long getPrioritizationId() {
        return prioritizationId;
    }

    public void setPrioritizationId(Long prioritizationId) {
        this.prioritizationId = prioritizationId;
    }
    
    public Long getDiscoveryId() {
        return discoveryId;
    }

    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getScoresGeneratedTime() {
        return scoresGeneratedTime;
    }

    public void setScoresGeneratedTime(Date scoresGeneratedTime) {
        this.scoresGeneratedTime = scoresGeneratedTime;
    }

    public List<CfeResults> getDiscoveryScores() {
        return discoveryScores;
    }

    public void setDiscoveryScores(List<CfeResults> discoveryScores) {
        this.discoveryScores = discoveryScores;
    }

    public List<CfeResults> getPrioritizationScores() {
        return prioritizationScores;
    }

    public void setPrioritizationScores(List<CfeResults> prioritizationScores) {
        this.prioritizationScores = prioritizationScores;
    }

}
