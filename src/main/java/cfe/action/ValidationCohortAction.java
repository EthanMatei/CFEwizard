package cfe.action;

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

public class ValidationCohortAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(ValidationCohortAction.class.getName());
    
	private Map<String, Object> webSession;
    
	public static final Long RANDOM_SEED = 10972359723095792L;
	
	private List<CfeResults> discoveryResultsList;
	private CfeResults discoveryResults;
	
	private ArrayList<String> phenes;
	
	private Map<String,ArrayList<ColumnInfo>> pheneMap;
	
	private String[] operators = {">=", ">", "<=", "<"};
	
	private Long discoveryId;
    
	private String errorMessage;
	
	private String phene1;
	private String phene2;
	private String phene3;
	
	private String operator1;
	private String operator2;
	private String operator3;
	
	private String value1;
	private String value2;
	private String value3;
	
	private double validationCohortComparisonThreshold = 0.0001;
	
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
	
    private String scoringDataFileName;
    private String pheneVisitsFileName;
    
	public ValidationCohortAction() {
	    this.cohortSubjects     = new TreeSet<String>();
	    this.validationSubjects = new TreeSet<String>();
	    this.testingSubjects    = new TreeSet<String>();
	}
	
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		} else {
            this.discoveryResultsList = CfeResultsService.getMetadata(
                    CfeResultsType.PRIORITIZATION_SCORES
            );
            Collections.sort(this.discoveryResultsList, new CfeResultsNewestFirstComparator());
		}
	    return result;
	}
	
    public String specification() throws Exception {
        String result = SUCCESS;
        
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else {
            try {
                if (discoveryId == null) {
                    result = INPUT;
                    throw new Exception("No data ID specified for validation cohort specification.");
                }
                
                ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
                this.discoveryResults = CfeResultsService.get(discoveryId);
            
                this.discoveryPhene      = discoveryResults.getPhene();
                this.discoveryLowCutoff  = discoveryResults.getLowCutoff();
                this.discoveryHighCutoff = discoveryResults.getHighCutoff();
            
                XSSFWorkbook workbook = discoveryResults.getResultsSpreadsheet();
            
                if (workbook == null) {
                    result = INPUT;
                    throw new Exception("Could not get results workbook for ID \"" + discoveryId + "\"'");
                }
                
                // Get the discovery database phene table name
                XSSFSheet discoveryCohortInfoSheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO);
                if (discoveryCohortInfoSheet == null) {
                    result = INPUT;
                    throw new Exception("The data spreadsheet is missing sheet \"" + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".");
                }
                
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
                cohortData.setKey(null);
                cohortData.initializeToWorkbookSheet(cohortDataSheet);
                cohortData.setKey("Subject Identifiers.PheneVisit");

                phenes = new ArrayList<String>();
                phenes.add("");
                phenes.addAll(cohortData.getPheneList());

                pheneMap = cohortData.getPheneMap();
            }
            catch (Exception exception) {
                if (result == SUCCESS) {
                    result = ERROR;
                }
                String message = "Validation cohort creation error: " + exception.getLocalizedMessage();
                this.setErrorMessage(message);
                log.severe(message);
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                this.setExceptionStack(stackTrace);
            }  
        }
        return result;
    }
    
    public String process() throws Exception {
        String result = SUCCESS;
        
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        } else {
            try {
                ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
                this.discoveryResults = CfeResultsService.get(discoveryId);

                XSSFWorkbook discoveryWorkbook = discoveryResults.getResultsSpreadsheet();
                LinkedHashMap<String,DataTable> dataTables = discoveryResults.getDataTables();
                
                XSSFSheet sheet = discoveryWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
                CohortDataTable cohortData = new CohortDataTable();
                cohortData.setKey(null);
                cohortData.initializeToWorkbookSheet(sheet);
                cohortData.setKey("Subject Identifiers.PheneVisit");
                
                List<PheneCondition> pheneConditions = PheneCondition.createList(
                        phene1, operator1, value1,
                        phene2, operator2, value2,
                        phene3, operator3, value3
                );

                double percentInValidation = Double.parseDouble(this.percentInValidationCohort) / 100.0;

                List<TreeSet<String>> results = cohortData.setValidationAndTestingCohorts(
                        discoveryPhene, discoveryLowCutoff, discoveryHighCutoff,
                        this.validationCohortComparisonThreshold,
                        // clinicalPhene, clinicalHighCutoff,
                        pheneConditions, percentInValidation
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
                row.add(this.percentInValidationCohort);
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

                /*
            row = new ArrayList<String>();
            row.add("Clincal Phene");
            row.add(this.clinicalPhene);
            validationCohortInfo.addRow(row);

            row = new ArrayList<String>();
            row.add("Clinical High Cutoff");
            row.add(this.clinicalHighCutoff + "");
            validationCohortInfo.addRow(row);
                 */


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

                validationCohortInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.VALIDATION_COHORT_INFO);

                cohortData.addToWorkbook(resultsWorkbook, CfeResultsSheets.COHORT_DATA);

                //-------------------------------------------
                // Create and save CFE results
                //-------------------------------------------
                CfeResults cfeResults = new CfeResults();

                if (discoveryResults.getResultsType().equals(CfeResultsType.DISCOVERY_COHORT)) {
                    cfeResults.setResultsType(CfeResultsType.VALIDATION_COHORT_ONLY);
                }
                else if (discoveryResults.getResultsType().equals(CfeResultsType.PRIORITIZATION_SCORES)) {
                    cfeResults.setResultsType(CfeResultsType.VALIDATION_COHORT);
                    cfeResults.addCsvAndTextFiles(discoveryResults);
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
                result = ERROR;
                String message = "Validation cohort creation error: " + exception.getLocalizedMessage();
                this.setErrorMessage(message);
                log.severe(message);
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                this.setExceptionStack(stackTrace);
            }  
        }
        
        return result;
    }
    	

    

    /**
     * Creates the phene visit CSV file that is used for input into the Python script
     * for calculating the hospitalization cohort.
     * 
     * @throws Exception
     */
    /*
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
        File pheneVisitsCsvFile = FileUtil.createTempFile("testing-phene-visits-",  ".csv");
        if (pheneVisitsCsv != null) {
            FileUtils.write(pheneVisitsCsvFile, pheneVisitsCsv, "UTF-8");
        }
        
        this.pheneVisitsFileName = pheneVisitsCsvFile.getAbsolutePath();
    }
    */
    
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
    
    
    public double getValidationCohortComparisonThreshold() {
        return validationCohortComparisonThreshold;
    }

    
    public void setValidationCohortComparisonThreshold(double validationCohortComparisonThreshold) {
        this.validationCohortComparisonThreshold = validationCohortComparisonThreshold;
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
