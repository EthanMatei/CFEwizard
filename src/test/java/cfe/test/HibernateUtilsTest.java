package cfe.test;

import static org.junit.Assert.fail;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cfe.utils.HibernateUtils;


public class HibernateUtilsTest {
	
	private static Log log = LogFactory.getLog(HibernateUtilsTest.class);
	
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	//@Test
	public void testUpload() {
		
		SessionFactory session = null;
		
		Session sess = null;
				
		try {
			String propertiesFileName = "build.properties";
		    File file = new File(propertiesFileName);
		    Properties properties = new Properties();
		    
		    try {
			    BufferedInputStream inputStream = new BufferedInputStream( new FileInputStream(file) );
			    properties.load( inputStream );
		    }
		    catch (FileNotFoundException exception){
		    	// This may be OK if it is a local development copy
		        log.warn("Build properties file \"" + propertiesFileName + "\" not found.");    
		    }
		    catch (IOException exception) {
		    	// This may be OK if it is a local development copy
		        log.warn("Build properties file \"" + propertiesFileName + "\" not found.");   
		    }
		    
		    String dbPassword = properties.getProperty("mysqlAdminPassword");
		    
			HibernateUtils.setPassword( dbPassword );
	
			HibernateUtils.setCreateSchema(true);
			
			// Creates the schema
			session = HibernateUtils.getSessionFactory();
			
			// Did we forget to add models?
			// 2 to include GeneList and ScoringModel
			if (HibernateUtils.getNumModels() != cfe.enums.CfeTables.size + 4)
				throw new Exception("HibernateUtils.getNumModels() != cfe.enums.Tables.size");
			// Create index on genecardSymbol
			sess = HibernateUtils.getSession();
			
			HibernateUtils.setCreateSchema(false);
			
			for (cfe.enums.CfeTables tbl : cfe.enums.CfeTables.values())
				System.out.println("SELECT genecardSymbol, descriptiveName, pubMedID, psychiatricDomain, subDomain, relevantDisorder FROM " + tbl.getTblName() + " LIMIT 0, 3;");
			System.out.println("GRANT INSERT, DELETE, UPDATE, SELECT, EXECUTE ON cfe.* TO 'cfeUser'@'localhost';");
			System.out.println("SET lower_case_table_names=2;");
			System.out.println("ALTER TABLE cfe.scoringdata ADD INDEX indx1 (genecardSymbol)");
			
			// Let's create the stored procedures file
			String path = "./test-data";
			
			OutputStream outputStream = new FileOutputStream(new File(path +  "sp.txt")); 
			
			String sqlPath = "./src/main/sql";
			
			File folder = new File(sqlPath);	
			
			File[] files = folder.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(".sql");
			    }
			});
			byte[] buf = new byte[1024];
		    int len;
			for (File fileEntry : files) {
				
		        if (!fileEntry.isDirectory()) {
		        	
		        	 InputStream in = new FileInputStream(fileEntry);
		        	 
		        	 while ((len = in.read(buf)) > 0) {
		        		 outputStream.write(buf, 0, len);
				      }
		        	 
		        	 in.close();
		        	 in = null;
		        }
		    }
			
			outputStream.close();
			
            // Windows-dependent code
			//Runtime.getRuntime().exec(new String[]{"notepad",path +  "sp.txt"});		
			
		} catch (Exception e) {
			
			fail("Test error: " + e);	
			
		} finally {
			sess = null;
			if (!session.isClosed())
				session.close();
			session = null;
			// Give some time to release the session
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("Done");
		}		
	}

}
