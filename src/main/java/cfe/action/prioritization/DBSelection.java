package cfe.action.prioritization;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.action.BaseAction;

import cfe.utils.Authorization;

public class DBSelection extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 5275127679668304884L;
	private static final Log log = LogFactory.getLog(DBSelection.class);
	
	private String[] dbnames;
	
	private Map<String, Object> webSession;
		
	public String initialize() throws Exception {
	    String result = SUCCESS;
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    return result;
	}
	
	public String execute() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
		    log.info("List of selected databases:");
		
		    for (int i = 0; i < dbnames.length; i++)	{
			    dbnames[i] = cfe.enums.prioritization.Databases.valueOf(dbnames[i]).getLabel();	
			    log.info(dbnames[i]);
		    }

		    webSession.put("dbnames", dbnames);
		}
		return result;
	}

	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}

	public String[] getDbnames() {
		return dbnames;
	}


	public void setDbnames(String[] dbnames) {
		this.dbnames = dbnames;
	}
	
	public void validate() {
		
		if (dbnames.length == 0)
			addActionError( "You must select at least one database" );
	}

	public Map<String, Object> getSession() {
		return webSession;
	}	
}
