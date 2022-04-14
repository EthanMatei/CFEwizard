package cfe.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.struts2.interceptor.SessionAware;

import cfe.model.CfeResults;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;

/**
 * Action for viewing previously calculated discovery results.
 * 
 * @author Jim Mullen
 *
 */
public class CfeResultsDetailAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CfeResultsDetailAction.class.getName());

	private Map<String, Object> webSession;
	
    private List<String> fileNames;
	
	private String tempDir;
	
	private String errorMessage;
	
	private Long cfeResultsId;
	
	private CfeResults cfeResults;
    
	public String execute() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
		    try {
		        this.cfeResults = CfeResultsService.get(this.cfeResultsId);
		        
		        this.tempDir = System.getProperty("java.io.tmpdir");
		        
		        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
		    } catch (Exception exception) {
		        this.errorMessage = "The Discovery database could not be processed. " + exception.getLocalizedMessage();
		        result = ERROR;
		    }
		}
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

    public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    public CfeResults getCfeResults() {
        return cfeResults;
    }

    public void setCfeResults(CfeResults cfeResults) {
        this.cfeResults = cfeResults;
    }

}
