package cfe.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.action.SessionAware;

import cfe.model.CfeResults;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;

/**
 * Struts2 action for displaying CFE results spreadsheet in .xlsx format
 * 
 * @author Jim Mullen
 *
 */
public class CfeResultsXlsxDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(CfeResultsXlsxDisplayAction.class);
	

    private Map<String, Object> session;

    private String reportName;
    private Long cfeResultsId;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
    private String published;
    
    private int month;
    private int year;
	
    public CfeResultsXlsxDisplayAction() {
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
    		if (cfeResultsId == null) {
    			throw new ActionErrorException("No cfeResultsId argument was specified.");
    		}
    		
    		log.info("cfeResultsId: " + cfeResultsId);
    		CfeResults cfeResults = CfeResultsService.get(cfeResultsId);

    		//try {
    		    
                fileStream = new ByteArrayInputStream( cfeResults.getResults() );
    		    if (fileStream == null) {
    			    throw new Exception("The dicovery results with ID \""
    		            + cfeResultsId + "\" could not be retrieved.");
    		    }
    		//}
    		//catch (Exception exception) {
    		//	throw new ActionErrorException( exception.getMessage() );
    		//}

   			if (fileName == null || fileName.isEmpty()) {
    		    fileName = "cfe-results.xlsx";
    		}
    		reportName = fileName;
    		fileContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
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

	public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
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

}
