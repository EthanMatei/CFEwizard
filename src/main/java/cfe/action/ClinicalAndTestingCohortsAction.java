package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Table;

import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.parser.AccessDatabaseParser;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.DataTable;
import cfe.utils.PheneCondition;
import cfe.utils.WebAppProperties;

public class ClinicalAndTestingCohortsAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ClinicalAndTestingCohortsAction.class);

	private Map<String, Object> webSession;
    
	public static final Long RANDOM_SEED = 10972359723095792L;
	
	public static final String ACTUARIAL_TABLE_NAME = "Actuarial and Subject Info";
	public static final String HOSPITALIZATIONS_TABLE_NAME = "Hospitalizations Follow-up Database";
	
	private List<CfeResults> discoveryResultsList;
	private CfeResults discoveryResults;
	
	private ArrayList<String> phenes;
	
	private String[] operators = {">=", ">", "<=", "<"};
	
	private Long discoveryId;
	
	private String admissionPhene;
    
	private String errorMessage;
	
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
	
	private String clinicalPhene;
	private Integer clinicalHighCutoff;
	
	private String discoveryPhene;
	private String discoveryPheneTable;
	private Integer discoveryLowCutoff;
	private Integer discoveryHighCutoff;
	
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
    private String followUpDbContentType;
    private String followUpDbFileName;
    private String scoringDataFileName;
    private String pheneVisitsFileName;
    
    private List<String> admissionReasons;
    private String predictionCohortCreationCommand;
    private String scriptOutput;
    private String outputFile;
    private String scriptOutputFile;
    
	public ClinicalAndTestingCohortsAction() {
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
	
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		} else {
            this.discoveryResultsList = CfeResultsService.getMetadata(CfeResultsType.DISCOVERY_COHORT, CfeResultsType.DISCOVERY_SCORES);
		}
	    return result;
	}
	
    public String specification() throws Exception {
        String result = SUCCESS;
        
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        } else {
            ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
            this.discoveryResults = CfeResultsService.get(discoveryId);
            
            this.discoveryPhene      = discoveryResults.getPhene();
            this.discoveryLowCutoff  = discoveryResults.getLowCutoff();
            this.discoveryHighCutoff = discoveryResults.getHighCutoff();
            
            XSSFWorkbook workbook = discoveryResults.getResultsSpreadsheet();
            
            // Get the discovery database phene table name
            XSSFSheet discoveryCohortInfoSheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO);
            DataTable cohortInfo = new DataTable("attribute");
            cohortInfo.initializeToWorkbookSheet(discoveryCohortInfoSheet);
            ArrayList<String> row = cohortInfo.getRow("Phene Table");
            if (row == null) {
                throw new Exception("Unable to find Phene Table row in sheet \""
                        + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".");
            }
            this.discoveryPheneTable = row.get(1);
            if (this.discoveryPheneTable == null || this.discoveryPheneTable.isEmpty()) {
                throw new Exception("Could not get phene table information from workbook sheet \""
                        + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".");
            }       
            
            XSSFSheet cohortDataSheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
            CohortDataTable cohortData = new CohortDataTable(this.discoveryPheneTable);
            cohortData.initializeToWorkbookSheet(cohortDataSheet);
            cohortData.setKey("Subject Identifiers.PheneVisit");
            
            phenes = new ArrayList<String>();
            phenes.add("");
            phenes.addAll(cohortData.getPhenes());
        }
        return result;
    }
    
    public String process() throws Exception {
        String result = SUCCESS;
        
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        } else {
            this.discoveryResults = CfeResultsService.get(discoveryId);
            
            log.info("Testing follow-up database file name: " + this.followUpDbFileName);
            
            ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
            this.discoveryResults = CfeResultsService.get(discoveryId);

            XSSFWorkbook discoveryWorkbook = discoveryResults.getResultsSpreadsheet();
            XSSFSheet sheet = discoveryWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
            CohortDataTable cohortData = new CohortDataTable();
            cohortData.initializeToWorkbookSheet(sheet);
            cohortData.setKey("Subject Identifiers.PheneVisit");

            //double value;
            //PheneCondition pheneCondition;
            List<PheneCondition> pheneConditions = new ArrayList<PheneCondition>();

            /*
            if (phene1 != null && !phene1.isEmpty() && value1 != null && !value1.isEmpty()) {
                value = Double.parseDouble(value1);
                pheneCondition = new PheneCondition(phene1, operator1, value);
                pheneConditions.add(pheneCondition);
            }

            if (phene2 != null && !phene2.isEmpty() && value2 != null && !value2.isEmpty()) {
                value = Double.parseDouble(value2);
                pheneCondition = new PheneCondition(phene2, operator2, value);
                pheneConditions.add(pheneCondition);
            }

            if (phene3 != null && !phene3.isEmpty() && value3 != null && !value3.isEmpty()) {
                value = Double.parseDouble(value3);
                pheneCondition = new PheneCondition(phene3, operator3, value);
                pheneConditions.add(pheneCondition);
            }
            */
            
            double percentInValidation = Double.parseDouble(this.percentInValidationCohort) / 100.0;
            
            List<TreeSet<String>> results = cohortData.setValidationAndTestingCohorts(
                    discoveryPhene, discoveryLowCutoff, discoveryHighCutoff, 
                    clinicalPhene, clinicalHighCutoff,
                    pheneConditions, percentInValidation
            );
            
            this.validationSubjects = results.get(0);
            this.testingSubjects    = results.get(1);
            
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
            String tempDir = System.getProperty("java.io.tmpdir");
            
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

            File tempFile = File.createTempFile("prediction-cohort-creation-python-script-output-", ".txt");
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
            
            //-------------------------------------------------------------------------------
            // Create new CFE results that has all the cohorts plus previous information
            //-------------------------------------------------------------------------------
            XSSFWorkbook resultsWorkbook = new XSSFWorkbook();
            
            if (discoveryResults.getResultsType().equals(CfeResultsType.DISCOVERY_SCORES)) {
                // Discovery scores table
                DataTable discoveryScores = new DataTable(null);
                discoveryScores.initializeToWorkbookSheet(discoveryWorkbook.getSheet(CfeResultsSheets.DISCOVERY_SCORES));
                discoveryScores.addToWorkbook(resultsWorkbook, CfeResultsSheets.DISCOVERY_SCORES);
            
                // Discovery scores info table
                DataTable discoveryScoresInfo = new DataTable(null);
                discoveryScoresInfo.initializeToWorkbookSheet(discoveryWorkbook.getSheet(CfeResultsSheets.DISCOVERY_SCORES_INFO));
                discoveryScoresInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.DISCOVERY_SCORES_INFO);                
            }
            
            // Discovery cohort table
            DataTable discoveryCohort = new DataTable(null);
            discoveryCohort.initializeToWorkbookSheet(discoveryWorkbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT));
            discoveryCohort.addToWorkbook(resultsWorkbook, CfeResultsSheets.DISCOVERY_COHORT);

            // Discovery cohort info table
            DataTable discoveryCohortInfo = new DataTable(null);
            discoveryCohortInfo.initializeToWorkbookSheet(discoveryWorkbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO));
            discoveryCohortInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.DISCOVERY_COHORT_INFO);           

            // Modify (all) cohort data table
            cohortData.addCohort("validation", validationSubjects);
            cohortData.addCohort("testing", testingSubjects);
            String[] sortColumns = {"Cohort", "Subject", "Subject Identifiers.PheneVisit"};
            cohortData.sortWithBlanksLast(sortColumns);            
            
            // Create validation cohort data table
            ArrayList<String> columns = new ArrayList<String>();
            columns.add("Subject");
            columns.add("VisitNumber");
            columns.add("Subject Identifiers.PheneVisit");
            columns.add("AffyVisit");
            columns.add("Visit Date");
            columns.add("Gender(M/F)");
            columns.add("Age at testing (Years)");
            columns.add("Race/Ethnicity");
            columns.add("DxCode");
            columns.add(this.discoveryPhene);
            columns.add("Validation");
            columns.add("ValCategory");
            columns.add("ValidationCohort");
            columns.add("TestingCohort");

                        
            DataTable validationCohort = cohortData.filter("Subject Identifiers.PheneVisit", columns);

            String[] validationSortColumns = {"Subject", "VisitNumber"};
            validationCohort.sort(validationSortColumns);  
            
            //validationCohort.addColumn("Subject",  "");
            //for (String subject: validationSubjects) {
            //    ArrayList<String> row = new ArrayList<String>();
            //    row.add(subject);
            //    validationCohort.addRow(row);
            //}
            validationCohort.addToWorkbook(resultsWorkbook, CfeResultsSheets.CLINICAL_COHORT);

            // Create testing cohort data table
            DataTable testingCohort = new DataTable("Subject");
            testingCohort.addColumn("Subject",  "");
            for (String subject: testingSubjects) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(subject);
                testingCohort.addRow(row);
            }
            
            testingCohort.addToWorkbook(resultsWorkbook, CfeResultsSheets.TESTING_COHORT);

            // Create validation cohort info table
            DataTable validationCohortInfo = new DataTable("attribute");
            validationCohortInfo.addColumn("attribute", "");
            validationCohortInfo.addColumn("value", "");
            
            ArrayList<String> row;
            
            row = new ArrayList<String>();
            row.add("CFE Version");
            row.add(VersionNumber.VERSION_NUMBER);
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("Time Cohort Generated");
            row.add(new Date().toString());
            validationCohortInfo.addRow(row);

            
            row = new ArrayList<String>();
            row.add("% in clinical cohort specified");
            row.add(this.percentInValidationCohort);
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("Number of clinical cohort subjects");
            row.add(this.numberOfValidationSubjects + "");
            validationCohortInfo.addRow(row);            

            row = new ArrayList<String>();
            row.add("Number of testing cohort subjects");
            row.add(this.numberOfTestingSubjects + "");
            validationCohortInfo.addRow(row); 
            
            row = new ArrayList<String>();
            row.add("Discovery Phene");
            row.add(this.discoveryPhene);
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("Discovery Low Cutoff");
            row.add(this.discoveryLowCutoff + "");
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("Discovery High Cutoff");
            row.add(this.discoveryHighCutoff + "");
            validationCohortInfo.addRow(row);
            
            
            row = new ArrayList<String>();
            row.add("Clincal Phene");
            row.add(this.clinicalPhene);
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("Clinical High Cutoff");
            row.add(this.clinicalHighCutoff + "");
            validationCohortInfo.addRow(row);
            
            /*
            row = new ArrayList<String>();
            row.add("Constraint 1");
            if (!this.phene1.isEmpty() && !this.value1.isEmpty()) {
                row.add(this.phene1 + " " + this.operator1 + " " + this.value1);
            }
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("Constraint 2");
            if (!this.phene2.isEmpty() && !this.value2.isEmpty()) {
                row.add(this.phene2 + " " + this.operator2 + " " + this.value2);
            }
            validationCohortInfo.addRow(row);            

            row = new ArrayList<String>();
            row.add("Constraint 3");
            if (!this.phene3.isEmpty() && !this.value3.isEmpty()) {
                row.add(this.phene3 + " " + this.operator3 + " " + this.value3);
            }
            validationCohortInfo.addRow(row);
            */

            validationCohortInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.CLINICAL_COHORT_INFO);
            
            cohortData.addToWorkbook(resultsWorkbook, CfeResultsSheets.COHORT_DATA);

            
            //-------------------------------------------
            // Create and save CFE results
            //-------------------------------------------
            CfeResults cfeResults = new CfeResults();

            if (discoveryResults.getResultsType().equals(CfeResultsType.DISCOVERY_COHORT)) {
                cfeResults.setResultsType(CfeResultsType.ALL_COHORTS);
            }
            else if (discoveryResults.getResultsType().equals(CfeResultsType.DISCOVERY_SCORES)) {
                cfeResults.setResultsType(CfeResultsType.ALL_COHORTS_PLUS_DISCOVERY_SCORES);
            }

            cfeResults.setResultsSpreadsheet(resultsWorkbook);
            cfeResults.setPhene(discoveryPhene);
            cfeResults.setLowCutoff(discoveryLowCutoff);
            cfeResults.setHighCutoff(discoveryHighCutoff);
            cfeResults.setGeneratedTime(new Date());

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
            checkColumns.add(this.clinicalPhene);
            checkColumns.add("Cohort");
            checkColumns.add("Validation");
            checkColumns.add("ValCategory");
            checkColumns.add("ValidationCohort");
            checkColumns.add("TestingCohort");
            DataTable cohortCheck = cohortData.filter(cohortData.getKey(), checkColumns);
            
            String cohortCheckCsv = cohortCheck.toCsv();
            File cohortCheckCsvFile = File.createTempFile("cohort-check-",  ".csv");
            if (cohortCheckCsv != null) {
                FileUtils.write(cohortCheckCsvFile, cohortCheckCsv, "UTF-8");
            }
            this.cohortCheckCsvFileName = cohortCheckCsvFile.getAbsolutePath();
        }
        
        return result;
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
        File scoringDataCsvFile = File.createTempFile("testing-scoring-data-",  ".csv");
        if (scoringDataCsv != null) {
            FileUtils.write(scoringDataCsvFile, scoringDataCsv, "UTF-8");
        }
        this.scoringDataFileName = scoringDataCsvFile.getAbsolutePath();
    }
    
    /**
     * Creates the phene visit CSV file that is used for input into the Python script
     * for calculating the hospitalization cohort.
     * 
     * @throws Exception
     */
    public void createPheneVistsCsvFile() throws Exception {
        XSSFWorkbook workbook = this.discoveryResults.getResultsSpreadsheet();
        
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
        File pheneVisitsCsvFile = File.createTempFile("testing-phene-visits-",  ".csv");
        if (pheneVisitsCsv != null) {
            FileUtils.write(pheneVisitsCsvFile, pheneVisitsCsv, "UTF-8");
        }
        
        this.pheneVisitsFileName = pheneVisitsCsvFile.getAbsolutePath();
    }
    
	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public void validate() {
	}

	public Map<String, Object> getSession() {
		return webSession;
	}


	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    public List<CfeResults> getDiscoveryResultsList() {
        return discoveryResultsList;
    }

    public void setDiscoveryResultsList(List<CfeResults> discoveryResultsList) {
        this.discoveryResultsList = discoveryResultsList;
    }

    public CfeResults getDiscoveryResults() {
        return discoveryResults;
    }

    public void setDiscoveryResults(CfeResults discoveryResults) {
        this.discoveryResults = discoveryResults;
    }

    public Long getDiscoveryId() {
        return discoveryId;
    }

    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }

    public ArrayList<String> getPhenes() {
        return phenes;
    }

    public void setPhenes(ArrayList<String> phenes) {
        this.phenes = phenes;
    }

    public String[] getOperators() {
        return operators;
    }

    public void setOperators(String[] operators) {
        this.operators = operators;
    }

    /*
    public String getPhene1() {
        return phene1;
    }

    public void setPhene1(String phene1) {
        this.phene1 = phene1;
    }

    public String getPhene2() {
        return phene2;
    }

    public void setPhene2(String phene2) {
        this.phene2 = phene2;
    }

    public String getPhene3() {
        return phene3;
    }

    public void setPhene3(String phene3) {
        this.phene3 = phene3;
    }

    public String getOperator1() {
        return operator1;
    }

    public void setOperator1(String operator1) {
        this.operator1 = operator1;
    }

    public String getOperator2() {
        return operator2;
    }

    public void setOperator2(String operator2) {
        this.operator2 = operator2;
    }

    public String getOperator3() {
        return operator3;
    }

    public void setOperator3(String operator3) {
        this.operator3 = operator3;
    }
    
    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public String getValue3() {
        return value3;
    }

    public void setValue3(String value3) {
        this.value3 = value3;
    }
    */
    
    

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

    public Integer getDiscoveryLowCutoff() {
        return discoveryLowCutoff;
    }

    public void setDiscoveryLowCutoff(Integer discoveryLowCutoff) {
        this.discoveryLowCutoff = discoveryLowCutoff;
    }

    public Integer getDiscoveryHighCutoff() {
        return discoveryHighCutoff;
    }

    public void setDiscoveryHighCutoff(Integer discoveryHighCutoff) {
        this.discoveryHighCutoff = discoveryHighCutoff;
    }
    
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

    public String getFollowUpDbContentType() {
        return followUpDbContentType;
    }

    public void setFollowUpDbContentType(String followUpDbContentType) {
        this.followUpDbContentType = followUpDbContentType;
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

}
