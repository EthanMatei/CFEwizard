package cfe.listeners;

import java.util.Enumeration;
import java.util.logging.Logger;
import javax.servlet.*;

import cfe.utils.DatabaseMigration;
import cfe.utils.WebAppProperties;


/**
 * Class for handling web application initialization.  Initialization code
 * should go in the "contextInitialized" method.  Also allows non-servlet
 * class methods to retrieve application initialization parameters by
 * calling the "getInitParameter" method.
 * 
 * @author Jim Mullen
 * 
 */
public class AppInitContextListener implements ServletContextListener {
	
	private static String rootDir;
	private static ServletContext context;
	
	private static Logger logger =
        Logger.getLogger(cfe.listeners.AppInitContextListener.class.getName());
	 
	public void contextInitialized(ServletContextEvent event) {
        context = event.getServletContext();
        
        rootDir = context.getRealPath("/");

        logger.info("ROOT DIRECTORY: " + rootDir);
        
        Enumeration names = context.getAttributeNames();
        while (names.hasMoreElements()) {
        	String name = (String) names.nextElement();
        	System.out.println("    " + name + ": " + context.getAttribute(name));
        }
        
        WebAppProperties.initialize(".");;
        
        DatabaseMigration.migrate();
        
        logger.info("web application initialization complete");
	}
	
	
	public void contextDestroyed(ServletContextEvent event)	{

	}
	
	/**
	 * Returns the value of a context param set in web.xml.
	 * 
	 * @param name
	 * @return
	 */
	public static String getInitParameter(String name) {
		String value = context.getInitParameter( name );
		return value;
	}
	
	public static ServletContext getServletContext() {
		return context;
	}
	
	public static String getRootDir() {
		return rootDir;
	}

 }