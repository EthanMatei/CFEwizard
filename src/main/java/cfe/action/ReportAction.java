package cfe.action;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

import cfe.action.ActionErrorException;
import cfe.model.CfeScores;
import cfe.model.reports.ReportException;
import cfe.model.reports.ReportGenerator;
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
	

    @SuppressWarnings("unchecked")
    private Map session;

    private String reportName;
    private String reportFileName;
    private String reportType;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
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
    		if (reportName == null || reportName.trim().equals("")) {
    			throw new ActionErrorException("No report name was specified.");
    		}

    		Object cfeScoresObject = session.get("cfeScores");
    		CfeScores cfeScores = (CfeScores) cfeScoresObject;

    		List<cfe.enums.CfeScoringWeights> weights = (List<cfe.enums.CfeScoringWeights>) session.get("weights");
    		List<cfe.enums.CfeValidationWeights> validationWeights =
    				(List<cfe.enums.CfeValidationWeights>) session.get("validationWeights");
    		
    		/*
    		Map<String, ScoreResults> scores;
    		Object scoresObject = session.get("scores");
    		scores = (Map<String, ScoreResults>) scoresObject;

    		List<cfe.enums.ScoringWeights> weights = (List<cfe.enums.ScoringWeights>) session.get("weights");

    		Results results = (Results) session.get("results");
            */
    		
    		/*********
    		if (session == null) {
    			String exceptionMessage = "You user information could not be retrieved."
    				+ " Please make sure that you are logged in.";
    			throw new ActionErrorException( exceptionMessage );	
    		}
    		 ***************/

    		reportFormat = Filter.filterNonAlphaNumeric( reportFormat );


    		try {
    		    log.info("Trying to generate report with name " + reportName + " and format " + reportFormat + ".");

    		    fileStream = ReportGenerator.generate( reportName,  reportFormat, cfeScores, weights, validationWeights );
    		    if (fileStream == null) {
    			    throw new ReportException("No data could be retrieved for this report.");
    		    }
    		}
    		catch (ReportException exception) {
    			throw new ActionErrorException( exception.getMessage() );
    		}

    		fileContentType = "application/vnd.ms-excel";

    		String firstLetter = reportName.substring(0,1);
    		String remainder   = reportName.substring(1);
    		String fileSuffix = ".xls";
    		if (reportFormat != null && reportFormat.equals("xlsx")) {
    			fileSuffix = ".xlsx";
    			fileContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    		}
    		fileName = firstLetter.toUpperCase() + remainder + fileSuffix;

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
}
