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

    
    private File probesetMappingDb;
    private String probesetMappingDbContentType;
    private String probesetMappingDbFileName;

	private List<CfeResults> discoveryScores;
	private List<CfeResults> prioritizationScores;
    
    private Long validationDataId;	
	private Long prioritizationId;
	
	private String validationMasterSheetFile;
	private String predictorListFile;

	private String phene;
	
	private double scoreCutoff = 6.0;
	
	private List<String> genesNotFoundInPrioritization = new ArrayList<String>();
	
	    
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
        //else if (this.probesetMappingDb == null || this.probesetMappingDbFileName == null) {
        //    this.setErrorMessage("No probeset to gene mapping database file was specified.");
        //    result = INPUT;
        //}
        else if (validationDataId == null) {
            this.setErrorMessage("No validation data selected.");
            result = INPUT;
        }
        else if (prioritizationId == null) {
            this.setErrorMessage("No prioritization results selected.");
            result = INPUT;
        }
        else {
            try {
                //------------------------------------------------------------
                // Get the probeset to mapping information
                //------------------------------------------------------------
                /*
                String key = "Probe Set ID";
                DataTable probesetMapping = new DataTable(key);
                
                ProbesetMappingParser dbParser = new ProbesetMappingParser(this.probesetMappingDb.getAbsolutePath());
                Table table = dbParser.getMappingTable();
                probesetMapping.initializeToAccessTable(table);
                */
                
                this.validationMasterSheetFile = this.createValidationMasterSheet(this.validationDataId, this.prioritizationId);
                this.predictorListFile = this.createPredictorList(this.validationDataId, this.prioritizationId);
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
	
	
	public String createValidationMasterSheet(Long validationDataId, Long prioritizationId) throws Exception
	{
        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
        
	    CfeResults cfeResults = CfeResultsService.get(validationDataId);
	    XSSFWorkbook workbook = cfeResults.getResultsSpreadsheet();
	    
	    String key = "Subject Identifiers.PheneVisit";
	    
	    XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.VALIDATION_COHORT);
	    DataTable masterSheet = new DataTable(key);
	    masterSheet.initializeToWorkbookSheet(sheet);
	    
	    masterSheet.deleteColumn("TestingCohort");
	    
	    // Create dx column
	    masterSheet.insertColumn("dx", 9, "");
        for (int i = 0; i < masterSheet.getNumberOfRows(); i++) {
            String gender = masterSheet.getValue(i, "Gender(M/F)");
            String dxCode = masterSheet.getValue(i, "DxCode");
            masterSheet.setValue(i, "dx", gender + "-" + dxCode);
        }
        
        // Create Biomarkers column
	    masterSheet.addColumn("Biomarkers",  "");
	    for (int i = 0; i < masterSheet.getNumberOfRows(); i++) {
	        String pheneVisit = masterSheet.getValue(i, key);
	        masterSheet.setValue(i, "Biomarkers", pheneVisit);
	    }
	    
	    //-------------------------------------------------------
	    // Write the master sheet to a CSV file
	    //-------------------------------------------------------
	    String masterSheetCsv = masterSheet.toCsv();
        File validationMasterSheetCsvTmp = File.createTempFile("validation-master-sheet-",  ".csv");
        if (masterSheetCsv != null) {
            FileUtils.write(validationMasterSheetCsvTmp, masterSheetCsv, "UTF-8");
        }
        return validationMasterSheetCsvTmp.getAbsolutePath();
	}

	public String createPredictorList(Long validationDataId, Long prioritizationId) throws Exception
	{
	    ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

	    Map<String,Double> prioritizationScores = this.getPrioritizationScores(prioritizationId);
	    
	    //----------------------------------------
	    // Get the discovery scores
	    //----------------------------------------
	    CfeResults discoveryScoresAndCohorts = CfeResultsService.get(validationDataId);
	    XSSFWorkbook workbook = discoveryScoresAndCohorts.getResultsSpreadsheet();
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_SCORES);
        
        DataTable discoveryScores = new DataTable("Probe Set ID");
        discoveryScores.initializeToWorkbookSheet(sheet);
	    
        
	    String key = "Predictor";

	    DataTable predictorList = new DataTable(key);
	    predictorList.addColumn(key, "");
	    predictorList.addColumn("Direction", "");
        predictorList.addColumn("Male", "");
        predictorList.addColumn("Female", "");
        predictorList.addColumn("BP", "");
        predictorList.addColumn("MDD", "");
        predictorList.addColumn("SZ", "");
        predictorList.addColumn("SZA", "");
        predictorList.addColumn("PTSD", "");
        predictorList.addColumn("PSYCHOSIS", "");
        predictorList.addColumn("All", "");
        
        for (int i = 0; i < discoveryScores.getNumberOfRows(); i++) {
            double deScore = 0.0;
            double prioritizationScore = 0.0;
            double rawDeScore = 0.0;
            double dePercentile = 0.0;
            
            String gene = discoveryScores.getValue(i, "Genecards Symbol");
            String probeset = discoveryScores.getValue(i, "Probe Set ID");
            String rawDeScoreString = discoveryScores.getValue(i, "DEscores");
            String dePercentileString = discoveryScores.getValue(i, "DE Percentile");
            String deScoreString = discoveryScores.getValue(i, "DE Score");
            
            try {
                rawDeScore = Double.parseDouble(rawDeScoreString);
            }
            catch (NumberFormatException exception) {
                rawDeScore = 0.0;
            }
            
            try {
                dePercentile = Double.parseDouble(dePercentileString);
            }
            catch (NumberFormatException exception) {
                dePercentile = 0.0;
            }
                        
            try {
                deScore = Double.parseDouble(deScoreString);
            }
            catch (NumberFormatException exception) {
                deScore = 0.0;
            }
        
            if (prioritizationScores.containsKey(gene)) {
                prioritizationScore = prioritizationScores.get(gene);   

                double score = deScore + prioritizationScore;
                if (dePercentile >= 0.3333333333 && score > this.scoreCutoff) {
                    String direction = "I";
                    if (rawDeScore < 0.0) {
                        direction = "D";
                    }

                    ArrayList<String> row = new ArrayList<String>();
                    row.add(gene + "biom" + probeset);
                    row.add(direction);
                    row.add("0"); // Male
                    row.add("0"); // Female
                    row.add("0"); // BP
                    row.add("0"); // MDD
                    row.add("0"); // SZ
                    row.add("0"); // SZA
                    row.add("0"); // PTSD
                    row.add("0"); // PSYCHOSIS
                    row.add("1"); // All

                    predictorList.addRow(row);
                }
            }
            else {
                this.genesNotFoundInPrioritization.add(gene);
            }
        }
        
	    //-------------------------------------------------------
	    // Write the master sheet to a CSV file
	    //-------------------------------------------------------
	    String predictorListCsv = predictorList.toCsv();
	    File predictorListCsvTmp = File.createTempFile("validation-master-sheet-",  ".csv");
	    if (predictorListCsv != null) {
	        FileUtils.write(predictorListCsvTmp, predictorListCsv, "UTF-8");
	    }
	    return predictorListCsvTmp.getAbsolutePath();
	}
	
    public Map<String,Double> getPrioritizationScores(Long prioritizationId) throws Exception
    {
        Map<String,Double> scores = new HashMap<String,Double>();
        
        CfeResults cfeResults = CfeResultsService.get(prioritizationId);
        XSSFWorkbook workbook = cfeResults.getResultsSpreadsheet();
        
        String key = "Gene";
        
        XSSFSheet sheet = workbook.getSheet("CFG Wizard Scores");
        DataTable cfgScoresSheet = new DataTable(key);
        cfgScoresSheet.initializeToWorkbookSheet(sheet);

        // Get scores
        for (int i = 0; i < cfgScoresSheet.getNumberOfRows(); i++) {
            String gene  = cfgScoresSheet.getValue(i, "Gene");
            String score = cfgScoresSheet.getValue(i, "Score");
            
            String[] geneList = gene.split(",");  // If list, change to single upper-case value
            gene = geneList[0].toUpperCase();
            
            Double scoreValue = 0.0;
            try {
                scoreValue = Double.parseDouble(score);
            } catch (NumberFormatException exception) {
                scoreValue = 0.0;
            }
            
            scores.put(gene, scoreValue);
        }

        return scores;
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
    
    public File getProbesetMappingDb() {
        return probesetMappingDb;
    }

    public void setProbesetMappingDb(File probesetMappingDb) {
        this.probesetMappingDb = probesetMappingDb;
    }

    public String getProbesetMappingDbContentType() {
        return probesetMappingDbContentType;
    }

    public void setProbesetMappingDbContentType(String probesetMappingDbContentType) {
        this.probesetMappingDbContentType = probesetMappingDbContentType;
    }

    public String getProbesetMappingDbFileName() {
        return probesetMappingDbFileName;
    }

    public void setProbesetMappingDbFileName(String probesetMappingDbFileName) {
        this.probesetMappingDbFileName = probesetMappingDbFileName;
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

    public String getValidationMasterSheetFile() {
        return validationMasterSheetFile;
    }

    public void setValidationMasterSheetFile(String validationMasterSheetFile) {
        this.validationMasterSheetFile = validationMasterSheetFile;
    }

    public String getPredictorListFile() {
        return predictorListFile;
    }

    public void setPredictorListFile(String predictorListFile) {
        this.predictorListFile = predictorListFile;
    }

    public double getScoreCutoff() {
        return scoreCutoff;
    }

    public void setScoreCutoff(double scoreCutoff) {
        this.scoreCutoff = scoreCutoff;
    }

    public List<String> getGenesNotFoundInPrioritization() {
        return genesNotFoundInPrioritization;
    }

    public void setGenesNotFoundInPrioritization(List<String> genesNotFoundInPrioritization) {
        this.genesNotFoundInPrioritization = genesNotFoundInPrioritization;
    }

}
