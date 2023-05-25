package cfe.action;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.enums.CfeTables;
import cfe.enums.prioritization.Tables;
import cfe.services.TableInfoService;
import cfe.utils.Authorization;
import cfe.utils.TableInfo;
import cfe.utils.WebAppProperties;

// http://struts.apache.org/release/2.3.x/docs/file-upload.html

/**
 * Action class for displaying the database status.
 * 
 * @author Jim Mullen
 *
 */
public class DatabaseStatusAction extends BaseAction implements SessionAware {


	private static final long serialVersionUID = -8998071047587570336L;
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(DatabaseStatusAction.class);
	
	private Map<String, Object> webSession;
	
    private Map<String,TableInfo> tableMap;
    private Map<String,TableInfo> prioritizationTableMap;
    
    private String dbHost;
    private String dbUser;

    public DatabaseStatusAction() {
        this.setCurrentTab("Special Functions");
        this.setCurrentSubTab("CFE Database Status");
    }
    
    
    public String execute() throws Exception {
    	
	    String result = SUCCESS;
	    
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
			dbHost = WebAppProperties.getDbHost();
			dbUser = WebAppProperties.getDbUsername();

		    this.tableMap = new TreeMap<String,TableInfo>();

		    for (CfeTables t: CfeTables.values()) {
		    	String className = "cfe.model." + t.getClassname();
		    	long count = TableInfoService.getCount(className);
		    	TableInfo tableInfo = new TableInfo(className, count, t.getTblName());
		        tableMap.put(t.getLabel(), tableInfo);
		    }
		    
		    
	        this.prioritizationTableMap = new TreeMap<String,TableInfo>();
	        
            for (Tables t: Tables.values()) {
                String className = "cfe.model.prioritization." + t.getClassname();
                long count = TableInfoService.getCount(className);
                TableInfo tableInfo = new TableInfo(className, count, t.getTblName());
                prioritizationTableMap.put(t.getLabel(), tableInfo);
            }
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
		    for (CfeTables t: CfeTables.values()) {
		    	String className = "cfe.model." + t.getClassname();
		    	TableInfoService.deleteAll(className);
		    }
	        
		    for (Tables t: Tables.values()) {
	            String className = "cfe.model.prioritization." + t.getClassname();
	            TableInfoService.deleteAll(className);
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
	
	public String getDbHost() {
		return this.dbHost;
	}
	
	public String getDbUser() {
		return this.dbUser;
	}

	public Map<String, TableInfo> getTableMap() {
	    return this.tableMap;
	}

    public Map<String, TableInfo> getPrioritizationTableMap() {
        return prioritizationTableMap;
    }
	
}
