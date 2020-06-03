package cfe.action;


import java.io.File;
// import java.io.IOException;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.parser.MSAccessParser;
import cfe.services.DisorderService;
import cfe.utils.Authorization;

// http://struts.apache.org/release/2.3.x/docs/file-upload.html

/**
 * Action class for uploading MS Access database files.
 * 
 * @author Michel Tavares
 *
 */
public class FileUpload extends BaseAction implements SessionAware {


	private static final long serialVersionUID = -8998071047587570336L;
	private List<File> uploads = new ArrayList<File>();
	private List<String> uploadFileNames = new ArrayList<String>();
	private List<String> uploadContentTypes = new ArrayList<String>();
	private static List<String> validationMsgs = new ArrayList<String>(10);
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(FileUpload.class);
	
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
    	
	    String result = SUCCESS;
	    
		//if (!Authorization.isAdmin(webSession)) {
		//	result = LOGIN;
		//}
		//else {
			if (uploadFileNames.isEmpty())	{
				addActionError("You must select at least one database file to upload" );
				return INPUT;
			}

			//---------------------------------------
			// Check for valid file extensions
			//---------------------------------------
			for (String filename: uploadFileNames) {
				if (filename.endsWith(".accdb") == false)	{
					addActionError(filename + ": invalid file extension. File must end with .accdb" );
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
			for (String dup: uploadFileNames)
			{
				if (!uploadFileNamesSet.add(dup))
					duplist.append(dup + ", ");
			}

			if (uploadFileNamesSet.size() != uploadFileNames.size() ) {

				addActionError("Duplicate databases detected. The following database(s) " + duplist + " were entered more than once.");
				duplist = null;
				uploadFileNamesSet = null;
				return INPUT;	
			}
			duplist = null;
			uploadFileNamesSet = null;


			//----------------------------------------------------------
			// Create temporary copies of input files
			//----------------------------------------------------------
			List<File> filesToProcess = new ArrayList<File>();

			for (File f: uploads)	{
				File tmp = File.createTempFile("dbupload", ".cfg", null);
				FileUtils.copyFile(f, tmp);
				filesToProcess.add(tmp);
			}

			// Decided to use serial instead of parallel version
			// to prevent the case in which user upload the same file twice
			// Currently there is no logic to prevent that
			/*
    	MSAccessParser msparser = new MSAccessParser();

    	for (File f: filesToProcess) {	
    		msparser.parse(f.getAbsolutePath());
    		validationMsgs.addAll(msparser.getValidationMsgs());		
    	}
    	msparser = null;

    	// This works. 
			 */
			int NUM_THREADS = Math.min(uploadFileNames.size() ,Runtime.getRuntime().availableProcessors());

			List<Future<Long>> list = new ArrayList<Future<Long>>();
			ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS); //ThreadUtils.getExecutor();

			try {
				//for (String filename: uploadFileNames) {
				for (File f: filesToProcess) {
					Worker worker = new Worker(f.getPath());

					// I don't care about the return value
					Future<Long> submit = executor.submit(worker);

					list.add(submit);
				}

				// Force exception if necessary
				@SuppressWarnings("unused")
				long sum = 0;
				for (Future<Long> future : list) {
					sum += future.get();
				}

				// This will make the executor accept no new threads
				// and finish all existing threads in the queue
				executor.shutdown();

				// Wait until all threads are finished
				executor.awaitTermination(50L, TimeUnit.SECONDS);

				// Update the disorders table
				DisorderService.update();

				webSession.put("uploaded",uploadFileNames);
				if (!validationMsgs.isEmpty())
					webSession.put("vMsgs",validationMsgs);

			}	
			catch (Exception ioe)	{			
				executor.shutdownNow();
				Thread.currentThread().interrupt();
				throw new Exception(ioe);			
			}


			//_session.put("uploaded",uploadFileNames);
			//if (!validationMsgs.isEmpty())
			//	_session.put("vMsgs",validationMsgs);
		//}
		
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
}


class Worker implements Callable<Long> {
	
	private String filename;

	Worker(String filename){
		
		this.filename = filename;
	}

	public Long call() throws Exception{
	
		MSAccessParser msparser = new MSAccessParser();
				
		// Parse the file
		msparser.parse(filename);
		
		FileUpload.getValidationMsgs().addAll(msparser.getValidationMsgs());
				
		return 1L;
	}
}
