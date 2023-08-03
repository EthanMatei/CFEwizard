package cfe.action;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.action.SessionAware;

import cfe.utils.Authorization;
import cfe.utils.TemporaryFileInfo;
import cfe.utils.WebAppProperties;

// http://struts.apache.org/release/2.3.x/docs/file-upload.html

/**
 * Action class for displaying the database status.
 * 
 * @author Jim Mullen
 *
 */
public class TemporaryFilesAction extends BaseAction implements SessionAware {
	
	@SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(TemporaryFilesAction.class.getName());
	
	private Map<String, Object> webSession;
    
    private String tempDir;
    private List<TemporaryFileInfo> tempFileInfos;
    private Integer deleteAge;
    
    public TemporaryFilesAction() {
        this.setCurrentTab("Admin");
        this.setCurrentSubTab("Temporary Files");
    }
    
    public String execute() throws Exception {
    	
	    String result = SUCCESS;
	    
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
			this.tempDir = WebAppProperties.getTempDir();
			this.tempFileInfos = TemporaryFileInfo.getTemporaryFileInfos(tempDir);
	    }

        return result;
    }
    
    /**
     * Clears all tables (i.e., deletes all records from tables).
     *
     * @return status
     * @throws Exception
     */
    public String delete() throws Exception {
	    String result = SUCCESS;
	    
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
	        this.tempDir = WebAppProperties.getTempDir();
	        this.tempFileInfos = TemporaryFileInfo.getTemporaryFileInfos(tempDir);
	        
	        if (deleteAge == null || deleteAge <= 0) {
	            throw new Exception("The value specified for temporary file deletion must be a positive integer.");
	        }
	        
	        for (TemporaryFileInfo fileInfo: tempFileInfos) {
	            File file = new File(tempDir + "/" + fileInfo.getName());
	            if (fileInfo.getAgeInDays() > deleteAge) {
	                file.delete();
	            }
	        }
	    }

        return result;        
    }

	public void withSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public Map<String, Object> getSession() {
		return webSession;
	}

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public List<TemporaryFileInfo> getTempFileInfos() {
        return tempFileInfos;
    }

    public void setTempFileInfos(List<TemporaryFileInfo> tempFileInfos) {
        this.tempFileInfos = tempFileInfos;
    }

    public Integer getDeleteAge() {
        return deleteAge;
    }

    public void setDeleteAge(Integer deleteAge) {
        this.deleteAge = deleteAge;
    }
    
}
