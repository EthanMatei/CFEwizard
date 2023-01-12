package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;
import com.opencsv.CSVReader;

import cfe.model.CfeResults;
import cfe.model.CfeResultsFile;
import cfe.model.CfeResultsFileType;
import cfe.model.CfeResultsNewestFirstComparator;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.PercentileScores;
import cfe.model.VersionNumber;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.parser.ProbesetMappingParser;
import cfe.services.CfeResultsFileService;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.CohortTable;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.Util;
import cfe.utils.WebAppProperties;

public class TestingDbCheckAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	
    public static final String PHENE_VISIT_PATTERN = "^phchp\\d+v\\d+$|^CTBIN\\d+v\\d+$";
    
	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(TestingDbCheckAction.class.getName());

	private Map<String, Object> webSession;
	    
	private File testingDb;
	private String testingDbContentType;
	private String testingDbFileName;
	
	private String report;
	
	Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
	
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    return result;
	}
	
	public String uploadAndCheckDatabase() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else if (this.testingDb == null || this.testingDbFileName == null) {
		    this.setErrorMessage("No testing database was specified.");
		    result = ERROR;
		}
		else if (!this.testingDbFileName.endsWith(".accdb")) {
		    this.setErrorMessage("Testing database file \"" + testingDbFileName
		            + "\" does not have expected MS Access database file extension \".accdb\".");
		    result = ERROR;
		}
		else {
		    try {
		        log.info("Database \"" + this.testingDbFileName + "\" uploaded.");

		        String testingDbFilePath = testingDb.getAbsolutePath();
		        
			    DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(testingDbFilePath);
			
	            //dbParser.checkCoreTables();
	         
			    Set<String> pheneTables = dbParser.getPheneTables();

			    this.phenes = dbParser.getTableColumnMap();
			    
			    StringWriter sout = new StringWriter();
			    PrintWriter out = new PrintWriter(sout);
			    
	            for (String pheneTable: pheneTables) {
	                out.println("TABLE: \"" + pheneTable + "\"");
	                Set<String> columns = dbParser.getTableColumnNames(pheneTable);
	                out.println("    COLUMNS: " + String.join(", ", columns));
	                
	                Table dbTable = dbParser.getTable(pheneTable);
	                DataTable dataTable = new DataTable();
	                dataTable.initializeToAccessTable(dbTable);
	                
	                if (!dataTable.hasColumn("PheneVisit")) {
	                    out.println(    "WARNING: Table \"" + pheneTable +"\" has no \"PheneVisit\" column.");
	                }
	                else {                    
	                    Set<String> pheneVisits = new HashSet<String>();
	                    for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
	                        String pheneVisit = dataTable.getValue(i, "PheneVisit");
	                        if (pheneVisits.contains(pheneVisit)) {
	                            out.println("    ERROR: duplicate phene visit \"" + pheneVisit + "\" on line " + (i+1) + ".");
	                        }
	                        else if (pheneVisit.matches(PHENE_VISIT_PATTERN)) {
	                            pheneVisits.add(pheneVisit);
	                        }
	                        else {
	                            out.println("    ERROR: phene visit \"" + pheneVisit + "\" on line " + (i+1) + " has an incorrect format.");
	                        }
	                           
	                    }
	                }
	                out.println();
	            }
	            
	            report = sout.toString();
	            sout.close();
	            out.close();
		    } catch (Exception exception) {
		        String message = "The Discovery database \"" + this.testingDbFileName + "\" could not be processed: " + exception.getLocalizedMessage();
		        log.severe(message);
		        this.setErrorMessage(message);
		        result = ERROR;
		    }
		}
	    return result;
	}
	

	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public void validate() {
	}

	public Map<String, Object> getSession() {
		return webSession;
	}

	public Map<String, ArrayList<ColumnInfo>> getPhenes() {
		return phenes;
	}

	public void setPhenes(Map<String, ArrayList<ColumnInfo>> phenes) {
		this.phenes = phenes;
	}

    public File getTestingDb() {
        return testingDb;
    }

    public void setTestingDb(File testingDb) {
        this.testingDb = testingDb;
    }

    public String getTestingDbContentType() {
        return testingDbContentType;
    }

    public void setTestingDbContentType(String testingDbContentType) {
        this.testingDbContentType = testingDbContentType;
    }

    public String getTestingDbFileName() {
        return testingDbFileName;
    }

    public void setTestingDbFileName(String testingDbFileName) {
        this.testingDbFileName = testingDbFileName;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

}
