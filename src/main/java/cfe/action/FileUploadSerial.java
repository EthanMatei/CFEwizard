package cfe.action;


import java.io.File;
// import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
//import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.enums.Databases;
import cfe.model.DatabaseUploadInfo;
import cfe.parser.MSAccessParser;
import cfe.services.DatabaseUploadInfoService;
import cfe.utils.Authorization;
import cfe.utils.ParseResult;

// http://struts.apache.org/release/2.3.x/docs/file-upload.html

/**
 * Action class for uploading MS Access database files serially.
 * Authorization checking did not work in the parallel version.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class FileUploadSerial extends BaseAction implements SessionAware {

	private static final long serialVersionUID = -8998071047587570336L;
	private List<File> uploads = new ArrayList<File>();
	private List<String> uploadFileNames = new ArrayList<String>();
	private List<String> uploadContentTypes = new ArrayList<String>();
	private static List<String> validationMsgs = new ArrayList<String>(10);
	private List<ParseResult> parseResults = new ArrayList<ParseResult>();
	
	private List<String> dbnames = new ArrayList();
	private Long uploadTimeMilliseconds;
	private String uploadTime;
	
	private String errorMessage;

	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(FileUploadSerial.class);
	
	private Map<String, Object> webSession;

    public List<File> getUpload() {
        return this.uploads;
    }

    public void setUpload(List<File> uploads) {
        this.uploads = uploads;        
    }

    public List<String> getUploadFileName() {
        return this.uploadFileNames;
    }
    
    public void setUploadFileName(List<String> uploadFileNames) {
        this.uploadFileNames = uploadFileNames;
    }

    public List<String> getUploadContentType() {
        return this.uploadContentTypes;
    }
    
    public void setUploadContentType(List<String> contentTypes) {
        this.uploadContentTypes = contentTypes;
    }

    public String execute() throws Exception {
    	log.info("Starting file upload.");
    	
    	this.errorMessage = "";
    	String result = SUCCESS;

    	long startTime = new Date().getTime();

    	if (!Authorization.isAdmin(webSession)) {
    		result = LOGIN;
    	}
    	else {

    		if (uploadFileNames.isEmpty())	{
    		    String errorMessage = "No files were specified for upload.";
    			addActionError( errorMessage );
    			log.error( errorMessage );
    			return INPUT;
    		}
    		else if (uploadFileNames.size() < dbnames.size())	{
    			int diff = dbnames.size() - uploadFileNames.size();
    			String errorMessage = "All databases selected must have an upload file."
					                  + " Number of database with missing files: " + diff;
    			addActionError( errorMessage );
    			log.error( errorMessage );
    			return INPUT;
    		}

    		//---------------------------------------
    		// Check for valid file extensions
    		//---------------------------------------

    		for (String filename: uploadFileNames) {

    			//log.info("***************************** UPLOAD FILE NAME: " + filename);
    			//log.info("------------------------------- i: " + i);
    			//log.info("------------------------------- dbanme: " + dbnames.get(i));
    			//log.info("------------------------------- dbanme enum: " + Databases.fromLabel( dbnames.get(i) ) );


    			if (filename.endsWith(".accdb") == false)	{
    				String errorMessage = filename + ": invalid file extension. File must end with .accdb";
    				addActionError( errorMessage );
    				log.error( errorMessage );
    				return INPUT;
    			}    		
    		}

    		validationMsgs.clear();

    		//---------------------------------------------------------------
    		// Test filename duplicates by creating a set from the list
    		// of filenames and checking if the size matches (which it only
    		// will if there are no duplicates)
    		//---------------------------------------------------------------
    		Set<String> uploadFileNamesSet = new HashSet<String>(uploadFileNames.size());
    		StringBuffer duplist = new StringBuffer();

    		for (String dup: uploadFileNames) {
    			if (!uploadFileNamesSet.add(dup))
    				duplist.append(dup + ", ");
    		}

    		if (uploadFileNamesSet.size() != uploadFileNames.size() ) {
    			String errorMessage = "Duplicates Databases detected. The following database(s) " + duplist + " were entered more than once.";
    			addActionError( errorMessage );
    			log.error( errorMessage );
    			duplist = null;
    			uploadFileNamesSet = null;
    			return INPUT;	
    		}
    		duplist = null;
    		uploadFileNamesSet = null;

    		//-------------------------------------------------------------
    		// Process the database files
    		//-------------------------------------------------------------
    		int i = 0;
    		try {
	    		MSAccessParser msparser = new MSAccessParser();

	    		for (File f: uploads) {
	    			log.info("Parsing file \"" + f.getAbsolutePath() + "\"");
	    			
	    			Exception parseException = null;
	    			try {
	    			    msparser.parse(f.getAbsolutePath());
	    			} catch (Exception exception) {
	    				parseException = exception;
	    			}
	    			
	    			// Get the parse information, even if an exception occurred (if possible)
	    			if (msparser != null) {
	    			    log.info("Getting validation messages for file \"" + f.getAbsolutePath() + "\"");
	    			    validationMsgs.addAll(msparser.getValidationMsgs());
	    			
	    			    ParseResult parseResult = msparser.getParseResult();
	    			    // Reset the file name to the original MS Access file name.
	    			    // The parser will have the temporary file name.
	    			    parseResult.setFileName( uploadFileNames.get(i));
	    			    parseResults.add(parseResult);
	    			}
	    			
	    			// If an exception did occur, re-throw it now, after getting the parse information
	    			if (parseException != null) {
	    				throw parseException;
	    			}
	    			
	    			DatabaseUploadInfo info = new DatabaseUploadInfo();
	    			String dbName = Databases.fromLabel( dbnames.get(i) ).toString();
	    			
	    			log.info("dbName: " + dbName);
	    			info.setDatabaseName(dbName);
	    			
	    			info.setUploadFileName( uploadFileNames.get(i) );
	    			Date uploadTime = new Date();
	    			info.setUploadTime(uploadTime);
	    			
	    			log.info("Calling DatabaseUploadInfoService.update for file \"" + f.getAbsolutePath() + "\"");
	    			DatabaseUploadInfoService.update(info);
	    			i++;
	    		}
	    		msparser = null;
    		} catch (Exception exception) {
    			exception.printStackTrace();
    			this.errorMessage = "Database upload error for " + uploadFileNames.get(i) + ": " + exception.getLocalizedMessage();
    			Throwable cause = exception.getCause();
    			if (cause != null) {
    				this.errorMessage += ": " + cause.getLocalizedMessage();
    			}
    			for (int j = uploadFileNames.size() - 1; j >= i; j--) {
    				uploadFileNames.remove(j);
    			}
	            addActionError( this.errorMessage );
	            log.error( this.errorMessage );
	            result = ERROR;
    		}

    		//-------------------------------------------------------------
    		// Store status information in the session
    		// Should this be moved to a variable for serial version??? 
    		//-------------------------------------------------------------
   		    webSession.put("uploaded",uploadFileNames);
    	}

		
		//--------------------------------------------------------------------
		// Calculate upload time and created formatted string version
		//--------------------------------------------------------------------
		long endTime = new Date().getTime();
		uploadTimeMilliseconds = endTime - startTime;
		long totalSeconds = Math.round( uploadTimeMilliseconds / 1000.0 );
		long minutes = totalSeconds / 60;
		long seconds = totalSeconds % 60;
		uploadTime = "";
		if (minutes > 0) uploadTime = minutes + " minutes ";
		uploadTime += seconds + " seconds";
				
        return result;
    }
    
    

	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	public Map<String, Object> getSession() {
		return webSession;
	}
	
	public static List<String> getValidationMsgs() {
		return validationMsgs;
	}
	
	public List<ParseResult> getParseResults() {
		return this.parseResults;
	}

	public List<String> getDbnames() {
		return dbnames;
	}

	public void setDbnames(List<String> dbnames) {
		this.dbnames = dbnames;
	}

	public Long getUploadTimeMilliseconds() {
		return uploadTimeMilliseconds;
	}

	public void setUploadTimeMilliseconds(Long uploadTimeMilliseconds) {
		this.uploadTimeMilliseconds = uploadTimeMilliseconds;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}
	
	public String getErrorMessage()
	{
		return this.errorMessage;
	}

}


