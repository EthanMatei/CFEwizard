package cfe.action;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.ServletActionContext;

import cfe.action.ActionErrorException;
import cfe.model.discovery.DiscoveryResults;
import cfe.services.discovery.DiscoveryResultsService;
import cfe.utils.Authorization;
import cfe.utils.Filter;

/**
 * Struts2 action for displaying spreadsheet in .xlsx format
 * 
 * @author Jim Mullen
 *
 */
public class DiscoveryResultsXlsxDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(DiscoveryResultsXlsxDisplayAction.class);
	

    @SuppressWarnings("unchecked")
    private Map session;

    private String reportName;
    private Integer discoveryResultsId;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
    private String published;
    
    private int month;
    private int year;
	
    public DiscoveryResultsXlsxDisplayAction() {
        errorMessage    = "";
        fileName        = "";
        fileContentType = "";
        fileStream      = null;
        
        month = 0;
        year  = 0;
    }
    
    @SuppressWarnings("unchecked")
    public void setSession(Map session) {
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
    		if (discoveryResultsId == null) {
    			throw new ActionErrorException("No Discovery Results ID was specified.");
    		}
    		
    		log.info("discoveryResultsId: " + discoveryResultsId);
    		DiscoveryResults discoveryResults = DiscoveryResultsService.get(discoveryResultsId);

    		//try {
    		    
                fileStream = new ByteArrayInputStream( discoveryResults.getResults() );
    		    if (fileStream == null) {
    			    throw new Exception("The dicovery results with ID \""
    		            + discoveryResultsId + "\" could not be retrieved.");
    		    }
    		//}
    		//catch (Exception exception) {
    		//	throw new ActionErrorException( exception.getMessage() );
    		//}

   			String fileSuffix = ".xlsx";
    		fileName = "discovery-results" + fileSuffix;
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

	public Integer getDiscoveryResultsId() {
        return discoveryResultsId;
    }

    public void setDiscoveryResultsId(Integer discoveryResultsId) {
        this.discoveryResultsId = discoveryResultsId;
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
