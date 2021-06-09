package cfe.action;

import com.opensymphony.xwork2.ActionSupport;

import cfe.utils.WebAppProperties;
/**
 * Nov 01, 2013 2:09:33 PM com.opensymphony.xwork2.interceptor.ParametersInterceptor warn
WARNING: Parameter [struts.token.name] is on the excludeParams list of patterns!
 * @author mtavares
 *
 */
public abstract class BaseAction extends ActionSupport {

	private static final long serialVersionUID = 6229924955503493034L;
	
	private String errorMessage;
	private String exceptionStack;
	
	public BaseAction() {
	    this.errorMessage = "";
	    this.exceptionStack = "";
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
    
    public String getExceptionStack() {
        return exceptionStack;
    }
    
    public void setExceptionStack(String exceptionStack) {
        this.exceptionStack = exceptionStack;
    }
    
	//public String getToken() {
	//	return token;
	//}

	//public void setToken(String token) {
	//	this.token = token;
	//}

    public String getAdminUser() {
		return WebAppProperties.getAdminUsername();
	}

	//private String token;

}
