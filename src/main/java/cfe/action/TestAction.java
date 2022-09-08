package cfe.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.model.PercentileScore;
import cfe.model.PercentileScores;
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
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
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

}
