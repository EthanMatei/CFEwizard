package cfe.action;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.ServletActionContext;


import cfe.action.ActionErrorException;
import cfe.utils.Authorization;
import cfe.utils.Filter;

/**
 * Struts2 action for displaying script results.
 * 
 * @author Jim Mullen
 *
 */
public class ScriptResultsDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(ScriptResultsDisplayAction.class);
	

    @SuppressWarnings("unchecked")
    private Map session;
    
    private String scriptCommand;
    private String scriptOutput;
	
    public ScriptResultsDisplayAction() {
        this.scriptCommand = "";
        this.scriptOutput  = "";
    }
    
    @SuppressWarnings("unchecked")
    public void setSession(Map session) {
    	this.session = session;
    }    
	
    public String view() throws Exception {
        String result = SUCCESS;

        if (!Authorization.isLoggedIn(session)) {
            result = LOGIN;
        }
        else {
            if (this.scriptCommand == null) {
                this.scriptCommand = "";
            }
            
            if (this.scriptOutput == null) {
                this.scriptOutput = "";
            }
        }

        return result;
    }

    public String getScriptCommand() {
        return scriptCommand;
    }

    public void setScriptCommand(String scriptCommand) {
        this.scriptCommand = scriptCommand;
    }

    public String getScriptOutput() {
        return scriptOutput;
    }

    public void setScriptOutput(String scriptOutput) {
        this.scriptOutput = scriptOutput;
    }
    
    
}
