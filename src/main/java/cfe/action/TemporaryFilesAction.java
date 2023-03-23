package cfe.action;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.enums.CfeTables;
import cfe.enums.prioritization.Tables;
import cfe.services.TableInfoService;
import cfe.utils.Authorization;
import cfe.utils.TableInfo;
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
    public String clear() throws Exception {
	    String result = SUCCESS;
	    
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
	    }

        return result;        
    }

	public void setSession(Map<String, Object> session) {
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
    
    
}
