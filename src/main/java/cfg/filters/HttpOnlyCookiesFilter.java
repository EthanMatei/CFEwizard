package cfg.filters;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import cfg.utils.WebAppProperties;

/**
 * Filter to set the JSESSIONID cookie
 * to being "http only", which
 * in most modern browsers makes it inaccessible 
 * to the client side (e.g., JavaScript); and to set cookies to
 * "secure" if specified (usually this would NOT be specified for
 * personal development environments). 
 */
public class HttpOnlyCookiesFilter implements Filter {


	private FilterConfig filterConfig;

	public void doFilter(ServletRequest request, ServletResponse response,
			             FilterChain chain)
	throws IOException,
	ServletException {
		
		HttpServletRequest  httpRequest  = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		
		String sessionid = httpRequest.getSession().getId();
		
		String secureCookies = WebAppProperties.getSecureCookies();
		if (secureCookies != null && secureCookies.trim().equalsIgnoreCase("false") ) {
			httpResponse.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; HttpOnly");
		}
		else {
		    httpResponse.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid + "; Secure; HttpOnly");
		}
		
        chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}
	
	public void destroy() {
		this.filterConfig = null;
	}
	
}