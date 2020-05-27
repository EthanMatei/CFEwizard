package cfg.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfg.model.DatabaseUploadInfo;
import cfg.services.DatabaseUploadInfoService;
import cfg.utils.Authorization;

public class DatabaseList extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 5275127679668304884L;
	private static final Log log = LogFactory.getLog(DatabaseList.class);
	
	List<DatabaseUploadInfo> databaseUploadInfos; 
	
	private Map<String, Object> webSession;
		
	public String initialize() throws Exception {
	    String result = SUCCESS;
	    databaseUploadInfos = new ArrayList<DatabaseUploadInfo>();
		
	    if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
		    databaseUploadInfos = DatabaseUploadInfoService.getAll();
		    if (databaseUploadInfos == null) {
		    	log.warn("databaseUploadInfos returned as null");
		    }
		    else {
		        log.info("Number of database log entries: " + databaseUploadInfos.size());
		    }
		}

	    return result;
	}
	


	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}

	public Map<String, Object> getSession() {
		return webSession;
	}



	public List<DatabaseUploadInfo> getDatabaseUploadInfos() {
		return databaseUploadInfos;
	}

	public void setDatabaseUploadInfos(List<DatabaseUploadInfo> databaseUploadInfos) {
		this.databaseUploadInfos = databaseUploadInfos;
	}
	
	
}
