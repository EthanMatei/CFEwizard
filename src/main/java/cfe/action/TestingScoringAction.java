package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.opencsv.CSVReader;

import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.WebAppProperties;


public class TestingScoringAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(TestingScoringAction.class);

	private Map<String, Object> webSession;

	private Long testingDataId;
	private List<CfeResults> cfeResults;
	
	private CfeResults testingData;
    private String scriptDir;
    private String scriptFile;
    private String geneExpressionCsv;
    private double scoreCutoff;
    private List<String> genesNotFoundInPrioritization = new ArrayList<String>();
	
	/**
	 * Select testing data
	 * @return
	 * @throws Exception
	 */
	public String selectTestingData() throws Exception {
	    String result = SUCCESS;
	    
	    if (!Authorization.isAdmin(webSession)) {
	        result = LOGIN;
	    } else {
	        this.cfeResults = CfeResultsService.getMetadata(
	                CfeResultsType.ALL_COHORTS_PLUS_DISCOVERY_SCORES,
	                CfeResultsType.ALL_COHORTS_PLUS_VALIDATION_SCORES
	            );
	    }
	    
	    return result;
	}
	
	public String specifyTestingScoringOptions() throws Exception {
	    String result = SUCCESS;
	    
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else if (testingDataId == null) {
            this.setErrorMessage("No testing data selected.");
            result = INPUT;
        }
        else {
            testingData = CfeResultsService.get(testingDataId);
            if (testingData == null) {
                result = ERROR;
                this.setErrorMessage("Unable to retrieve testing data for ID " + testingDataId + ".");
            }
        }

	    return result;
	}
	
	/**
	 * Calculates the testing results.
	 * 
	 * @return the status of this action.
	 * 
	 * @throws Exception
	 */
	public String calculateTestingScores() throws Exception {
		String result = SUCCESS;
		
		log.info("Testing scoring phase started");
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    else {
            try {
                log.info("Starting testing scoring");
                
                this.scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
                this.scriptFile = new File(getClass().getResource("/R/Predictions-Script-ALL-Dx.R").toURI()).getAbsolutePath();
                
                String[] rScriptCommand = new String[12];
                rScriptCommand[0] = WebAppProperties.getRscriptPath();
                rScriptCommand[1] = this.scriptFile;
                rScriptCommand[2] = scriptDir;
            }
            catch (Exception exception) {
                result = ERROR;
                if (exception != null) {
                    this.setErrorMessage("Testing scoring failed: " + exception.getLocalizedMessage());
                    String stackTrace = ExceptionUtils.getStackTrace(exception);
                    this.setExceptionStack(stackTrace);
                }
            }
        }

		return result;
	}
	
    public String createTestingMasterSheet(Long validationDataId, DataTable predictorList, File geneExpressionCsvFile)
            throws Exception
    {
        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
        
        CfeResults cfeResults = CfeResultsService.get(validationDataId);
        if (cfeResults == null) {
            throw new Exception("Could not get saved results for ID " + validationDataId + ".");
        }
        
        XSSFWorkbook workbook = cfeResults.getResultsSpreadsheet();
        
        if (workbook == null) {
            throw new Exception("Unable to get results spreadsheet from database for results ID "
                + validationDataId + ".");
        }
        
        String key = "Subject Identifiers.PheneVisit";
        
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.CLINICAL_COHORT);
        if (sheet == null) {
            sheet = workbook.getSheet(CfeResultsSheets.VALIDATION_COHORT); // check for old deprecated name
            if (sheet == null) {
                throw new Exception("Could not find \"" + CfeResultsSheets.CLINICAL_COHORT + "\" sheet in results workbook.");
            }
        }
        
        DataTable masterSheet = new DataTable(key);
        masterSheet.initializeToWorkbookSheet(sheet);
        
        masterSheet.deleteColumn("TestingCohort");
        
        // Create dx column (gender + diagnosis code)
        masterSheet.insertColumn("dx", 9, "");
        for (int i = 0; i < masterSheet.getNumberOfRows(); i++) {
            String gender = masterSheet.getValue(i, "Gender(M/F)");
            String dxCode = masterSheet.getValue(i, "DxCode");
            masterSheet.setValue(i, "dx", gender + "-" + dxCode);
        }
        
        // Create Biomarkers column (contains pheneVisit as value)
        masterSheet.addColumn("Biomarkers",  "");
        for (int i = 0; i < masterSheet.getNumberOfRows(); i++) {
            String pheneVisit = masterSheet.getValue(i, key);
            masterSheet.setValue(i, "Biomarkers", pheneVisit);
        }
        
        // Add predictor columns (combination of gene cards symbol, "biom" and pro)beset)
        for (int i = 0; i < predictorList.getNumberOfRows(); i++) {
            String predictor = predictorList.getValue(i, "Predictor");
            masterSheet.addColumn(predictor, "");
        }
        
        // Set "Validation Cohort" to 1 where "ValCategory" is "Low" or "High"
        for (int rowIndex = 0; rowIndex < masterSheet.getNumberOfRows(); rowIndex++) {
            String valCategory = masterSheet.getValue(rowIndex, "ValCategory");
            if (valCategory.equalsIgnoreCase("Low") || valCategory.equalsIgnoreCase("High")) {
                masterSheet.setValue(rowIndex, "ValidationCohort", "1");
            }
        }

        //---------------------------------------------------------------------------
        // Read in the gene expression CSV file. It has the following format:
        //
        // ID            <phene-visit> <phene-visit> <phene-visit>
        // <probeset>    <value>       <value>       <value>
        // <probeset>    <value>       <value>       <value>
        //---------------------------------------------------------------------------
        FileReader filereader = new FileReader(geneExpressionCsv);
        CSVReader csvReader = new CSVReader(filereader);
        
        String[] header = csvReader.readNext();
        
        // Create map from probesets to predictors
        HashMap<String,String> probesetToPredictorMap = new HashMap<String,String>();
        List<String> columns = masterSheet.getColumnNames();
        for (String column: columns) {
            
            // If this is a predictor
            if (column.contains("biom")) {
                String predictor = column;
                predictor = predictor.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
                predictor = predictor.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);
                
                String[] mapValues = column.split("biom");
                String probeset = mapValues[1];
                probeset = probeset.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
                probeset = probeset.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);        
                
                probesetToPredictorMap.put(probeset,  predictor);
            }
        }
        
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            String probeset = row[0];
            probeset = probeset.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
            probeset = probeset.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);
            
            String predictor = probesetToPredictorMap.get(probeset);

            if (predictor != null && !predictor.isEmpty()) {
                for (int i = 1; i < row.length; i++) {
                    String pheneVisit = header[i];
                    String value = row[i];
                    
                    predictor = predictor.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
                    predictor = predictor.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);

                    // Set mastersheet values
                    //
                    // ... Biomarker     <gene>biom<probeset>  <gene>biom<probeset> ...
                    // ... <phene-visit> <value>               <value>
                    // ... <phene-visit> <value>               <value>
                    int rowIndex = masterSheet.getRowIndex("Biomarkers", pheneVisit);
                    if (rowIndex >= 0) {
                        masterSheet.setValue(rowIndex, predictor, value);    
                    }
                }
            }
        }
        csvReader.close();
        
        //-----------------------------)--------------------------
        // Write the master sheet to a CSV file
        //-------------------------------------------------------
        String masterSheetCsv = masterSheet.toCsv();
        File validationMasterSheetCsvTmp = FileUtil.createTempFile("validation-master-sheet-",  ".csv");
        if (masterSheetCsv != null) {
            FileUtils.write(validationMasterSheetCsvTmp, masterSheetCsv, "UTF-8");
        }
        else {
            throw new Exception("Unable to create validation mastersheet.");
        }
        
        return validationMasterSheetCsvTmp.getAbsolutePath();
    }

    
    public DataTable createPredictorList(Long validationDataId, Long prioritizationId) throws Exception
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
        predictorList.addColumn("PSYCH", "");
        predictorList.addColumn("PSYCHOSIS", "");
        predictorList.addColumn("All", "");
        
        for (int i = 0; i < discoveryScores.getNumberOfRows(); i++) {
            double deScore = 0.0;
            double prioritizationScore = 0.0;
            double rawDeScore = 0.0;
            double dePercentile = 0.0;
            
            String gene = discoveryScores.getValue(i, "Genecards Symbol");
            String probeset = discoveryScores.getValue(i, "Probe Set ID");
            String rawDeScoreString = discoveryScores.getValue(i, "DE Raw Score");
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
                    
                    String predictor = gene + "biom" + probeset;
                    predictor = predictor.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
                    predictor = predictor.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);
                    
                    row.add(predictor);
                    row.add(direction);
                    row.add("0"); // Male
                    row.add("0"); // Female
                    row.add("0"); // BP
                    row.add("0"); // MDD
                    row.add("0"); // SZ
                    row.add("0"); // SZA
                    row.add("0"); // PTSD
                    row.add("0"); // PSYCH
                    row.add("0"); // PSYCHOSIS
                    row.add("1"); // All

                    predictorList.addRow(row);
                }
            }
            else {
                this.genesNotFoundInPrioritization.add(gene);
            }
        }
        
        return predictorList;
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
    	
	public DataTable createTestingScoresInfoTable() throws Exception {
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

    public Long getTestingDataId() {
        return testingDataId;
    }

    public void setTestingDataId(Long testingDataId) {
        this.testingDataId = testingDataId;
    }

    public List<CfeResults> getCfeResults() {
        return cfeResults;
    }

    public void setCfeResults(List<CfeResults> cfeResults) {
        this.cfeResults = cfeResults;
    }

    public CfeResults getTestingData() {
        return testingData;
    }

    public void setTestingData(CfeResults testingData) {
        this.testingData = testingData;
    }

    public String getGeneExpressionCsv() {
        return geneExpressionCsv;
    }

    public void setGeneExpressionCsv(String geneExpressionCsv) {
        this.geneExpressionCsv = geneExpressionCsv;
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
