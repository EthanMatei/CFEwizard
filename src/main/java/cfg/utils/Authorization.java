package cfg.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Class for checking if a user has required authorization.
 * 
 * @author Jim Mullen
 *
 */
public class Authorization {

	private static Log log = LogFactory.getLog( Authorization.class );
	
	/**
	 * Checks the session to see if the user associated with it is logged in as a (non-admin) user.
	 * 
	 * @param session
	 * 
	 * @return
	 */
	public static boolean isUser(Map<String,Object> session) {
		boolean isUser = false;
		
		if (session != null) {
            Object usernameObject = session.get("username");
            String username;
            
            if (usernameObject != null && usernameObject instanceof String) {
        	    username = (String) usernameObject;
        	    if (username != null && username.equals( WebAppProperties.getUserUsername() ) ) {
        	        isUser = true;
        	    }
            }	
		}
		
	    return isUser;
	}
	
	/**
	 * Checks session to see if the user associated with it is logged in as an admin.
	 * 
	 * @param session
	 * 
	 * @return
	 */
	public static boolean isAdmin(Map<String,Object> session) {
		boolean isAdmin = false;
		
		if (session != null) {
            Object usernameObject = session.get("username");
            String username;
            
            if (usernameObject != null && usernameObject instanceof String) {
        	    username = (String) usernameObject;
        	    if (username != null && username.equals( WebAppProperties.getAdminUsername() ) ) {
        	        isAdmin = true;
        	    }
            }	
		}
		
		return isAdmin;
	}
	
	public static boolean isLoggedIn(Map<String,Object> session) {
		boolean isLoggedIn = false;
		
		if (session != null) {
            Object usernameObject = session.get("username");
            String username;
            
            if (usernameObject != null && usernameObject instanceof String) {
        	    username = (String) usernameObject;
        	    if (username != null) {
        	    	if (username.equals( WebAppProperties.getAdminUsername() ) ) {
        	            isLoggedIn = true;
        	    	}
        	    	else if (username.equals( WebAppProperties.getUserUsername() ) ) {
        	    		isLoggedIn = true;
        	    	}
        	    }
            }	
		}
		
		return isLoggedIn;
	}
	
}