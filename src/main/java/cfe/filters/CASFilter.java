package cfe.filters;

import java.io.*;
import java.net.URL;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.inresearch.util.InrsrchUtil;

/**
 * See http://kb.iu.edu/data/atfc.html for information on CAS at IU.
 * 
 * @author mullen2
 * @author daltenho
 */
public class CASFilter implements Filter {
	
	private static Log log = LogFactory.getLog(CASFilter.class);
	
	// Production:
	public static final String CAS_LOGIN_URL  =  "https://cas.iu.edu/cas/login";
	public static final String VALIDATION_URL =  "https://cas.iu.edu/cas/validate";
	
	// Test:
	//public static final String CAS_LOGIN_URL  =  "https://cas-test.iu.edu/cas/login";
	//public static final String VALIDATION_URL =  "https://cas-test.iu.edu/cas/validate";

	//public static final String YOUR_SITE = "http://in-143-175.dhcp-149-166.iupui.edu:8080/ngvb";
	

	private FilterConfig filterConfig = null;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
						throws IOException, ServletException {
		
		// Figure out what our domain is here based on which server we're on
		// Temporarily set to development:
		String serverInstance = filterConfig.getServletContext().getInitParameter("serverInstance");
		log.debug("The serverInstance is: " + serverInstance);
		
		//String myUrl;
		
		// Determine where to redirect:
		/*
		if(serverInstance.equals("local") )
			myUrl = InrsrchUtil.SERVER_LOCAL_URL;
		else if(serverInstance.equals("dev"))
			myUrl = InrsrchUtil.SERVER_DEV_URL;
		else if(serverInstance.equals("rel"))
			myUrl = InrsrchUtil.SERVER_REL_URL;
		else 
			myUrl = InrsrchUtil.SERVER_PROD_URL;
		*/
		
		//String casUrl = "http://in-uits-800112:8080/mCFG"; //myUrl + "/ctsiadmin/home";
		String casUrl = "http://localhost:8080/mCFG";    // JGM
		
		//String test = java.net.InetAddress.getByName( request.getServerName() ).toString();
		//log.info("InetAddress: " + test);
		//log.info("ServerName: " + request.getServerName() );
		//log.info("LocalAddr: " + request.getLocalAddr() );
		//log.info("Protocol: " + request.getProtocol() );
		//log.info("TEST2: " + java.net.InetAddress.getByName( request.getServerName() ).getHostAddress() );
		
		//String rootDir = filterConfig.getServletContext().getRealPath("/");
		//log.debug("rootDir: " + rootDir);
		
		//String validationURL = filterConfig.getInitParameter("CASValidationURL");
		
		HttpServletRequest  httpRequest  = (HttpServletRequest) request; 
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		HttpSession session = httpRequest.getSession(true);  // true => create a session if it
		                                                     //         doesn't already exist
		
		// Are they already authenticated?
		String casUsername = (String) session.getAttribute("casname");//InrsrchUtil.SESVAR_CAS_USERNAME);
		
		// If the user has not authenticated
		if (casUsername == null) {
			
			log.debug("User not authenticated");

	        String casticket = request.getParameter("casticket");
            
	        String casLoginString = CAS_LOGIN_URL 
            + "?cassvc=IU"
            + "&casurl=" + casUrl;
            
			// if there is no ticket, redirect to CAS login
	        if (casticket == null || casticket.trim().equals("")) {
	        	log.debug("No cas ticket ... redirecting");
	            ((HttpServletResponse) response).sendRedirect(casLoginString);	        	
	        }
	        else {
	        	log.debug("Ticket present ... validating");
	        	// else there is a ticket, so check it
	        	String validationString = VALIDATION_URL + "?cassvc=IU" + "&casticket=" + casticket  + "&casurl=" + casUrl;
	        	log.debug("validation string: " + validationString);
	        	
	        	URL u = new URL(validationString);
	        	BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
	        	
	        	// If the attempt fails, return null immediately
	        	/*
	        	if (in == null)
	        	{
	        		log.error("Attempt to validate CAS ticket failed. null was returned");
    	            //((HttpServletResponse) response).sendRedirect(casLoginString);
	        		throw new RuntimeException("Failed attempt to validate CAS ticket!");
	        	}
	        	else
	        	{
	        	*/
	        		// Read the first line of the response, hopefully it says 'yes'
	        		String line1 = in.readLine();

	        		if (line1.equals("no")) {
	        			log.warn("Invalid CAS ticket");
	                    throw new RuntimeException("casticket '" + casticket + "' is not valid");
	        		}
	        		else {
	        			// Read the second line of the response, hopefully it contains a user ID
		        		String line2 = in.readLine();
	        			String username = line2;
	        			log.info("Valid login ... setting casUsername session attribute to: " + username);
	        			
	        			//  Invalidate session on production to prevent session fixation attacks:
	        			//if(serverInstance.equals("prod")) {
	        				httpRequest.getSession().invalidate();
		        			HttpSession newSession = httpRequest.getSession(true);
		        			newSession.setAttribute("casname", username);
	        			//}
	        			//else {
	        			//	httpRequest.getSession().setAttribute("casname", username);
	        			//}
	        			
	        		}
	        	//}
	        }
		}

		
		chain.doFilter(request, httpResponse);
	}

	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
	
	public void destroy() {
		this.filterConfig = null;
	}
	
}