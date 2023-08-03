package cfe.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.action.SessionAware;

import cfe.model.PercentileScore;
import cfe.model.PercentileScores;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;

/**
 * Struts2 action class for the testing functionaity.
 * 
 * @author Jim Mullen
 *
 */
public class TestAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(TestAction.class);

	private Map<String, Object> session;
	
	private List<Double> data;
	
	private PercentileScores percentileScores;
	
	private List<PercentileScore> perScores;
	
	private List<String> phenes;
	private Date minimumGeneratedTime;
	
	@Override
	public void withSession(Map<String, Object> session) {
		this.session = session;
	}
    
	public TestAction() {
	    this.setCurrentTab("Admin");
	    this.setCurrentSubTab("Test Page");
	}
	
    public String initialize() throws Exception {
        String result = SUCCESS;
        if (!Authorization.isLoggedIn(session)) {
            result = LOGIN;
        }
        else {
            data = new ArrayList<Double>();
            data.add(0.33);
            data.add(0.50);
            
            this.percentileScores = new PercentileScores();
            this.perScores = PercentileScore.getDefaultPercentileScores();
            
            this.phenes = CfeResultsService.getPhenes();
            this.minimumGeneratedTime = CfeResultsService.getMinimumGeneratedTime();
        }
        return result;
    }
    
	public String process() throws Exception {
		String result = SUCCESS;
		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		return result;
	}

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public PercentileScores getPercentileScores() {
        return percentileScores;
    }

    public void setPercentileScores(PercentileScores percentileScores) {
        this.percentileScores = percentileScores;
    }

    public List<PercentileScore> getPerScores() {
        return perScores;
    }

    public void setPerScores(List<PercentileScore> perScores) {
        this.perScores = perScores;
    }

    public List<String> getPhenes() {
        return phenes;
    }

    public void setPhenes(List<String> phenes) {
        this.phenes = phenes;
    }

    public Date getMinimumGeneratedTime() {
        return minimumGeneratedTime;
    }

    public void setMinimumGeneratedTime(Date minimumGeneratedTime) {
        this.minimumGeneratedTime = minimumGeneratedTime;
    }

}
