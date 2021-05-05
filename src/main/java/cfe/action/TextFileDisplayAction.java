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
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.ServletActionContext;


import cfe.action.ActionErrorException;
import cfe.utils.Authorization;
import cfe.utils.Filter;

/**
 * Struts2 action for displaying text files.
 * 
 * @author Jim Mullen
 *
 */
public class TextFileDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(TextFileDisplayAction.class);
	

    @SuppressWarnings("unchecked")
    private Map session;

    private String textFilePath;
    private String textFileName;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
    private String reportFileName;
	
    public TextFileDisplayAction() {
        errorMessage    = "";
        fileName        = "";
        fileContentType = "";
        fileStream      = null;
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
    		if (textFilePath == null || textFilePath.trim().equals("")) {
    			throw new ActionErrorException("No text file was specified.");
    		}

    		reportFileName = "R script output";
    	    Path path = Paths.get(textFilePath); 
    	    textFileName = path.getFileName().toString();
    	    String textFileBaseName = FilenameUtils.getBaseName(textFileName);

    	    File file = new File(textFilePath);
    		fileStream = new FileInputStream(file);

  		    if (fileStream == null) {
   			    throw new Exception("The file \"" + textFilePath + "\" could not be retrieved.");
    		}

   			String fileSuffix = ".txt";
    		fileName = textFileBaseName + fileSuffix;
    		fileContentType = "text/plain";

    		log.info("Displaying text file \"" + textFilePath + "\".");
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

	public String getTextFilePath() {
		return textFilePath;
	}

	public void setTextFilePath(String textFilePath) {
		this.textFilePath = textFilePath;
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

	public String getTextFileName() {
		return textFileName;
	}

	public void setTextFileName(String textFileName) {
		this.textFileName = textFileName;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public void setReportFileName(String reportFileName) {
		this.reportFileName = reportFileName;
	}
	
}
