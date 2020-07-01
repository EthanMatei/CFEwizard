package cfe.utils;

import java.util.HashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;


// http://stackoverflow.com/questions/8621906/is-buildsessionfactory-deprecated-in-hibernate-4 
// http://stackoverflow.com/questions/8640619/hibernate-serviceregistrybuilder
public class HibernateUtils {
	public static int BATCH_SIZE = 200;
	
	public static String BATCH_SIZE_STR =  "200";
	
	private static String password = null;
	
	private static SessionFactory sessionFactory = buildSessionFactory();
	
	private static boolean createSchema = false;
	
	// This has no purpose whatsoever, except verify that 
	// we didn't forget to add a model to the model list
	private static int numModels = 0;
		
	private static SessionFactory buildSessionFactory() {
		
		try {
			
			WebAppProperties.initialize(".");
			
			Map<String,String> settings = new HashMap<String,String>();
			
			// http://docs.jboss.org/hibernate/orm/3.6/reference/en-US/html/session-configuration.html
			settings.put("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
			
			// Read in from properties file:
			settings.put("hibernate.connection.url", WebAppProperties.getProperty(WebAppProperties.DB_HOST));
			
			if (createSchema == true){
				if (password == null) throw new Exception("root password is null");
				
				settings.put("hibernate.connection.username", "root");
				settings.put("hibernate.connection.password", password);

			} else {
				settings.put("hibernate.connection.username", WebAppProperties.getProperty(WebAppProperties.DB_USERNAME));
				settings.put("hibernate.connection.password", WebAppProperties.getProperty(WebAppProperties.DB_PASSWORD));
			}			
			settings.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
			
			settings.put("hibernate.connection.provider_class", "org.hibernate.service.jdbc.connections.internal.C3P0ConnectionProvider");
			settings.put("hibernate.c3p0.min_size", "5");
			settings.put("hibernate.c3p0.max_size", "20"); 
			settings.put("hibernate.c3p0.timeout", "60");
			settings.put("hibernate.c3p0.max_statements", "50");
			settings.put("hibernate.c3p0.idle_test_period", "3000");
			
			// Used by AbstractDao
			settings.put("hibernate.jdbc.batch_size", BATCH_SIZE_STR);
			
			settings.put("hibernate.current_session_context_class", "thread");
			settings.put("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
			
			// Disable when not testing
			settings.put("hibernate.show_sql", "false");
			settings.put("hibernate.use_sql_comments", "false");
			
			settings.put("hibernate.hbm2ddl.auto", WebAppProperties.getProperty(WebAppProperties.DB_HBM2DDL));
			//log.info("hbm2ddl set to: " + WebAppProperties.getProperty(WebAppProperties.DB_HBM2DDL));
						
			
			ServiceRegistryBuilder srb = new ServiceRegistryBuilder();
						
			srb.applySettings(settings);
			
			ServiceRegistry sr = srb.buildServiceRegistry();
						
			Configuration conf = new Configuration();
			
			Class<?>[] models = {
					
					cfe.model.DatabaseUploadInfo.class,
					
					cfe.model.Discovery.class
					};
			for( Class<?> model: models)
				conf.addAnnotatedClass(model);
			
			// Check to make sure we didn't forget to include a model
			numModels = models.length;
					
			conf.addPackage("cfe.model");
															
			sessionFactory = conf.buildSessionFactory(sr);
									
			// create the table in the database
			// http://docs.jboss.org/hibernate/orm/4.2/javadocs/org/hibernate/tool/hbm2ddl/SchemaExport.html
			// http://nikojava.wordpress.com/2008/05/05/hibernate-java-10-minutes-en/
			// http://docs.jboss.org/hibernate/orm/4.1/devguide/en-US/html_single/#d5e339
			/**
			 * We just run this once. This needs to be moved to a script
			 */
			if (createSchema){
				
				org.hibernate.tool.hbm2ddl.SchemaExport se = new org.hibernate.tool.hbm2ddl.SchemaExport(sr, conf);
			
				se.setOutputFile("sql/createTables.sql");
				se.setDelimiter(";");
				
				se.create(true, true);
				

			}
			
			return sessionFactory;			 
			
		} catch (Throwable ex) {
			
			System.err.println("Initial SessionFactory creation failed. " + ex); 
			throw new ExceptionInInitializerError(ex);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static Session getSession() throws HibernateException {

		return getSessionFactory().openSession();

	}

	public static boolean isCreateSchema() {
		return createSchema;
	}

	public static void setCreateSchema(boolean createSchema) {
		HibernateUtils.createSchema = createSchema;
		// Rebuild the session
		sessionFactory.close();
		sessionFactory = buildSessionFactory();
	}

	public static void setPassword(String password) {
		HibernateUtils.password = password;
	}

	public static int getNumModels() {
		return numModels;
	}
}

