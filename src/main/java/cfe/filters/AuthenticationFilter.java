package cfe.filters;
 
import java.io.IOException;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Authentication filter for making sure the user accessing the site has logged in successfully.
 * 
 * @author Jim Mullen
 *
 */
public class AuthenticationFilter implements Filter {
 
    private ServletContext context;
	private static final Log log = LogFactory.getLog(AuthenticationFilter.class);
	
    public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
    }
     
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
 
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        
        String uri = req.getRequestURI();
        
        String username = null;
        
        HttpSession session = req.getSession(false);
        
        if (session != null) {
            Object usernameObject = session.getAttribute("username");
        
           if (usernameObject != null && usernameObject instanceof String) {
        	    username = (String) usernameObject;
            }
        }
        
        // Eliminate JSESSIONID, or anything else added on to URL with a semi-colon
        uri = uri.replaceFirst(";.*", "");
        
        // eliminate anything after the first .action
        uri = uri.replaceFirst("\\.action.*",  ".action");
        
        if (uri.endsWith("LoginAction.action") || uri.endsWith("LoginProcess.action")
        		|| uri.endsWith(".css") || uri.endsWith(".gif") || uri.endsWith(".jpeg")) {
            chain.doFilter(request, response);
        }
        else if (username == null || username.trim().equals("")) {
        	
        	String redirectUrl = req.getContextPath() + "/" + "CFGLoginAction.action";
        	
        	log.info("access to " + uri + " rediredcted to " + redirectUrl + ".");
            res.sendRedirect( redirectUrl );
        }
        else {
            chain.doFilter(request, response);
        }

    }
 
     
 
    public void destroy() {
        //close any resources here
    }
 
}