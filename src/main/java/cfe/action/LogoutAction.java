package cfe.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.action.SessionAware;
import org.apache.struts2.dispatcher.SessionMap;


import com.opensymphony.xwork2.ActionContext;

/**
 * Struts2 action class for logout.
 * 
 * @author Jim Mullen
 *
 */
public class LogoutAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(LogoutAction.class);
	
	private Map<String, Object> session;
	
	
	@Override
	public void withSession(Map<String, Object> session) {
		this.session = session;
	}
	
		
	public String execute() {

	    if (session != null) {
	    	Object usernameObject = session.get("username");
	    	if (usernameObject != null && usernameObject instanceof String) {
	    		String username = (String) usernameObject;
	    		log.info("user \"" + username + "\" logged out.");
	    	}
	    	
	    	// Remove the username, just in case session invalidation fails
	    	session.remove("username");
	    	
	    	// Invalidate the session
			((SessionMap)this.session).invalidate();
			this.session = ActionContext.getContext().getSession();
			
	    	if (session instanceof org.apache.struts2.dispatcher.SessionMap) {
	    	    try {
	    	    	((org.apache.struts2.dispatcher.SessionMap) session).invalidate();
	    	    }
	    	    catch (IllegalStateException exception) {
	    	        log.error(exception.getMessage());
	    	    }
	    	}

	    }
		return SUCCESS;
	}

}
