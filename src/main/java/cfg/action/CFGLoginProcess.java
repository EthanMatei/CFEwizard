package cfg.action;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.SessionAware;



import com.opensymphony.xwork2.ActionContext;

import cfg.utils.WebAppProperties;

/**
 * Struts2 action class for processing a login.
 * 
 * @author Jim Mullen
 *
 */
public class CFGLoginProcess extends BaseAction  implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CFGLoginProcess.class);
	
	private Map<String, Object> session;
	
	public String username;
	public String password;

	
	@Override
	public void setSession(Map<String, Object> session) {
			this.session = session;
	}
	
	
	public CFGLoginProcess() {
		this.setErrorMessage("");
	}

	public String execute() {
		String result = SUCCESS;
		
		// Invalidate the session at login
		((SessionMap)this.session).invalidate();
		this.session = ActionContext.getContext().getSession();

		if (username == null) username = "";
		else username = username.trim();
		
		if (password == null) password = "";
		else password = password.trim();
		
		if (username.equals("")) {
			this.setErrorMessage("No username specified.");
			result = ERROR;
		}
		else if (password.equals("")) {
			this.setErrorMessage("No password specified.");
			result = ERROR;
		}
		else if ( username.equals( WebAppProperties.getUserUsername() ) 
				&& password.equals( WebAppProperties.getUserPassword() ) ) {
			log.info("User \"" + username + "\" successfully logged in.");
			session.put("username",  username);
			result = SUCCESS;
		}
		else if ( username.equals( WebAppProperties.getAdminUsername() ) 
				&& password.equals( WebAppProperties.getAdminPassword() ) ) {
			log.info("User \"" + username + "\" successfully logged in.");
			session.put("username",  username);
			result = SUCCESS;
		}
		else {
			log.warn("User \"" + username + "\" falied to log in.");
			this.setErrorMessage("Invalid username and/or password.");
			result = ERROR;
		}
		
		return result;
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
