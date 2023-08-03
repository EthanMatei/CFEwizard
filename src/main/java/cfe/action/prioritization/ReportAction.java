package cfe.action.prioritization;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.action.SessionAware;

import cfe.action.ActionErrorException;
//import com.i2iconnect.model.SessionInfo;
import cfe.action.BaseAction;
import cfe.model.prioritization.GeneListInput;
import cfe.model.prioritization.ScoreResults;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.model.prioritization.reports.ReportException;
import cfe.model.prioritization.reports.ReportGenerator;
import cfe.model.prioritization.results.Results;
import cfe.utils.Authorization;
import cfe.utils.Filter;

/**
 * Struts2 action for displaying Excel scoring reports
 * 
 * @author Jim Mullen
 *
 */
public class ReportAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(ReportAction.class);
	

    private Map<String, Object> session;

    private String reportName;
    private String reportFileName;
    private String reportType;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
    private Long discoveryId;
    private Double discoveryScoreCutoff;
    private String geneListFileName;
    
    private String published;
    
    private int month;
    private int year;
    
    private String reportFormat;
	
    public ReportAction() {
        errorMessage    = "";
        fileName        = "";
        fileContentType = "";
        fileStream      = null;
        
        month = 0;
        year  = 0;
    }
    
    public void withSession(Map<String, Object> session) {
    	this.session = session;
    }    
	
    /**
     * For viewing reports.
     * 
     * @return
     */
    public String view() throws Exception {
    	String result = SUCCESS;

    	if (!Authorization.isLoggedIn(session)) {
    		result = LOGIN;
    	}
    	else {
    		//try {
    		if (reportName == null || reportName.trim().equals("")) {
    			throw new ActionErrorException("No report name was specified.");
    		}

    		Map<String, ScoreResults> scores;
    		Object scoresObject = session.get("scores");
    		scores = (Map<String, ScoreResults>) scoresObject;

            GeneListInput geneListInput = (GeneListInput) session.get("geneListInput");
            if (geneListInput == null) {
                geneListInput = new GeneListInput();
            }
            
    		List<DiseaseSelector> diseaseSelectors;
    		diseaseSelectors = (List<DiseaseSelector>) session.get("diseaseSelectors");

    		List<cfe.enums.prioritization.ScoringWeights> weights
    		    = (List<cfe.enums.prioritization.ScoringWeights>) session.get("weights");

    		Results results = (Results) session.get("results");

    		/*********
    		if (session == null) {
    			String exceptionMessage = "You user information could not be retrieved."
    				+ " Please make sure that you are logged in.";
    			throw new ActionErrorException( exceptionMessage );	
    		}
    		 ***************/

    		reportFormat = Filter.filterNonAlphaNumeric( reportFormat );


    		//try {
    		log.info("Trying to generate report with name " + reportName + " and format " + reportFormat + ".");

    		fileStream = ReportGenerator.generate(
    		        reportName, 
    		        reportFormat,
    		        results,
    		        scores,
    		        weights,
    		        diseaseSelectors,
    		        geneListInput,
    		        discoveryId,
    		        discoveryScoreCutoff,
    		        geneListFileName
    		);
    		if (fileStream == null) {
    			throw new ReportException("No data could be retrieved for this report.");
    		}
    		//}
    		//catch (ReportException exception) {
    		//	throw new ActionErrorException( exception.getMessage() );
    		//}

    		fileContentType = "application/vnd.ms-excel";

    		String firstLetter = reportName.substring(0,1);
    		String remainder   = reportName.substring(1);
    		String fileSuffix = ".xls";
    		if (reportFormat != null && reportFormat.equals("xlsx")) {
    			fileSuffix = ".xlsx";
    			fileContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    		}

    		fileName = firstLetter.toUpperCase() + remainder + fileSuffix;
            
            if (reportName.contentEquals("diseases-with-coefficients")) {
                fileSuffix = ".csv";
                fileContentType = "text/csv";
                fileName = "diseases" + fileSuffix; 
            }
            
    		log.info("Generating report " + fileName + " in " + reportFormat + " format.");
    		//fileName        = "report.xls";
    		//}
    		//catch (ActionErrorException exception) {
    		//	errorMessage = exception.getMessage();
    		//	result = ERROR;
    		//}
    	}

    	return result;
    }
    

	//-----------------------------------------
	// Getters and Setters
	//-----------------------------------------
	public InputStream getFileStream() {
		return fileStream;
	}

	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}
	
	public String getReportFormat() {
		return reportFormat;
	}

	public void setReportFormat(String reportFormat) {
		this.reportFormat = reportFormat;
	}

    public Long getDiscoveryId() {
        return discoveryId;
    }

    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }

    public Double getDiscoveryScoreCutoff() {
        return discoveryScoreCutoff;
    }

    public void setDiscoveryScoreCutoff(Double discoveryScoreCutoff) {
        this.discoveryScoreCutoff = discoveryScoreCutoff;
    }

    public String getGeneListFileName() {
        return geneListFileName;
    }

    public void setGeneListFileName(String geneListFileName) {
        this.geneListFileName = geneListFileName;
    }
	
	
}
