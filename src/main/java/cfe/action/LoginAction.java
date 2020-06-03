package cfe.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;

/**
 * Struts2 action class for login initialization.
 * 
 * @author Jim Mullen
 *
 */
public class LoginAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(LoginAction.class);
	
	private Map<String, Object> session;
	
	private String username;
	private String password;
	
	@Override
	public void setSession(Map<String, Object> session) {
			this.session = session;
	}
	
	public LoginAction() {
		this.setErrorMessage("");
	}
		
	public String execute() {
		return SUCCESS;
	}
	
	
	//---------------------------------------------------------------
	// Getters and Setters
	//---------------------------------------------------------------
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	
	

}
