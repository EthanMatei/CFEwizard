package cfe.action;

import java.sql.DatabaseMetaData;
import java.text.NumberFormat;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.action.SessionAware;

import cfe.utils.Authorization;
import cfe.utils.WebAppProperties;

// http://struts.apache.org/release/2.3.x/docs/file-upload.html

/**
 * Action class for displaying system information.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
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

    private long currentHeapSize;

    private long maxHeapSize;

    private long currentFreeHeapSize;

    private String currentHeapSizeFormatted;

    private String maxHeapSizeFormatted;

    private String currentFreeHeapSizeFormatted;

    public SystemStatusAction() {
        this.setCurrentTab("Admin"); 
        this.setCurrentSubTab("System Status");
    }
    
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
		    
		    this.currentHeapSize     = Runtime.getRuntime().totalMemory(); 
		    this.maxHeapSize         = Runtime.getRuntime().maxMemory();
		    this.currentFreeHeapSize = Runtime.getRuntime().freeMemory();
		    
		    this.currentHeapSizeFormatted     = NumberFormat.getInstance().format(this.currentHeapSize);
		    this.maxHeapSizeFormatted         = NumberFormat.getInstance().format(this.maxHeapSize);
		    this.currentFreeHeapSizeFormatted = NumberFormat.getInstance().format(this.currentFreeHeapSize);
	    }

        return result;
    }

	public void withSession(Map<String, Object> session) {
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

    public long getCurrentHeapSize() {
        return currentHeapSize;
    }

    public void setCurrentHeapSize(long currentHeapSize) {
        this.currentHeapSize = currentHeapSize;
    }

    public long getMaxHeapSize() {
        return maxHeapSize;
    }

    public void setMaxHeapSize(long maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
    }

    public long getCurrentFreeHeapSize() {
        return currentFreeHeapSize;
    }

    public void setCurrentFreeHeapSize(long currentFreeHeapSize) {
        this.currentFreeHeapSize = currentFreeHeapSize;
    }

    public String getCurrentHeapSizeFormatted() {
        return currentHeapSizeFormatted;
    }

    public void setCurrentHeapSizeFormatted(String currentHeapSizeFormatted) {
        this.currentHeapSizeFormatted = currentHeapSizeFormatted;
    }

    public String getMaxHeapSizeFormatted() {
        return maxHeapSizeFormatted;
    }

    public void setMaxHeapSizeFormatted(String maxHeapSizeFormatted) {
        this.maxHeapSizeFormatted = maxHeapSizeFormatted;
    }

    public String getCurrentFreeHeapSizeFormatted() {
        return currentFreeHeapSizeFormatted;
    }

    public void setCurrentFreeHeapSizeFormatted(String currentFreeHeapSizeFormatted) {
        this.currentFreeHeapSizeFormatted = currentFreeHeapSizeFormatted;
    }
	
}
