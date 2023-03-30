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
import org.apache.struts2.action.SessionAware;
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
public class StringToTextFileDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(StringToTextFileDisplayAction.class);
	

    private Map<String, Object> session;

    private String value;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
	
    public StringToTextFileDisplayAction() {
        errorMessage    = "";
        fileName        = "";
        fileContentType = "";
        fileStream      = null;
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
    	    if (value == null) {
    	        value = "";
    	    }
    	    
    	    fileStream = new ByteArrayInputStream(value.getBytes());

  		    if (fileStream == null) {
   			    throw new Exception("The data could not be converted to a file streamd.");
    		}

    		if (fileName == null || fileName.trim().isEmpty()) {
    		    fileName = "data.txt";
    		};
    		
    		if (!fileName.endsWith(".txt")) {
    		    fileName += ".txt";
    		}
    		
    		fileContentType = "text/plain";

    		log.info("Displaying string as text file \"" + fileName + "\".");
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

	public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

}
