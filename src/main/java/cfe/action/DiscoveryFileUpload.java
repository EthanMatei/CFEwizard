package cfe.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.utils.Authorization;
import cfe.utils.WebAppProperties;

public class DiscoveryFileUpload extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(DiscoveryFileUpload.class);

	private Map<String, Object> webSession;
	
	private File discoveryCsv;
	private String discoveryCsvContentType;
	private String discoveryCsvFileName;
	
	private File discoveryDb;
	private String discoverDbContentType;
	private String dicoveryDbFileName;
	
	private String baseDir;
	
	public String initialize() throws Exception {
	    return SUCCESS;
	}
	
	public String execute() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
			//baseDir = System.getProperty("user.dir");
			//baseDir = System.getProperty("user.dir");
			baseDir = WebAppProperties.getRootDir();
			
			// Runtime.getRuntime().exec("test.sh");
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

	public File getDiscoveryCsv() {
		return discoveryCsv;
	}

	public void setDiscoveryCsv(File discoveryCsv) {
		this.discoveryCsv = discoveryCsv;
	}

	public String getDiscoveryCsvContentType() {
		return discoveryCsvContentType;
	}

	public void setDiscoveryCsvContentType(String discoveryCsvContentType) {
		this.discoveryCsvContentType = discoveryCsvContentType;
	}

	public String getDiscoveryCsvFileName() {
		return discoveryCsvFileName;
	}

	public void setDiscoveryCsvFileName(String discoveryCsvFileName) {
		this.discoveryCsvFileName = discoveryCsvFileName;
	}

	public File getDiscoveryDb() {
		return discoveryDb;
	}

	public void setDiscoveryDb(File discoveryDb) {
		this.discoveryDb = discoveryDb;
	}

	public String getDiscoverDbContentType() {
		return discoverDbContentType;
	}

	public void setDiscoverDbContentType(String discoverDbContentType) {
		this.discoverDbContentType = discoverDbContentType;
	}

	public String getDicoveryDbFileName() {
		return dicoveryDbFileName;
	}

	public void setDicoveryDbFileName(String dicoveryDbFileName) {
		this.dicoveryDbFileName = dicoveryDbFileName;
	}

	public String getBaseDir() {
		return baseDir;
	}	

}
