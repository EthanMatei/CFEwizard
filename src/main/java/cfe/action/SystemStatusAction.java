package cfe.action;

import java.sql.DatabaseMetaData;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.utils.Authorization;
import cfe.utils.WebAppProperties;

// http://struts.apache.org/release/2.3.x/docs/file-upload.html

/**
 * Action class for displaying system information.
 * 
 * @author Michel Tavares
 *
 */
public class SystemStatusAction extends BaseAction implements SessionAware {


	private static final long serialVersionUID = 1L;
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(SystemStatusAction.class);
	
	private Map<String, Object> webSession;
	
	private Map<String,String> applicationProperties;
	private Map<String,String> systemProperties;
	private Map<String,String> environmentVariables;

    public String execute() throws Exception {
    	
	    String result = SUCCESS;
	    
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
		    this.applicationProperties = new TreeMap<String, String>();
		    this.applicationProperties.put(WebAppProperties.TEMP_DIR, WebAppProperties.getTempDir());
            this.applicationProperties.put(WebAppProperties.PYTHON3_PATH_PROPERTY, WebAppProperties.getPython3Path());
		    this.applicationProperties.put(WebAppProperties.RSCRIPT_PATH_PROPERTY, WebAppProperties.getRscriptPath());
		    
    		String[] props = {
	    		"java.home", "java.class.path", "java.version",
		    	"os.name", "os.version", "user.home", "user.name"
		    };
		    this.systemProperties = new TreeMap<String, String>();
		    for (String prop: props) {
			    this.systemProperties.put(prop, System.getProperty(prop, ""));
		    }
		    
		    this.environmentVariables = System.getenv();   
	    }

        return result;
    }

	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public Map<String, String> getApplicationProperties() {
        return applicationProperties;
    }

    public Map<String, Object> getSession() {
		return webSession;
	}
	
	public Map<String, String> getSystemProperties() {
	    return this.systemProperties;
	}
	
	public Map<String, String> getEnvironmentVariables() {
		return this.environmentVariables;
	}
}
