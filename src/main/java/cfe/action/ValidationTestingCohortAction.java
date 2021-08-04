package cfe.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.DataTable;
import cfe.utils.PheneCondition;

public class ValidationTestingCohortAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ValidationTestingCohortAction.class);

	private Map<String, Object> webSession;
    
	public static final Long RANDOM_SEED = 10972359723095792L;
	
	private List<CfeResults> discoveryResultsList;
	private CfeResults discoveryResults;
	
	private ArrayList<String> phenes;
	
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
	
	private String discoveryPhene;
	private String discoveryPheneTable;
	private Integer discoveryLowCutoff;
	private Integer discoveryHighCutoff;
	
	private String percentInValidationCohort;
	
	TreeSet<String> cohortSubjects;
	TreeSet<String> validationSubjects;
	TreeSet<String> testingSubjects;
	
	private Long cfeResultsId;
	
	public ValidationTestingCohortAction() {
	    this.cohortSubjects     = new TreeSet<String>();
	    this.validationSubjects = new TreeSet<String>();
	    this.testingSubjects    = new TreeSet<String>();
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
            ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
            this.discoveryResults = CfeResultsService.get(discoveryId);

            XSSFWorkbook workbook = discoveryResults.getResultsSpreadsheet();
            XSSFSheet sheet = workbook.getSheet("cohort data");
            CohortDataTable cohortData = new CohortDataTable();
            cohortData.initializeToWorkbookSheet(sheet);
            cohortData.setKey("Subject Identifiers.PheneVisit");

            double value;
            PheneCondition pheneCondition;
            List<PheneCondition> pheneConditions = new ArrayList<PheneCondition>();

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

            this.cohortSubjects = cohortData.getValidationAndTestingCohortSubjects(
                    discoveryPhene, discoveryLowCutoff, discoveryHighCutoff, pheneConditions
                    );

            List<String> subjects = new ArrayList<String>();
            subjects.addAll(cohortSubjects);

            Random rand = new Random(RANDOM_SEED);
            Collections.shuffle(subjects, rand);
            int count = subjects.size();

            for (int i = 0; i < count; i++) {
                double percent = (i + 1.0) / count;
                double percentInValidation = Double.parseDouble(this.percentInValidationCohort) / 100.0;

                if (percent <= percentInValidation) {
                    this.validationSubjects.add(subjects.get(i));
                }
                else {
                    this.testingSubjects.add(subjects.get(i));
                }
            }


            //-------------------------------------------------------------------------------
            // Create new CFE results that has all the cohorts plus previous information
            //-------------------------------------------------------------------------------
            XSSFWorkbook resultsWorkbook = new XSSFWorkbook();
            
            // Discovery cohort table
            DataTable discoveryCohort = new DataTable(null);
            discoveryCohort.initializeToWorkbookSheet(workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT));
            discoveryCohort.addToWorkbook(resultsWorkbook, CfeResultsSheets.DISCOVERY_COHORT);

            // Discovery cohort info table
            DataTable discoveryCohortInfo = new DataTable(null);
            discoveryCohortInfo.initializeToWorkbookSheet(workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO));
            discoveryCohortInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.DISCOVERY_COHORT_INFO);           
            
            // Create validation cohort data table
            DataTable validationCohort = new DataTable("Subject");
            validationCohort.addColumn("Subject",  "");
            for (String subject: validationSubjects) {
                ArrayList<String> row = new ArrayList<String>();
                row.add(subject);
                validationCohort.addRow(row);
            }
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
            row.add("constraint1");
            if (!this.phene1.isEmpty() && !this.value1.isEmpty()) {
                row.add(this.phene1 + " " + this.operator1 + " " + this.value1);
            }
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("constraint2");
            if (!this.phene2.isEmpty() && !this.value2.isEmpty()) {
                row.add(this.phene2 + " " + this.operator2 + " " + this.value2);
            }
            validationCohortInfo.addRow(row);            

            row = new ArrayList<String>();
            row.add("constraint3");
            if (!this.phene3.isEmpty() && !this.value3.isEmpty()) {
                row.add(this.phene3 + " " + this.operator3 + " " + this.value3);
            }
            validationCohortInfo.addRow(row);
            
            row = new ArrayList<String>();
            row.add("% in validation cohort specified");
            row.add(this.percentInValidationCohort);
            validationCohortInfo.addRow(row);
            
            validationCohortInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.VALIDATION_COHORT_INFO);
            
            // Create (all) cohort data table
            CohortDataTable cohortDataDataTable = new CohortDataTable();
            cohortDataDataTable.initializeToWorkbookSheet(workbook.getSheet(CfeResultsSheets.COHORT_DATA));
            cohortDataDataTable.addCohort("validation", validationSubjects);
            cohortDataDataTable.addCohort("testing", testingSubjects);
            String[] sortColumns = {"Cohort", "Subject", "Subject Identifiers.PheneVisit"};
            cohortDataDataTable.sortWithBlanksLast(sortColumns);
            cohortDataDataTable.addToWorkbook(resultsWorkbook, CfeResultsSheets.COHORT_DATA);

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
        }
        
        return result;
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

}
