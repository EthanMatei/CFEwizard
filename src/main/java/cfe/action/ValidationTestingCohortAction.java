package cfe.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import cfe.model.CfeResults;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;

public class ValidationTestingCohortAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ValidationTestingCohortAction.class);

	private Map<String, Object> webSession;
    
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
	private Integer discoveryLowCutoff;
	private Integer discoveryHighCutoff;
	
	private String percentInValidationCohort;
	
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		} else {
            this.discoveryResultsList = CfeResultsService.getAllMetadata();
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
            XSSFSheet sheet = workbook.getSheet("cohort data");
            CohortDataTable cohortData = new CohortDataTable();
            cohortData.initializeToWorkbookSheet(sheet);
            cohortData.setKey("Subject Identifiers.PheneVisit");
            
            phenes = new ArrayList<String>();
            phenes.add("");
            phenes.addAll(cohortData.getPhenes());
        }
        return result;
    }
    
    public String process() throws Exception {
        String result = SUCCESS;
        
        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
        this.discoveryResults = CfeResultsService.get(discoveryId);

        XSSFWorkbook workbook = discoveryResults.getResultsSpreadsheet();
        XSSFSheet sheet = workbook.getSheet("cohort data");
        CohortDataTable cohortData = new CohortDataTable();
        cohortData.initializeToWorkbookSheet(sheet);
        cohortData.setKey("Subject Identifiers.PheneVisit");
        
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

}
