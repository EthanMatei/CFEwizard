package cfe.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.struts2.action.SessionAware;

import com.healthmarketscience.jackcess.Table;

import cfe.parser.DiscoveryDatabaseParser;
import cfe.utils.Authorization;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.TableCheckInfo;

public class TestingDbCheckAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
    
	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(TestingDbCheckAction.class.getName());

	private Map<String, Object> webSession;
	    
	private File testingDb;
	private String testingDbContentType;
	private String testingDbFileName;
	
	private List<TableCheckInfo> tableCheckInfos;
	
	private String report;
	
    private Date generatedTime;
	
	Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
	
	public TestingDbCheckAction() {
	    this.setCurrentTab("Other Functions");
	    this.setCurrentSubTab("Phenomic Database Check");
	    
	    this.tableCheckInfos = new ArrayList<TableCheckInfo>();
	}
	
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    return result;
	}
	
	public String uploadAndCheckDatabase() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else if (this.testingDb == null || this.testingDbFileName == null) {
		    this.setErrorMessage("No testing database was specified.");
		    result = ERROR;
		}
		else if (!this.testingDbFileName.endsWith(".accdb")) {
		    this.setErrorMessage("Testing database file \"" + testingDbFileName
		            + "\" does not have expected MS Access database file extension \".accdb\".");
		    result = ERROR;
		}
		else {
		    try {
		        log.info("Database \"" + this.testingDbFileName + "\" uploaded.");

		        String testingDbFilePath = testingDb.getAbsolutePath();
		        
		        this.tableCheckInfos = TableCheckInfo.checkTestingDatabase(testingDbFilePath);
		        this.generatedTime = new Date();
		    } catch (Exception exception) {
		        String message = "The Discovery database \"" + this.testingDbFileName + "\" could not be processed: " + exception.getLocalizedMessage();
		        log.severe(message);
		        this.setErrorMessage(message);
		        result = ERROR;
		    }
		}
	    return result;
	}
	

	public void withSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public void validate() {
	}

	public Map<String, Object> getSession() {
		return webSession;
	}

	public Map<String, ArrayList<ColumnInfo>> getPhenes() {
		return phenes;
	}

	public void setPhenes(Map<String, ArrayList<ColumnInfo>> phenes) {
		this.phenes = phenes;
	}

    public File getTestingDb() {
        return testingDb;
    }

    public void setTestingDb(File testingDb) {
        this.testingDb = testingDb;
    }

    public String getTestingDbContentType() {
        return testingDbContentType;
    }

    public void setTestingDbContentType(String testingDbContentType) {
        this.testingDbContentType = testingDbContentType;
    }

    public String getTestingDbFileName() {
        return testingDbFileName;
    }

    public void setTestingDbFileName(String testingDbFileName) {
        this.testingDbFileName = testingDbFileName;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public List<TableCheckInfo> getTableCheckInfos() {
        return tableCheckInfos;
    }

    public void setTableCheckInfos(List<TableCheckInfo> tableCheckInfos) {
        this.tableCheckInfos = tableCheckInfos;
    }

    public Date getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Date generatedTime) {
        this.generatedTime = generatedTime;
    }

}
