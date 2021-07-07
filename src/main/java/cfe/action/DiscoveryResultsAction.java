package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import cfe.model.VersionNumber;
import cfe.model.discovery.DiscoveryResults;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.parser.PheneVisitParser;
import cfe.services.discovery.DiscoveryResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.CohortTable;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.WebAppProperties;

/**
 * Action for viewing previously calculated discovery results.
 * 
 * @author Jim Mullen
 *
 */
public class DiscoveryResultsAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(DiscoveryResultsAction.class);

	private Map<String, Object> webSession;
	
    private List<String> fileNames;
	
	private String tempDir;
	
	private String errorMessage;

	private File[] files;

    private List<DiscoveryResults> discoveryResults;
    
	public String execute() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
		    try {
		        this.discoveryResults = DiscoveryResultsService.getAllMetadata();
		        
		        this.tempDir = System.getProperty("java.io.tmpdir");
		        
		        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
		        
		        File dir = new File(tempDir);
		        this.files = dir.listFiles((d, name) -> name.startsWith("discovery-results-"));
		        
		        this.fileNames = new ArrayList<String>();
		        for (File file: files) {
		            this.fileNames.add(file.getAbsolutePath());
		        }
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

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }

    public List<DiscoveryResults> getDiscoveryResults() {
        return discoveryResults;
    }

    public void setDiscoveryResults(List<DiscoveryResults> discoveryResults) {
        this.discoveryResults = discoveryResults;
    }

}
