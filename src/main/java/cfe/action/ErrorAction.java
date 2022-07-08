package cfe.action;

import com.opensymphony.xwork2.ActionSupport;


/**
 * Struts2 action class for general errors.
 * 
 * @author Jim Mullen
 */
public class ErrorAction extends ActionSupport {

	private static final long serialVersionUID = 1L;

    public String execute() {
    	String result = SUCCESS;

        return result;
    }

}