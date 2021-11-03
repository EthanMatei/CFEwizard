package cfe.action;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;
import com.opencsv.CSVReader;

import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.parser.PheneVisitParser;
import cfe.parser.ProbesetMappingParser;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.CohortTable;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.WebAppProperties;
import cfe.utils.WorkbookUtil;

public class ValidationScoringAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ValidationScoringAction.class);

	private Map<String, Object> webSession;


	private List<CfeResults> discoveryScores;
	private List<CfeResults> prioritizationScores;
    
    private Long validationDataId;	
	private Long prioritizationId;

	private String phene;
	
	    
	/**
	 * Select validation data (cohorts + discovery and prioritization scores)
	 * @return
	 * @throws Exception
	 */
	public String validationDataSelection() throws Exception {
	    String result = SUCCESS;
	    
	    if (!Authorization.isAdmin(webSession)) {
	        result = LOGIN;
	    } else {
	        this.discoveryScores  = CfeResultsService.getMetadata(CfeResultsType.ALL_COHORTS_PLUS_DISCOVERY_SCORES);
	        this.prioritizationScores = CfeResultsService.getMetadata(CfeResultsType.PRIORITIZATION_SCORES);
	    }
	    
	    return result;
	}
	
	public String validationScoringSpecification() throws Exception {
	    String result = SUCCESS;
	    
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else if (validationDataId == null) {
            this.setErrorMessage("No validation data selected.");
            result = ERROR;
        }
        else {
            try {

            }
            catch (Exception exception) {
                this.setErrorMessage(exception.getLocalizedMessage());
                result = ERROR;
            }
        }

	    return result;
	}
	
	/**
	 * Calculates the validation results.
	 * 
	 * @return the status of this action.
	 * 
	 * @throws Exception
	 */
	public String calculateValidationScores() throws Exception {
		String result = SUCCESS;
		
		log.info("Validation scoring phase started");
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    else {
            try {
                log.info("Starting validation scoring");
                
                String scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
                String scriptFile = new File(getClass().getResource("/R/Validation.R").toURI()).getAbsolutePath();
                
                String[] rScriptCommand = new String[12];
                rScriptCommand[0] = WebAppProperties.getRscriptPath();    // Full path of the Rscript command
                rScriptCommand[1] = scriptFile;     // The R script to run
                rScriptCommand[2] = scriptDir;
                rScriptCommand[3] = this.phene;
                

            }
            catch (Exception exception) {
                result = ERROR;
                if (exception != null) {
                    this.setErrorMessage("Validation scoring failed: " + exception.getLocalizedMessage());
                    String stackTrace = ExceptionUtils.getStackTrace(exception);
                    this.setExceptionStack(stackTrace);
                }
            }
        }

		return result;
	}
	

	
	public DataTable createValidationScoresInfoTable() throws Exception {
        DataTable infoTable = new DataTable("attribute");
        infoTable.insertColumn("attribute", 0, "");
        infoTable.insertColumn("value",  1,  "");
        
        ArrayList<String> row = new ArrayList<String>();
        row.add("CFE Version");
        row.add(VersionNumber.VERSION_NUMBER);
        infoTable.addRow(row);
        
        //row = new ArrayList<String>();
        //row.add("Time Scores Generated");
        //row.add(this.scoresGeneratedTime.toString());
        //infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("");
        row.add("");
        infoTable.addRow(row);
        
	    return infoTable;
	}
	


    
	/** 
	 * Executes the specified command and returns the output from the command.
	 *
	 * @param command the command to execute
	 * @return the output generated by the command
	 * @throws Exception
	 */
	public String runCommand(String[] command) throws Exception {
		StringBuilder output = new StringBuilder();
		
        //Process process = Runtime.getRuntime().exec(command);
		
		log.info("run command: " + String.join(" ", command));
        
		// This allows debugging:
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // redirect standard error to standard output
        
        log.info("*** Before process start");
        Process process = processBuilder.start();


	    BufferedReader reader = new BufferedReader(
	    new InputStreamReader(process.getInputStream()));

	    String line;
	    while ((line = reader.readLine()) != null) {
	        output.append(line + "\n");
	    }

	    log.info("*** Going to wait for process...");
	    int status = process.waitFor();
	    if (status != 0) {
            //throw new Exception("Command \"" + command + "\" exited with code " + status);
	    }
	    
	    reader.close();
        log.info("*** reader closed");
		return output.toString();
	}
	
	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public void validate() {
	}

	public Map<String, Object> getSession() {
		return webSession;
	}
	
    public Long getCfeResultsId() {
        return validationDataId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.validationDataId = cfeResultsId;
    }

    public String getPhene() {
        return phene;
    }

    public void setPhene(String phene) {
        this.phene = phene;
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

    public Long getValidationDataId() {
        return validationDataId;
    }

    public void setValidationDataId(Long validationDataId) {
        this.validationDataId = validationDataId;
    }

    public Long getPrioritizationId() {
        return prioritizationId;
    }

    public void setPrioritizationId(Long prioritizationId) {
        this.prioritizationId = prioritizationId;
    }

}
