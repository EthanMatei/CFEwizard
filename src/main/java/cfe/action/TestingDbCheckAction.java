package cfe.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.struts2.action.SessionAware;

import com.healthmarketscience.jackcess.Table;

import cfe.parser.DiscoveryDatabaseParser;
import cfe.utils.Authorization;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.TableCheckInfo;

public class TestingDbCheckAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	
    public static final String PHENE_VISIT_PATTERN = "^phchp\\d+v\\d+$|^CTBIN\\d+v\\d+$";
    
	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(TestingDbCheckAction.class.getName());

	private Map<String, Object> webSession;
	    
	private File testingDb;
	private String testingDbContentType;
	private String testingDbFileName;
	
	private List<TableCheckInfo> tableCheckInfos;
	
	private String report;
	
	Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
	
	public TestingDbCheckAction() {
	    this.setCurrentTab("Special Functions");
	    this.setCurrentSubTab("Phenomic Database Check");
	    
	    this.tableCheckInfos = new ArrayList<TableCheckInfo>();
	}
	
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
                
                //StringWriter sout = new StringWriter();
                //PrintWriter out = new PrintWriter(sout);
			    
		        // Get the names of tables in the database
		        
			    Set<String> tableNames = new TreeSet<String>();         
		        tableNames = dbParser.getTableNames();
			    
			    Map<String, List<String>> coreTableMap = new LinkedHashMap<String, List<String>>();
			    coreTableMap.put(
			            DiscoveryDatabaseParser.DEMOGRAPHICS_TABLE,
			            Arrays.asList(DiscoveryDatabaseParser.DEMOGRAPHICS_REQUIRED_COLUMNS)
			    );
                coreTableMap.put(
                        DiscoveryDatabaseParser.DIAGNOSIS_TABLE,
                        Arrays.asList(DiscoveryDatabaseParser.DIAGNOSIS_REQUIRED_COLUMNS)
                );			    
                coreTableMap.put(
                        DiscoveryDatabaseParser.SUBJECT_IDENTIFIERS_TABLE,
                        Arrays.asList(DiscoveryDatabaseParser.SUBJECT_IDENTIFIERS_REQUIRED_COLUMNS)
                );  
                
                //----------------------------------------------------------------------
                // Check for required tables
                //----------------------------------------------------------------------
                for (Map.Entry<String, List<String>> entry : coreTableMap.entrySet()) {
                    TableCheckInfo tableCheckInfo = new TableCheckInfo();
                    
                    String tableName = entry.getKey();
			        // out.println("TABLE: \"" + tableName + "\"");
                    tableCheckInfo.setName(tableName);

			        if (!tableNames.contains(tableName)) {
			            // out.println("    ERROR: this required table does not exist in the database.");
	                    tableCheckInfo.addError("This required table does not exist in the database.");
			        }
			        else {
		                 Set<String> columns = dbParser.getTableColumnNames(tableName);
		                 // out.println("    COLUMNS: " + String.join(", ", columns));
		                 tableCheckInfo.setColumns(columns);
		                 
		                 List<String> requiredColumns = entry.getValue();
		                 for (String requiredColumn: requiredColumns) {
		                     if (!columns.contains(requiredColumn)) {
		                         // out.println("    ERROR: required column \"" + requiredColumn + "\" was not found in the table");
	                             tableCheckInfo.addError("Required column \"" + requiredColumn + "\" was not found in the table");
		                     }
		                 }
			        }
			        
			        tableCheckInfos.add(tableCheckInfo);
			        // out.println();
			    }
		          
                Set<String> pheneTables = dbParser.getPheneTables();
                
			    this.phenes = dbParser.getTableColumnMap();

			    //--------------------------------------------------------------
			    // Check phene tables
			    //--------------------------------------------------------------
	            for (String pheneTable: pheneTables) {
                    TableCheckInfo tableCheckInfo = new TableCheckInfo();
                    
                    tableCheckInfo.setName(pheneTable);
	                // out.println("TABLE: \"" + pheneTable + "\"");
                    
	                Set<String> columns = dbParser.getTableColumnNames(pheneTable);
	                // out.println("    COLUMNS: " + String.join(", ", columns));
	                tableCheckInfo.setColumns(columns);
	                
	                Table dbTable = dbParser.getTable(pheneTable);
	                DataTable dataTable = new DataTable();
	                dataTable.initializeToAccessTable(dbTable);
	                
	                if (!dataTable.hasColumn("PheneVisit")) {
	                    // out.println(    "WARNING: Table \"" + pheneTable +"\" has no \"PheneVisit\" column.");
	                    tableCheckInfo.addWarning("Table \"" + pheneTable +"\" has no \"PheneVisit\" column.");
	                }
	                else {                    
	                    Set<String> pheneVisits = new HashSet<String>();
	                    for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
	                        String pheneVisit = dataTable.getValue(i, "PheneVisit");
	                        if (pheneVisits.contains(pheneVisit)) {
	                            // out.println("    ERROR: duplicate phene visit \"" + pheneVisit + "\" on line " + (i+1) + ".");
	                            tableCheckInfo.addError("Duplicate phene visit \"" + pheneVisit + "\" on line " + (i+1) + ".");
	                        }
	                        else if (pheneVisit.matches(PHENE_VISIT_PATTERN)) {
	                            pheneVisits.add(pheneVisit);
	                        }
	                        else {
	                            // out.println("    ERROR: phene visit \"" + pheneVisit + "\" on line " + (i+1) + " has an incorrect format.");
	                            tableCheckInfo.addError("Phene visit \"" + pheneVisit + "\" on line " + (i+1) + " has an incorrect format.");
	                        }
	                           
	                    }
	                }
	                tableCheckInfos.add(tableCheckInfo);
	                //out.println();
	            }
	            
	            //report = sout.toString();
	            //sout.close();
	            //out.close();
		    } catch (Exception exception) {
		        String message = "The Discovery database \"" + this.testingDbFileName + "\" could not be processed: " + exception.getLocalizedMessage();
		        log.severe(message);
		        this.setErrorMessage(message);
		        result = ERROR;
		    }
		}
	    return result;
	}
	

	public void withSession(Map<String, Object> session) {
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

    public List<TableCheckInfo> getTableCheckInfos() {
        return tableCheckInfos;
    }

    public void setTableCheckInfos(List<TableCheckInfo> tableCheckInfos) {
        this.tableCheckInfos = tableCheckInfos;
    }


}
