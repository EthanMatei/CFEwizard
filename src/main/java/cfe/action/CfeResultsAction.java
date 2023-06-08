package cfe.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.struts2.interceptor.SessionAware;

import cfe.model.CfeResults;
import cfe.model.CfeResultsNewestFirstComparator;
import cfe.model.CfeResultsOldestFirstComparator;
import cfe.model.CfeResultsType;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;

/**
 * Action for viewing previously calculated discovery results.
 * 
 * @author Jim Mullen
 *
 */
public class CfeResultsAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CfeResultsAction.class.getName());

	private Map<String, Object> webSession;
	
    private List<String> fileNames;
	
	private String tempDir;
	
	private String errorMessage;

	private File[] files;

	private String cfeResultsType;
	private List<String> cfeResultsTypes;
	
	private String resultsOrder;
	private List<String> resultsOrders;
	
	private String resultsPhene;
	private List<String> resultsPhenes;

    private List<CfeResults> cfeResults;
    
    public CfeResultsAction() {
        cfeResults = new ArrayList<CfeResults>();
        
        this.cfeResultsTypes = new ArrayList<String>();
        this.cfeResultsTypes.add("ALL");
        this.cfeResultsType = "ALL";
        this.cfeResultsTypes.addAll( CfeResultsType.getTypes() );
        
        this.resultsOrder = "ascending";
        this.resultsOrders = new ArrayList<String>();
        this.resultsOrders.add("ascending");
        this.resultsOrders.add("descending");
        
        this.resultsPhene = "ALL";
        this.resultsPhenes = new ArrayList<String>();
        this.resultsPhenes.add("ALL");
        this.resultsPhenes.addAll( CfeResultsService.getPhenes() );
        
        this.setCurrentTab("Saved Results");    
    }
    
	public String execute() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
		    try {

		        if (this.cfeResultsType == null || this.cfeResultsType.equals("ALL")) {
		            this.cfeResults = CfeResultsService.getAllMetadata();
		        }
		        else {
		            this.cfeResults = CfeResultsService.getMetadata(this.cfeResultsType);
		        }

		        // Filter by specified phene
                CfeResults.filterByPhene(cfeResults, this.resultsPhene);
                
                if (cfeResults.size() > 0) {
		            if (this.resultsOrder.equals("descending")) {
		                Collections.sort(this.cfeResults, new CfeResultsNewestFirstComparator());
		            }
		            else {
	                    Collections.sort(this.cfeResults, new CfeResultsOldestFirstComparator());
		            }
                }
		        
		        this.tempDir = System.getProperty("java.io.tmpdir");
		        
		        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
		        
		        File dir = new File(tempDir);
		        this.files = dir.listFiles((d, name) -> name.startsWith("discovery-results-"));
		        
		        this.fileNames = new ArrayList<String>();
		        for (File file: files) {
		            this.fileNames.add(file.getAbsolutePath());
		        }
		    } catch (Exception exception) {
		        this.errorMessage = "The specified CFE results could not be retrieved. " + exception.getLocalizedMessage();
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                this.setExceptionStack(stackTrace);
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

    public String getCfeResultsType() {
        return cfeResultsType;
    }

    public void setCfeResultsType(String cfeResultsType) {
        this.cfeResultsType = cfeResultsType;
    }

    public List<String> getCfeResultsTypes() {
        return cfeResultsTypes;
    }

    public void setCfeResultsTypes(List<String> cfeResultsTypes) {
        this.cfeResultsTypes = cfeResultsTypes;
    }

    public List<CfeResults> getCfeResults() {
        return cfeResults;
    }

    public void setCfeResults(List<CfeResults> cfeResults) {
        this.cfeResults = cfeResults;
    }

    public String getResultsOrder() {
        return resultsOrder;
    }

    public void setResultsOrder(String resultsOrder) {
        this.resultsOrder = resultsOrder;
    }

    public List<String> getResultsOrders() {
        return resultsOrders;
    }

    public void setResultsOrders(List<String> resultsOrders) {
        this.resultsOrders = resultsOrders;
    }

    public String getResultsPhene() {
        return resultsPhene;
    }

    public void setResultsPhene(String resultsPhene) {
        this.resultsPhene = resultsPhene;
    }

    public List<String> getResultsPhenes() {
        return resultsPhenes;
    }

    public void setResultsPhenes(List<String> resultsPhenes) {
        this.resultsPhenes = resultsPhenes;
    }

}
