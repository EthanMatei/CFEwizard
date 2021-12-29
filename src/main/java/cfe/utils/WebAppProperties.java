package cfe.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Global web application properties class.  This class is intended for
 * properties that you do not want to store in the repository and/or
 * for which you would NOT want to retrieve a previous value if
 * you have to roll back to a previous release.  For example, a password
 * might fit both of these criteria.  For security reasons, you would not
 * want to store a password in the repository, and if you have to roll
 * back to a previous release, you want the current password, not an
 * old (possibly incorrect) one.
 * 
 * @author Jim Mullen
 *
 */
public class WebAppProperties {

	private static String propertiesFileName;
	
	// Hibernate
	public static final String DB_USERNAME = "db.username";
	public static final String DB_PASSWORD = "db.password";
	public static final String DB_HOST     = "db.host";
	public static final String DB_HBM2DDL  = "db.hbm2ddl";
	
	public static final String HOST_CAS_URL = "host.cas.url";
	
	private static final String SECURE_COOKIES_PROPERTY = "secure.cookies";
	
	private static final String USER_USERNAME_PROPERTY = "user.username";
	private static final String USER_PASSWORD_PROPERTY = "user.password";

	private static final String ADMIN_USERNAME_PROPERTY = "admin.username";
	private static final String ADMIN_PASSWORD_PROPERTY = "admin.password";
	
	public static final String RSCRIPT_PATH_PROPERTY = "rscript.path";
	private static final String DEFAULT_RSCRIPT_PATH  = "/usr/bin/Rscript";
	   
    public static final String PYTHON3_PATH_PROPERTY = "python3.path";
    private static final String DEFAULT_PYTHON3_PATH  = "/usr/bin/python3";
    
    public static final String TEMP_DIR = "temp.dir";
    
	private static Log log = LogFactory.getLog(WebAppProperties.class);
	
	private static Properties properties = new Properties();

	public static String getProperty(String name) {
		return properties.getProperty(name);
	}
	
	private static String rootDir;
	
	/**
	 * Loads web application properties from a file
	 */
	public static void initialize(String rootDir) {
		WebAppProperties.rootDir = rootDir;
		
		// Properties files are in user's home directory:
		propertiesFileName = System.getProperty("user.home", ".") + System.getProperty("file.separator");
		
		propertiesFileName += ".cfe";

		propertiesFileName += ".properties";
		
		log.info("Properties file name: " + propertiesFileName);
		
	    File file = new File(propertiesFileName);
	    
	    try {
		    BufferedInputStream inputStream = new BufferedInputStream( new FileInputStream(file) );
		    properties.load( inputStream );
	    }
	    catch (FileNotFoundException exception){
	    	// This may be OK if it is a local development copy
	        log.warn("Web application properties file \"" + propertiesFileName + "\" not found.");    
	    }
	    catch (IOException exception) {
	    	// This may be OK if it is a local development copy
	        log.warn("Web application properties file \"" + propertiesFileName + "\" not found.");   
	    }
	}
	
	public static String getSecureCookies() {
		return properties.getProperty( SECURE_COOKIES_PROPERTY );
	}
	
	public static String getUserUsername() {
		return properties.getProperty( USER_USERNAME_PROPERTY );
	}
	
	public static String getUserPassword() {
		return properties.getProperty( USER_PASSWORD_PROPERTY );
	}
	
	public static String getAdminUsername() {
		return properties.getProperty( ADMIN_USERNAME_PROPERTY );
	}
	
	public static String getAdminPassword() {
		return properties.getProperty( ADMIN_PASSWORD_PROPERTY );
	}
	
	public static String getRscriptPath() {
		return properties.getProperty( RSCRIPT_PATH_PROPERTY, DEFAULT_RSCRIPT_PATH );
	}
    
    public static String getPython3Path() {
        return properties.getProperty( PYTHON3_PATH_PROPERTY, DEFAULT_PYTHON3_PATH );
    }
    
	public static String getDbHost() {
		return properties.getProperty( DB_HOST );
	}
	
	public static String getDbUsername() {
		return properties.getProperty( DB_USERNAME );
	}
	
	public static String getRootDir() {
		return WebAppProperties.rootDir;
	}
	
	public static String getTempDir() {
	    String defaultTempDir = properties.getProperty("java.io.tmpdir", "/tmp");
	    String tempDir = properties.getProperty( TEMP_DIR, defaultTempDir );
	    return tempDir;
	}

}
