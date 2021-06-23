package cfe.action.prioritization;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.action.BaseAction;

import cfe.enums.prioritization.Scores;
import cfe.model.prioritization.Disorder;
import cfe.model.prioritization.disease.DiseaseSelector;
import cfe.services.prioritization.DisorderService;
import cfe.services.ServiceException;
import cfe.utils.Authorization;

import com.opensymphony.xwork2.ModelDriven;

// http://stackoverflow.com/questions/3044447/iterating-over-hashmap-in-jsp-in-struts-application
public class DiseaseSelectionAction extends BaseAction implements ModelDriven<List<DiseaseSelector>>, SessionAware {
	

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(DiseaseSelectionAction.class);
	
	private Map<String, Object> session;
	
	private List<DiseaseSelector> diseaseSelectors = new ArrayList<DiseaseSelector>();
	private String score;
	private boolean otherCompleted;
	
	
	private List<Disorder> disorders;
	
	public List<DiseaseSelector> getModel() {
		return diseaseSelectors;
	}

	public String initialize() {
		String result = SUCCESS;

		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		else {
			try {
				this.diseaseSelectors = DisorderService.getDiseaseSelectors();
			}
			catch (ServiceException exception) {
				result = ERROR;
			}

			score = Scores.OTHER.getLabel();
			otherCompleted = true;
		}
		
		return result;
	}
	
	/**
	 * Processes disease selection form submit.
	 */
	public String execute() throws Exception {

		String result = SUCCESS;
		
		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		else {
		    //JGM FIX!!!!!!!!!
		    // Need a check for the case of no disorders selected (return INPUT)
		    // And for negative coefficients
	        session.put("diseaseSelectors",  diseaseSelectors);
		}
		
		return result;
	}

	
	//-----------------------------------------------------------------
	// Getters and Setters
	//-----------------------------------------------------------------
	public List<DiseaseSelector> getDiseaseSelectors() {
		return diseaseSelectors;
	}

	public void setDiseaseSelectors(List<DiseaseSelector> diseaseSelectors) {
		this.diseaseSelectors = diseaseSelectors;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public boolean isOtherCompleted() {
		return otherCompleted;
	}

	public void setOtherCompleted(boolean otherCompleted) {
		this.otherCompleted = otherCompleted;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
		
	}

	public List<Disorder> getDisorders() {
		return disorders;
	}

	public void setDisorders(List<Disorder> disorders) {
		this.disorders = disorders;
	}

}
