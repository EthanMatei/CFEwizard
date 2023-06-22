package cfe.calc;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.services.CfeResultsService;
import cfe.utils.CohortDataTable;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.PheneCondition;

public class ValidationCohortCalc {

    private static final Logger log = Logger.getLogger(ValidationCohortCalc.class.getName());
    
	public static final Long RANDOM_SEED = 10972359723095792L;
	
	private List<CfeResults> discoveryResultsList;
	private CfeResults inputResults;
	
	private ArrayList<String> phenes;
	
	private Map<String,ArrayList<ColumnInfo>> pheneMap;
	
	private double comparisonThreshold = 0.0001;


	private String discoveryPhene;
	private String discoveryPheneTable;
	private Double discoveryLowCutoff;
	private Double discoveryHighCutoff;

    private double percentInValidationDecimal;
    
	TreeSet<String> cohortSubjects;
	TreeSet<String> validationSubjects;
	TreeSet<String> testingSubjects;

	int numberOfValidationSubjects = 0;
	int numberOfTestingSubjects    = 0;

	private Long cfeResultsId;
	private String cohortCheckCsvFileName;

	private String scoringDataFileName;
	private String pheneVisitsFileName;

	public ValidationCohortCalc() {
	    this.cohortSubjects     = new TreeSet<String>();
	    this.validationSubjects = new TreeSet<String>();
	    this.testingSubjects    = new TreeSet<String>();
	}

	public CfeResults calculate(
	        CfeResults inputResultsParam,
	        List<PheneCondition> pheneConditions,
	        double percentInValidationDecimalParam,      // [0, 1]
	        double comparisonThresholdParam
	) throws Exception {

	    if (inputResultsParam == null) {
	        throw new Exception("The previous step input results for validation cohort calculation are missing.");    
	    }
	    this.inputResults = inputResultsParam;

	    if (percentInValidationDecimalParam < 0.0 || percentInValidationDecimalParam > 1.0) {
	        throw new Exception("The specified in validation cohort percent is not in reange [0,1].");
	    }
        this.percentInValidationDecimal = percentInValidationDecimalParam;
        
        this.comparisonThreshold = comparisonThresholdParam;
        
	    ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

	    XSSFWorkbook discoveryWorkbook = this.inputResults.getResultsSpreadsheet();
	    if (discoveryWorkbook == null) {
	        throw new Exception("The previous step input results for validation cohort creation do not have a results spreadsheet.");
	    }
	    LinkedHashMap<String,DataTable> dataTables = this.inputResults.getDataTables();

	    XSSFSheet sheet = discoveryWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
	    if (sheet == null) {
	        String message = "The input results spreadsheet for validation cohort calculation does not contain a \""
	                + CfeResultsSheets.COHORT_DATA + "\" sheet.";
	        log.severe(message);
	        throw new Exception(message);
	    }
	    
	    CohortDataTable cohortData = new CohortDataTable();
	    cohortData.setKey(null);
	    cohortData.initializeToWorkbookSheet(sheet);
	    cohortData.setKey("Subject Identifiers.PheneVisit");

	    this.discoveryPhene      = inputResults.getPhene();
	    this.discoveryLowCutoff  = inputResults.getLowCutoff();
	    this.discoveryHighCutoff = inputResults.getHighCutoff();

	    List<TreeSet<String>> results = cohortData.setValidationAndTestingCohorts(
	            discoveryPhene, discoveryLowCutoff, discoveryHighCutoff,
	            this.comparisonThreshold,
	            // clinicalPhene, clinicalHighCutoff,
	            pheneConditions, percentInValidationDecimal
	    );

	    this.validationSubjects = results.get(0);
	    this.testingSubjects    = results.get(1);

	    this.numberOfValidationSubjects = this.validationSubjects.size();
	    this.numberOfTestingSubjects    = this.testingSubjects.size();

	    List<String> subjects = new ArrayList<String>();
	    subjects.addAll(cohortSubjects);

	    //--------------------------------------------------
	    // Create phene visits CSV file
	    //--------------------------------------------------
	    // this.createPheneVistsCsvFile();

	    //-------------------------------------------------------------------------------
	    // Create new CFE results that has all the cohorts plus previous information
	    //-------------------------------------------------------------------------------
	    dataTables.remove(CfeResultsSheets.COHORT_DATA);  // Remove cohort data, which will be replaced
	    XSSFWorkbook resultsWorkbook = DataTable.createWorkbook(dataTables);


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
	    for (PheneCondition condition: pheneConditions) {
	        String conditionPhene = condition.getPhene();

	        // Avoid adding duplicate phenes, e.g. one of the condition phenes
	        // is the same as the discovery phene.
	        if (!columns.contains(conditionPhene)) {
	            columns.add(conditionPhene);
	        }
	    }

	    columns.add("Validation");
	    columns.add("ValCategory");
	    columns.add("ValidationCohort");
	    columns.add("TestingCohort");


	    // Filter to get only needed columns
	    DataTable validationCohort = cohortData.filter("Subject Identifiers.PheneVisit", columns);

	    String[] validationSortColumns = {"Subject", "VisitNumber"};
	    validationCohort.sort(validationSortColumns);  

	    //validationCohort.addColumn("Subject",  "");
	    //for (String subject: validationSubjects) {
	    //    ArrayList<String> row = new ArrayList<String>();
	    //    row.add(subject);
	    //    validationCohort.addRow(row);
	    //}
	    validationCohort.addToWorkbook(resultsWorkbook, CfeResultsSheets.VALIDATION_COHORT);

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
	    DataTable validationCohortInfo = new DataTable();
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
	    row.add("% in validation cohort specified");
	    row.add((this.percentInValidationDecimal * 100.0) + "");
	    validationCohortInfo.addRow(row);

	    row = new ArrayList<String>();
	    row.add("Number of validation cohort subjects");
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

        for (int i = 1; i <= 3; i++) {
            row = new ArrayList<String>();
            row.add("Constraint " + i);
            
            if (pheneConditions.size() >= i) {
                PheneCondition pheneCondition = pheneConditions.get(i-1);
                row.add(pheneCondition.getPhene() + " " + pheneCondition.getOperator() + " " + pheneCondition.getValue());
            }
            else {
                row.add("");
            }
            validationCohortInfo.addRow(row);
        }


	    validationCohortInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.VALIDATION_COHORT_INFO);

	    cohortData.addToWorkbook(resultsWorkbook, CfeResultsSheets.COHORT_DATA);

	    //-------------------------------------------
	    // Create and save CFE results
	    //-------------------------------------------
	    CfeResults cfeResults = new CfeResults();
	    cfeResults.copyAttributes(inputResults);

	    if (inputResults.getResultsType().equals(CfeResultsType.DISCOVERY_COHORT)) {
	        cfeResults.setResultsType(CfeResultsType.VALIDATION_COHORT_ONLY);
	    }
	    else if (inputResults.getResultsType().equals(CfeResultsType.PRIORITIZATION_SCORES)) {
	        cfeResults.setResultsType(CfeResultsType.VALIDATION_COHORT);
	        cfeResults.addCsvAndTextFiles(inputResults);
	    }

	    cfeResults.setResultsSpreadsheet(resultsWorkbook);
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

	    return cfeResults;
    }
    	

  

    public List<CfeResults> getDiscoveryResultsList() {
        return discoveryResultsList;
    }

    public void setDiscoveryResultsList(List<CfeResults> discoveryResultsList) {
        this.discoveryResultsList = discoveryResultsList;
    }

    public CfeResults getInputResults() {
        return inputResults;
    }

    public void setInputResults(CfeResults inputResults) {
        this.inputResults = inputResults;
    }

    public ArrayList<String> getPhenes() {
        return phenes;
    }

    public void setPhenes(ArrayList<String> phenes) {
        this.phenes = phenes;
    }

    
    public double getComparisonThreshold() {
        return comparisonThreshold;
    }

    
    public void setComparisonThreshold(double comparisonThreshold) {
        this.comparisonThreshold = comparisonThreshold;
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
    
    public double getPercentInValidationDecimal() {
        return this.percentInValidationDecimal;
    }

    public void setPercentInValidationCohort(double percentInValidationCohort) {
        this.percentInValidationDecimal = percentInValidationCohort;
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

    public Map<String, ArrayList<ColumnInfo>> getPheneMap() {
        return pheneMap;
    }

    public void setPheneMap(Map<String, ArrayList<ColumnInfo>> pheneMap) {
        this.pheneMap = pheneMap;
    }
}
