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
import cfe.utils.Authorization;
import cfe.utils.Filter;

/**
 * Struts2 action for displaying spreadsheet in .xlsx format
 * 
 * @author Jim Mullen
 *
 */
public class XlsxDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(XlsxDisplayAction.class);
	

    @SuppressWarnings("unchecked")
    private Map session;

    private String reportName;
    private String spreadsheetFilePath;
    private String spreadsheetFileName;
    private String reportType;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
    private String published;
    
    private int month;
    private int year;
    
    private String reportFormat;
	
    public XlsxDisplayAction() {
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
    		if (spreadsheetFilePath == null || spreadsheetFilePath.trim().equals("")) {
    			throw new ActionErrorException("No .xlsx file was specified.");
    		}

    	    Path path = Paths.get(spreadsheetFilePath); 
    	    spreadsheetFileName = path.getFileName().toString();
    	    String spreadsheetFileBaseName = FilenameUtils.getBaseName(spreadsheetFileName);


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

    		    XSSFWorkbook workbook = new XSSFWorkbook(spreadsheetFilePath);
    		    
    		    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    	        workbook.write(byteOut);
    		    byteOut.close();
    		    
                fileStream = new ByteArrayInputStream( byteOut.toByteArray() );
    		    if (fileStream == null) {
    			    throw new Exception("The file \"" + spreadsheetFilePath + "\" could not be retrieved.");
    		    }
    		//}
    		//catch (Exception exception) {
    		//	throw new ActionErrorException( exception.getMessage() );
    		//}

   			String fileSuffix = ".xlsx";
    		fileName = spreadsheetFileBaseName + fileSuffix;
    		fileContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    		log.info("Generating spreasheet " + fileName + " for file \"" + spreadsheetFilePath + "\".");
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

	public String getSpreadsheetFilePath() {
		return spreadsheetFilePath;
	}

	public void setSpreadsheetFilePath(String spreadsheetFilePath) {
		this.spreadsheetFilePath = spreadsheetFilePath;
	}

	public String getSpreadsheetFileName() {
		return spreadsheetFileName;
	}

	public void setSpreadsheetFileName(String spreadsheetFileName) {
		this.spreadsheetFileName = spreadsheetFileName;
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
