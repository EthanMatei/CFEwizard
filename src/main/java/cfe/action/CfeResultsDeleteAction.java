package cfe.action;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.struts2.action.SessionAware;

import cfe.model.CfeResults;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;

/**
 * Action for deleting previously calculated discovery results.
 * 
 * @author Jim Mullen
 *
 */
public class CfeResultsDeleteAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(CfeResultsDeleteAction.class.getName());

	private Map<String, Object> webSession;
	
	private String errorMessage;

	private Long cfeResultsId;

    private List<CfeResults> cfeResults;
    
	public String execute() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else {
		    try {
		        CfeResultsService.deleteById(cfeResultsId);
		        this.cfeResults = CfeResultsService.getAllMetadata();
		    } catch (Exception exception) {
		        this.errorMessage = "The Discovery database could not be processed. " + exception.getLocalizedMessage();
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

    public List<CfeResults> getCfeResults() {
        return cfeResults;
    }

    public void setCfeResults(List<CfeResults> cfeResults) {
        this.cfeResults = cfeResults;
    }

    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

}
