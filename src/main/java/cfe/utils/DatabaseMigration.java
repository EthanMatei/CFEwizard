package cfe.utils;


import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.flywaydb.core.Flyway;

import cfe.listeners.AppInitContextListener;
import cfe.utils.WebAppProperties;

/**
 * Class for performing database migrations (database schema changes). This class uses Flyway
 * to do the actual migrations.
 *  
 * @author Jim Mullen
 * 
 * @see <a href="http://flywaydb.org/">http://flywaydb.org/</a>
 *
 */
public class DatabaseMigration {
	
	private static Log log = LogFactory.getLog(DatabaseMigration.class);

	/**
	 * Perform any needed database migrations (database schema updates) using Flyway.
	 */
	public static void migrate() {
		log.info("Starting database migration.");

	    String dbHost     = WebAppProperties.getProperty("db.host");
		String dbUsername = WebAppProperties.getProperty("db.username");
		String dbPassword = WebAppProperties.getProperty("db.password");

		log.info("Database Host: " + dbHost);
		log.info("Database Username: " + dbUsername);
		
		try {
	        Flyway flyway = Flyway.configure()
	        	.dataSource(dbHost, dbUsername, dbPassword).load();
	        
		    log.info("Flyway locations: " + Arrays.toString( flyway.getConfiguration().getLocations()) );

	        // Start the migration
	        flyway.migrate();
	        /*
		    Flyway flyway = new Flyway();

		    flyway.setValidateOnMigrate( false ); // set to false because it can generate false negative
	        flyway.setBaselineOnMigrate(true);
	        flyway.setBaselineDescription("Baseline version");
	        flyway.setBaselineVersionAsString("1.0");
		    
		    flyway.setDataSource(dbHost, dbUsername, dbPassword);
		    flyway.migrate();
		    
		    try {
		    	flyway.validate();
		    }
		    catch (Exception exception) {
		    	log.warn("Database migration validation error: " + exception.getMessage());
		    }
		    
		    
		    */
		}
		catch (Exception exception) {
			log.error("Database migration failed: " + exception.getLocalizedMessage());
			System.exit(1);
		}
	}


}
