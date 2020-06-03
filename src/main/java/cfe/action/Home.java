package cfe.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

import cfe.utils.Authorization;

/**
 * Struts2 action class for the Home page.
 * 
 * @author Michel Tavares
 * @author Jim Mullen
 *
 */
public class Home extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(Home.class);

	private Map<String, Object> session;
	
	@Override
	public void setSession(Map<String, Object> session) {
		this.session = session;
	}
	
	public String execute() throws Exception {
		String result = SUCCESS;
		if (!Authorization.isLoggedIn(session)) {
			result = LOGIN;
		}
		return result;
	}

}
