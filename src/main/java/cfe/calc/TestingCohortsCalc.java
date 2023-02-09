package cfe.calc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Table;

import cfe.model.CfeResults;
import cfe.model.CfeResultsFileType;
import cfe.model.CfeResultsNewestFirstComparator;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.parser.AccessDatabaseParser;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.PheneCondition;
import cfe.utils.WebAppProperties;

public class TestingCohortsCalc {

    private static final Logger log = Logger.getLogger(TestingCohortsCalc.class.getName());
    
	public static final Long RANDOM_SEED = 10972359723095792L;
	
	public static final String ACTUARIAL_TABLE_NAME = "Actuarial and Subject Info";
	public static final String HOSPITALIZATIONS_TABLE_NAME = "Hospitalizations Follow-up Database";
	
	private List<CfeResults> validationResultsList;
	private CfeResults validationResults;
	
	private ArrayList<String> phenes;
	
	private Map<String,ArrayList<ColumnInfo>> pheneMap;
	
	private Long validationId;
	
	private String admissionPhene;
    
	private String errorMessage;
	
	private String validationConstraint1;
	private String validationConstraint2;
	private String validationConstraint3;
	
	/*
	private String phene1;
	private String phene2;
	private String phene3;
	
	private String operator1;
	private String operator2;
	private String operator3;
	
	private String value1;
	private String value2;
	private String value3;
	*/
	
	/*
	private String clinicalPhene;
	private Integer clinicalHighCutoff;
	*/
	
	private String discoveryPhene;
	private String discoveryPheneTable;
	private Double discoveryLowCutoff;
	private Double discoveryHighCutoff;
	
	private String percentInValidationCohort;
	
	TreeSet<String> cohortSubjects;
	TreeSet<String> validationSubjects;
	TreeSet<String> testingSubjects;
	
	int numberOfValidationSubjects = 0;
	int numberOfTestingSubjects    = 0;
	
	private Long cfeResultsId;
    private String cohortCheckCsvFileName;
	
    // Fllow-up MS Access database
    
    private File followUpDb;
    private String followUpDbFileName;

    private String scoringDataFileName;
    private String pheneVisitsFileName;
    
    private List<String> admissionReasons;
    private String predictionCohortCreationCommand;
    private String scriptOutput;
    private String outputFile;
    private String scriptOutputFile;
    
	public TestingCohortsCalc() {
	    this.cohortSubjects     = new TreeSet<String>();
	    this.validationSubjects = new TreeSet<String>();
	    this.testingSubjects    = new TreeSet<String>();
	    
	    admissionReasons = new ArrayList<String>();
	    admissionReasons.add("Suicide");
	    admissionReasons.add("Violence");
	    admissionReasons.add("Depression");
	    admissionReasons.add("Mania");
	    admissionReasons.add("Hallucinations");
	    admissionReasons.add("Delusion");
	    admissionReasons.add("Other Psychosis"); 
	    admissionReasons.add("Anxiety"); 
	    admissionReasons.add("Stress");
	    admissionReasons.add("Alcohol");
	    admissionReasons.add("Drugs");
	    admissionReasons.add("Pain");
	    Collections.sort(admissionReasons);
	}
	

	/** 
	 * WORK IN PROGRESS
	 *
	 */
    public CfeResults calculate(
            CfeResults validationResults,
            File followUpDb,
            String followUpDbFileName
    ) throws Exception {

        CfeResults cfeResults = null;
 
        try {

            this.validationResults = validationResults;
            this.followUpDb        = followUpDb;
            this.followUpDbFileName = followUpDbFileName;
            
            if (this.validationResults == null) {
                throw new Exception("No validation results specified.");
            }

            this.validationId = validationResults.getCfeResultsId();
            if (this.validationId == null) {
                String errorMessage = "Validation resultes do not have an ID.";
                log.severe(errorMessage);
                throw new Exception(errorMessage);
            }

            log.info("Testing follow-up database file name: " + this.followUpDbFileName);

            ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

            XSSFWorkbook validationWorkbook = validationResults.getResultsSpreadsheet();
            if (validationWorkbook == null) {
                String errorMessage = "Validation results do not have a spreadsheet.";
                log.severe(errorMessage);;
                throw new Exception(errorMessage);
            }
            
            XSSFSheet sheet = validationWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
            if (sheet == null) {
                String errorMessage = "Validation results spreadsheet dies not have a \""
                        + CfeResultsSheets.COHORT_DATA + "\" sheet.";
                log.severe(errorMessage);
                throw new Exception(errorMessage);
            }
            
            CohortDataTable cohortData = new CohortDataTable();
            cohortData.initializeToWorkbookSheet(sheet);
            cohortData.setKey("Subject Identifiers.PheneVisit");


            // NEED TO RESET THIS TO JUST GET THE INFORMATION ???
            /*
                List<TreeSet<String>> results = cohortData.setValidationAndTestingCohorts(
                        discoveryPhene, discoveryLowCutoff, discoveryHighCutoff, 
                        // clinicalPhene, clinicalHighCutoff,
                        pheneConditions, percentInValidation
                        );

                this.validationSubjects = results.get(0);
                this.testingSubjects    = results.get(1);
             */

            this.validationSubjects = cohortData.getCohort("validation");
            TreeSet<String> clinicalSubjects   = cohortData.getCohort("clinical");   // for old workbooks
            validationSubjects.addAll(clinicalSubjects);
            this.testingSubjects    = cohortData.getCohort("testing");

            this.numberOfValidationSubjects = this.validationSubjects.size();
            this.numberOfTestingSubjects    = this.testingSubjects.size();

            List<String> subjects = new ArrayList<String>();
            subjects.addAll(cohortSubjects);

            //-----------------------------------------------------------
            // Process hospitalization data
            //-----------------------------------------------------------
            this.processHospitalizations();

            //--------------------------------------------------
            // Create phene visits CSV file
            //--------------------------------------------------
            this.createPheneVistsCsvFile();

            //------------------------------------------------------------
            // Run Python script
            //------------------------------------------------------------
            String scriptFile = new File(getClass().getResource("/python/CohortCreation.py").toURI()).getAbsolutePath();
            String tempDir = WebAppProperties.getTempDir();

            String[] pythonScriptCommand = new String[6];
            pythonScriptCommand[0] = WebAppProperties.getPython3Path();
            pythonScriptCommand[1] = scriptFile;     // Python script to run
            pythonScriptCommand[2] = this.scoringDataFileName;
            pythonScriptCommand[3] = this.pheneVisitsFileName;
            pythonScriptCommand[4] = this.admissionPhene;
            pythonScriptCommand[5] = tempDir;

            log.info("PYTHON CREATE COHORT COMMAND: " + String.join(" ", pythonScriptCommand));

            this.predictionCohortCreationCommand = "\"" + String.join("\" \"",  pythonScriptCommand) + "\"";

            this.scriptOutput = this.runCommand(pythonScriptCommand);

            File tempFile = FileUtil.createTempFile("prediction-cohort-creation-python-script-output-", ".txt");
            FileUtils.write(tempFile, scriptOutput, "UTF-8");
            this.scriptOutputFile = tempFile.getAbsolutePath();


            //---------------------------------------------------------------
            // Get the output file path
            //---------------------------------------------------------------
            String outputFilePatternString = "Output file created: (.*)";

            Pattern outputFilePattern = Pattern.compile(outputFilePatternString);

            String lines[] = scriptOutput.split("\\r?\\n");
            for (String line: lines) {
                Matcher outputMatcher = outputFilePattern.matcher(line);

                if (outputMatcher.find()) {
                    this.outputFile = outputMatcher.group(1).trim();
                }             
            }

            if (this.outputFile == null || this.outputFile.isEmpty()) {
                throw new Exception("Could not find output file for Python Cohort Creation script.");
            }

            //Path path = Paths.get(outputFile);
            //String fileName = path.getFileName().toString();
            //this.outputFile = System.getProperty("java.io.tmpdir") + "/" + fileName;
            log.info("Updated prediction cohort output file:" + this.outputFile);


            // Create hospitalizations data table
            DataTable hospitalizationsData = new DataTable("hospitalizations cohort data", "TestingVisit");
            hospitalizationsData.initializeToCsv(this.outputFile);


            //-------------------------------------------------------------------------------
            // Create new CFE results that has all the cohorts plus previous information
            //-------------------------------------------------------------------------------
            LinkedHashMap<String, DataTable> validationResultsDataTables = validationResults.getDataTables();

            // //Remove the cohort data table from validation; it will be replaced
            // validationResultsDataTables.remove(CfeResultsSheets.COHORT_DATA);

            XSSFWorkbook resultsWorkbook = DataTable.createWorkbook(validationResultsDataTables);



            DataTable cohortDataForTesting = cohortData;

            cohortDataForTesting.deleteRows("Cohort", "discovery");
            cohortDataForTesting.deleteRows("Cohort", "validation");   // "validation" deprecated; new name "clinical"
            cohortDataForTesting.deleteRows("Cohort", "clinical");


            DataTable testingCohortData = DataTable.join("TestingVisit", "TestingVisit", "Subject Identifiers.PheneVisit",
                    hospitalizationsData, cohortDataForTesting, DataTable.JoinType.RIGHT_OUTER);
            testingCohortData.renameColumn("Time to 1st Hosp", "time");

            // Create TestCohort column that is 0 if the discovery phene value is not set
            // and 1 if it is set
            testingCohortData.addColumn("TestCohort", "1");
            for (int rowIndex = 0; rowIndex < testingCohortData.getNumberOfRows(); rowIndex++) {
                String pheneValue = testingCohortData.getValue(rowIndex, this.discoveryPhene);
                if (pheneValue == null || pheneValue.trim().isEmpty()) {
                    testingCohortData.setValue(rowIndex, "TestCohort", "0");
                }
            }
            testingCohortData.sort("Subject", "VisitNumber");


            hospitalizationsData.addToWorkbook(resultsWorkbook, CfeResultsSheets.PREDICTION_COHORT);
            testingCohortData.addToWorkbook(resultsWorkbook, CfeResultsSheets.TESTING_COHORT_DATA);

            // Create testing cohorts info table
            DataTable testingCohortsInfo = this.createTestingCohortsInfo();

            testingCohortsInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.TESTING_COHORT_INFO);

            // DEBUGGING
            int numberOfSheets = resultsWorkbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                log.info("********** TESTING COHORT RESULTS SHEET " + i + ": " + resultsWorkbook.getSheetName(i)); 
            }

            //-------------------------------------------
            // Create and save CFE results
            //-------------------------------------------
            cfeResults = new CfeResults();
            cfeResults.copyAttributes(validationResults);

            if (validationResults.getResultsType().equals(CfeResultsType.VALIDATION_COHORT_ONLY)) {
                cfeResults.setResultsType(CfeResultsType.TESTING_COHORTS_ONLY);
            }
            else if (validationResults.getResultsType().equals(CfeResultsType.VALIDATION_SCORES)) {
                cfeResults.setResultsType(CfeResultsType.TESTING_COHORTS);
                cfeResults.addCsvAndTextFiles(validationResults);   // Add files from input results
            }

            cfeResults.setResultsSpreadsheet(resultsWorkbook);
            cfeResults.setGeneratedTime(new Date());

            // Add the Python script command
            cfeResults.addTextFile(CfeResultsFileType.TESTING_COHORTS_PYTHON_SCRIPT_COMMAND, this.predictionCohortCreationCommand);

            // Add the validation R script log file
            cfeResults.addTextFile(CfeResultsFileType.TESTING_COHORTS_PYTHON_SCRIPT_LOG, this.scriptOutput);

            CfeResultsService.save(cfeResults);
            this.cfeResultsId = cfeResults.getCfeResultsId();

            //---------------------------------------------------------
            // Create table with info for checking cohorts (optional)
            //---------------------------------------------------------
            ArrayList<String> checkColumns = new ArrayList<String>();
            checkColumns.add("Subject");
            checkColumns.add("VisitNumber");
            checkColumns.add("Subject Identifiers.PheneVisit");
            checkColumns.add("AffyVisit");
            checkColumns.add("Visit Date");
            checkColumns.add(this.discoveryPhene);
            //checkColumns.add(this.clinicalPhene);
            checkColumns.add("Cohort");
            checkColumns.add("Validation");
            checkColumns.add("ValCategory");
            checkColumns.add("ValidationCohort");
            checkColumns.add("TestingCohort");
            DataTable cohortCheck = cohortData.filter(cohortData.getKey(), checkColumns);

            String cohortCheckCsv = cohortCheck.toCsv();
            File cohortCheckCsvFile = FileUtil.createTempFile("cohort-check-",  ".csv");
            if (cohortCheckCsv != null) {
                FileUtils.write(cohortCheckCsvFile, cohortCheckCsv, "UTF-8");
            }
            this.cohortCheckCsvFileName = cohortCheckCsvFile.getAbsolutePath();
        }
        catch (Exception exception) {
            String message = "Testing cohorts creation error: " + exception.getLocalizedMessage();
            this.setErrorMessage(message);
            log.severe(message);
        }  

        return cfeResults;
    }

    public String runCommand(String[] command) throws Exception {
        StringBuilder output = new StringBuilder();

        log.info("run command: " + String.join(" ", command));

        // This allows debugging:
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // redirect standard error to standard output

        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }

        int status = process.waitFor();
        if (status != 0) {
            //throw new Exception("Command \"" + command + "\" exited with code " + status);
        }

        reader.close();
        return output.toString();
    }

    public void processHospitalizations() throws Exception {
        AccessDatabaseParser dbParser = new AccessDatabaseParser(this.followUpDb);

        //---------------------------------------------------------------
        // Get the actuarial table
        //---------------------------------------------------------------
        Table actuarialTable = dbParser.getTable(ACTUARIAL_TABLE_NAME);

        if (actuarialTable == null) {
            String errorMessage = "The acturial table \"" + ACTUARIAL_TABLE_NAME + "\""
                    + " could not be found in the follow-up database \"" + this.followUpDbFileName + "\".";
            throw new IOException(errorMessage);
        }

        DataTable actuarialAndSubjectInfo = new DataTable(ACTUARIAL_TABLE_NAME, "ID");
        actuarialAndSubjectInfo.initializeToAccessTable(actuarialTable);

        //----------------------------------------------------------------
        // Get the hospitalization table
        //----------------------------------------------------------------
        Table hospitalizationsTable = dbParser.getTable(HOSPITALIZATIONS_TABLE_NAME);

        if (hospitalizationsTable == null) {
            String errorMessage = "The hospitalizations table \"" + HOSPITALIZATIONS_TABLE_NAME + "\""
                    + " could not be found in the follow-up database \"" + this.followUpDbFileName + "\".";
            throw new IOException(errorMessage);
        }

        DataTable hospitalizations = new DataTable(HOSPITALIZATIONS_TABLE_NAME, "ID");
        hospitalizations.initializeToAccessTable(hospitalizationsTable);

        // Join tables
        DataTable scoringData = null;
        String keyColumn = null;
        String joinColumn = "SubjectID";
        scoringData = DataTable.join(keyColumn, joinColumn, actuarialAndSubjectInfo, hospitalizations);

        String scoringDataCsv = scoringData.toCsv();
        File scoringDataCsvFile = FileUtil.createTempFile("testing-scoring-data-",  ".csv");
        if (scoringDataCsv != null) {
            FileUtils.write(scoringDataCsvFile, scoringDataCsv, "UTF-8");
        }
        this.scoringDataFileName = scoringDataCsvFile.getAbsolutePath();
    }

    public DataTable createTestingCohortsInfo() throws Exception {
        DataTable testingCohortsInfo = new DataTable();
        testingCohortsInfo.addColumn("attribute", "");
        testingCohortsInfo.addColumn("value", "");

        ArrayList<String> row;

        row = new ArrayList<String>();
        row.add("CFE Version");
        row.add(VersionNumber.VERSION_NUMBER);
        testingCohortsInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Time Cohort Generated");
        row.add(new Date().toString());
        testingCohortsInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("% in validation cohort specified");
        row.add(this.percentInValidationCohort);
        testingCohortsInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of validation cohort subjects");
        row.add(this.numberOfValidationSubjects + "");
        testingCohortsInfo.addRow(row);            

        row = new ArrayList<String>();
        row.add("Number of testing cohort subjects");
        row.add(this.numberOfTestingSubjects + "");
        testingCohortsInfo.addRow(row); 

        row = new ArrayList<String>();
        row.add("Discovery Phene");
        row.add(this.discoveryPhene);
        testingCohortsInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Discovery Low Cutoff");
        row.add(this.discoveryLowCutoff + "");
        testingCohortsInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Discovery High Cutoff");
        row.add(this.discoveryHighCutoff + "");
        testingCohortsInfo.addRow(row);
        

        row = new ArrayList<String>();
        row.add("Admission Phene");
        row.add(this.admissionPhene + "");
        testingCohortsInfo.addRow(row);
        
        return testingCohortsInfo;
    }
    
    
    
    
    /**
     * Creates the phene visit CSV file that is used for input into the Python script
     * for calculating the hospitalization cohort.
     * 
     * @throws Exception
     */
    public void createPheneVistsCsvFile() throws Exception {
        XSSFWorkbook workbook = this.validationResults.getResultsSpreadsheet();
        
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
        
        String originalKeyColumn = "Subject Identifiers.PheneVisit";
        String originalVisitDateColumn = "Visit Date";
        
        List<String> filterColumns = new ArrayList<String>();
        filterColumns.add(originalKeyColumn);
        filterColumns.add(originalVisitDateColumn);
        
        DataTable pheneVisits = new DataTable(originalKeyColumn);
        pheneVisits.initializeToWorkbookSheet(sheet);
        pheneVisits.sort("Subject", "VisitNumber");
        
        pheneVisits = pheneVisits.filter(originalKeyColumn, filterColumns);
        pheneVisits.renameColumn(originalKeyColumn, "TestingVisit");
        pheneVisits.renameColumn(originalVisitDateColumn, "PheneVisit Date");
        
        // Create the temporary CSV file
        String pheneVisitsCsv = pheneVisits.toCsv();
        File pheneVisitsCsvFile = FileUtil.createTempFile("testing-phene-visits-",  ".csv");
        if (pheneVisitsCsv != null) {
            FileUtils.write(pheneVisitsCsvFile, pheneVisitsCsv, "UTF-8");
        }
        
        this.pheneVisitsFileName = pheneVisitsCsvFile.getAbsolutePath();
    }

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    public List<CfeResults> getValidationResultsList() {
        return validationResultsList;
    }

    public void setValidationResultsList(List<CfeResults> validationResultsList) {
        this.validationResultsList = validationResultsList;
    }

    public CfeResults getValidationResults() {
        return validationResults;
    }

    public void setValidationResults(CfeResults validationResults) {
        this.validationResults = validationResults;
    }

    public Long getValidationId() {
        return validationId;
    }

    public void setValidationId(Long validationId) {
        this.validationId = validationId;
    }

    public ArrayList<String> getPhenes() {
        return phenes;
    }

    public void setPhenes(ArrayList<String> phenes) {
        this.phenes = phenes;
    }

    public String getDiscoveryPhene() {
        return discoveryPhene;
    }

    public void setDiscoveryPhene(String discoveryPhene) {
        this.discoveryPhene = discoveryPhene;
    }

    public String getDiscoveryPheneTable() {
        return discoveryPheneTable;
    }

    public void setDiscoveryPheneTable(String discoveryPheneTable) {
        this.discoveryPheneTable = discoveryPheneTable;
    }

    public Double getDiscoveryLowCutoff() {
        return discoveryLowCutoff;
    }

    public void setDiscoveryLowCutoff(Double discoveryLowCutoff) {
        this.discoveryLowCutoff = discoveryLowCutoff;
    }

    public Double getDiscoveryHighCutoff() {
        return discoveryHighCutoff;
    }

    public void setDiscoveryHighCutoff(Double discoveryHighCutoff) {
        this.discoveryHighCutoff = discoveryHighCutoff;
    }

    
    /*
    public String getClinicalPhene() {
        return clinicalPhene;
    }

    public void setClinicalPhene(String clinicalPhene) {
        this.clinicalPhene = clinicalPhene;
    }

    public Integer getClinicalHighCutoff() {
        return clinicalHighCutoff;
    }

    public void setClinicalHighCutoff(Integer clinicalHighCutoff) {
        this.clinicalHighCutoff = clinicalHighCutoff;
    }
    */
    
    public String getValidationConstraint1() {
        return validationConstraint1;
    }

    public void setValidationConstraint1(String validationConstraint1) {
        this.validationConstraint1 = validationConstraint1;
    }

    public String getValidationConstraint2() {
        return validationConstraint2;
    }

    public void setValidationConstraint2(String validationConstraint2) {
        this.validationConstraint2 = validationConstraint2;
    }

    public String getValidationConstraint3() {
        return validationConstraint3;
    }

    public void setValidationConstraint3(String validationConstraint3) {
        this.validationConstraint3 = validationConstraint3;
    }

    public String getPercentInValidationCohort() {
        return percentInValidationCohort;
    }

    public void setPercentInValidationCohort(String percentInValidationCohort) {
        this.percentInValidationCohort = percentInValidationCohort;
    }

    public TreeSet<String> getCohortSubjects() {
        return cohortSubjects;
    }

    public void setCohortSubjects(TreeSet<String> cohortSubjects) {
        this.cohortSubjects = cohortSubjects;
    }

    public TreeSet<String> getValidationSubjects() {
        return validationSubjects;
    }

    public void setValidationSubjects(TreeSet<String> validationSubjects) {
        this.validationSubjects = validationSubjects;
    }

    public TreeSet<String> getTestingSubjects() {
        return testingSubjects;
    }

    public void setTestingSubjects(TreeSet<String> testingSubjects) {
        this.testingSubjects = testingSubjects;
    }

    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    public int getNumberOfValidationSubjects() {
        return numberOfValidationSubjects;
    }

    public void setNumberOfValidationSubjects(int numberOfValidationSubjects) {
        this.numberOfValidationSubjects = numberOfValidationSubjects;
    }

    public int getNumberOfTestingSubjects() {
        return numberOfTestingSubjects;
    }

    public void setNumberOfTestingSubjects(int numberOfTestingSubjects) {
        this.numberOfTestingSubjects = numberOfTestingSubjects;
    }

    public String getCohortCheckCsvFileName() {
        return cohortCheckCsvFileName;
    }

    public void setCohortCheckCsvFileName(String cohortCheckCsvFileName) {
        this.cohortCheckCsvFileName = cohortCheckCsvFileName;
    }

    public File getFollowUpDb() {
        return followUpDb;
    }

    public void setFollowUpDb(File followUpDb) {
        this.followUpDb = followUpDb;
    }

    public String getFollowUpDbFileName() {
        return followUpDbFileName;
    }

    public void setFollowUpDbFileName(String followUpDbFileName) {
        this.followUpDbFileName = followUpDbFileName;
    }

    public String getScoringDataFileName() {
        return scoringDataFileName;
    }

    public void setScoringDataFileName(String scoringDataFileName) {
        this.scoringDataFileName = scoringDataFileName;
    }

    public String getPheneVisitsFileName() {
        return pheneVisitsFileName;
    }

    public void setPheneVisitsFileName(String pheneVisitsFileName) {
        this.pheneVisitsFileName = pheneVisitsFileName;
    }

    public List<String> getAdmissionReasons() {
        return admissionReasons;
    }

    public void setAdmissionReasons(List<String> admissionReasons) {
        this.admissionReasons = admissionReasons;
    }

    public String getAdmissionPhene() {
        return admissionPhene;
    }

    public void setAdmissionPhene(String admissionPhene) {
        this.admissionPhene = admissionPhene;
    }

    public String getPredictionCohortCreationCommand() {
        return predictionCohortCreationCommand;
    }

    public void setPredictionCohortCreationCommand(String predictionCohortCreationCommand) {
        this.predictionCohortCreationCommand = predictionCohortCreationCommand;
    }

    public String getScriptOutput() {
        return scriptOutput;
    }

    public void setScriptOutput(String scriptOutput) {
        this.scriptOutput = scriptOutput;
    }

    public String getScriptOutputFile() {
        return scriptOutputFile;
    }

    public void setScriptOutputFile(String scriptOutputFile) {
        this.scriptOutputFile = scriptOutputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public Map<String, ArrayList<ColumnInfo>> getPheneMap() {
        return pheneMap;
    }

    public void setPheneMap(Map<String, ArrayList<ColumnInfo>> pheneMap) {
        this.pheneMap = pheneMap;
    }

}
