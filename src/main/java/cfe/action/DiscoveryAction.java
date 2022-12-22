package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

public class DiscoveryAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(DiscoveryAction.class.getName());

	private Map<String, Object> webSession;
	
	private File discoveryCohortFile;
	private String discoveryCohortFileContentType;
	private String discoveryCohortFileName;
	
	private File discoveryCsv;
	private String discoveryCsvContentType;
	private String discoveryCsvFileName;
	    
	private File discoveryDb;
	private String discoveryDbContentType;
	private String discoveryDbFileName;
	
	private File probesetMappingDb;
	private String probesetMappingDbContentType;
	private String probesetMappingDbFileName;
	
	private String discoveryDbTempFileName;
	private String discoveryCsvTempFileName;
	
	private String scriptDir;
	private String scriptFile;
	private String scriptOutput;
	
	private String baseDir;
	
	private List<String> cohorts;
	private Map<String,String> diagnosisCodes;
	
	private String cohort;
	private String diagnosisCode;
	
	private String dbFileName;
	
	private String outputFile;
	private String reportFile;
	private String timingFile;
	
	private Set<String> pheneTables;
	
	private String pheneTable;
	
	private String pheneSelection;
	
	private double lowCutoff;
	private double highCutoff;
    private double discoveryCohortComparisonThreshold = 0.0001;  
    
	private String cohortDataCsv;
	private String cohortDataCsvFile;
	private String cohortDataXlsxFile;
	
	private String cohortCsvFile;
	private String cohortXlsxFile;
	
	private Set<String> genomicsTables;
	private String genomicsTable;
	
	private int numberOfSubjects;
	private int lowVisits;
	private int highVisits;
	
	private String scriptOutputTextFile;
	
	private String resultsXlsxFile;
	
	private String tempDir;
	
	private Date cohortGeneratedTime;
	private Date scoresGeneratedTime;
	
	private List<CfeResults> discoveryCohortResultsList;
	private Long discoveryId;
	
	private Long cfeResultsId;
	
	private String bigDataTempFileName;
	
	private boolean debugDiscoveryScoring = false;
	
	private String discoveryScoringCommand;
	
	/*
	private double dePercentileScore1 = 0;
	private double dePercentileScore2 = 1;
	private double dePercentileScore3 = 2;
	private double dePercentileScore4 = 4;
	*/
	
    private PercentileScores discoveryPercentileScores;
	
	Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
	private String scriptOutputTextFileName;
	
	public String initialize() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
	    return result;
	}
	
	public String uploadDatabase() throws Exception {
		String result = SUCCESS;
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
		else if (this.discoveryDb == null || this.discoveryDbFileName == null) {
		    this.setErrorMessage("No discovery database was specified.");
		    result = ERROR;
		}
		else if (!this.discoveryDbFileName.endsWith(".accdb")) {
		    this.setErrorMessage("Discovery database file \"" + discoveryDbFileName
		            + "\" does not have expected MS Access database file extension \".accdb\".");
		    result = ERROR;
		}
		else {
		    try {
		        log.info("Database \"" + this.discoveryDbFileName + "\" uploaded.");
		        
			    // Copy the upload files to temporary files, because the upload files get deleted
			    // and they are needed beyond this method
			    File discoveryDbTmp = FileUtil.createTempFile("discovery-db-", ".accdb");
			    FileUtils.copyFile(this.discoveryDb, discoveryDbTmp);
			    this.discoveryDbTempFileName = discoveryDbTmp.getAbsolutePath();
			
			    this.dbFileName = discoveryDb.getAbsolutePath();

			    DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(this.discoveryDbTempFileName);
			
	            dbParser.checkCoreTables();
	         
			    this.pheneTables = dbParser.getPheneTables();

			    this.phenes = dbParser.getTableColumnMap();
			
			    this.genomicsTables = dbParser.getGenomicsTables();
		    } catch (Exception exception) {
		        String message = "The Discovery database \"" + this.discoveryDbFileName + "\" could not be processed: " + exception.getLocalizedMessage();
		        log.severe(message);
		        this.setErrorMessage(message);
		        result = ERROR;
		    }
		}
	    return result;
	}
	
	/**
	 * Processes cohort specification and generates the cohort from it.
	 *
	 * @return
	 * @throws Exception
	 */
	public String cohortSpecification() {
		String result = SUCCESS;

		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		} else {
			try {
			    //-----------------------------------------------------------------------------------------------
			    
			    //-----------------------------------------------------------------------------------------------
		        DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(this.discoveryDbTempFileName);
		         
				// Split phene selection into table and phene
				String[] pheneInfo = pheneSelection.split("\\|", 2);
				this.pheneTable = pheneInfo[0];
				this.pheneSelection = pheneInfo[1];
				log.info("Phene \"" + pheneSelection + "\" selected from table \"" + pheneTable + "\"");

				dbParser.checkPheneTable(pheneTable);
				
				//-------------------------------------------------
				// Create the cohort data table
				//-------------------------------------------------
				Database db = dbParser.getDatabase();

				Table subjectIdentifiers = db.getTable("Subject Identifiers");
				Table demographics       = db.getTable("Demographics");
				Table diagnosis          = db.getTable("Diagnosis");
				//Table pheneData          = db.getTable(this.pheneTable);
				Table chips              = db.getTable(this.genomicsTable);

				CohortDataTable subjectIdentifiersData = new CohortDataTable();
				subjectIdentifiersData.initializeToAccessTable(subjectIdentifiers);

				CohortDataTable demographicsData = new CohortDataTable();
				demographicsData.initializeToAccessTable(demographics);

				CohortDataTable diagnosisData = new CohortDataTable();
				diagnosisData.initializeToAccessTable(diagnosis);

				//CohortDataTable pheneDataData = new CohortDataTable(this.pheneTable);
				//pheneDataData.initializeToAccessTable(pheneData);

				CohortDataTable chipsData = new CohortDataTable();
				chipsData.initializeToAccessTable(chips);

				CohortDataTable cohortData = subjectIdentifiersData.merge(demographicsData);
				
				log.info("Cohort data table has " + cohortData.getNumberOfRows() 
				    + " rows after merge of demographics table.");
				
				cohortData = cohortData.merge(diagnosisData);
	       
                log.info("Cohort data table has " + cohortData.getNumberOfRows() 
                    + " rows after merge of diagnosis table.");
                
				//log.info("Before phene tables merge");
				
				Set<String> pheneTableNames = dbParser.getPheneTables();
				for (String pheneTableName: pheneTableNames) {
				    log.info("Starting to add phene table \"" + pheneTableName + "\" to the cohort data.");
				    CohortDataTable pheneDataTable = new CohortDataTable();
				    Table dbTable = dbParser.getTable(pheneTableName);
				    pheneDataTable.initializeToAccessTable(dbTable);
				    
				    if (pheneDataTable.hasColumn("ID")) {
				        pheneDataTable.deleteColumn("ID");  // Delete an ID column if it exists
				    }
				    
				    String firstColumn = pheneDataTable.getColumnName(0);
				    if (!firstColumn.endsWith("PheneVisit")) {
				        throw new Exception("Phene table \"" + pheneTableName 
				                + "\" has first column \"" + firstColumn + "\", which is not "
				                + "a PheneVisit column.");    
				    }
				    //log.info("Data table for phene table \"" + pheneTableName + "\" created.");
				    cohortData = cohortData.mergePheneTable(pheneDataTable);
				    
	                log.info("Cohort data table has " + cohortData.getNumberOfRows() 
                    + " rows after merge of phene table \"" + pheneTableName + "\".");
                
				}
				//cohortData = cohortData.merge(pheneDataData);
				
				cohortData = cohortData.merge(chipsData);
                log.info("Cohort data table has " + cohortData.getNumberOfRows() 
                + " rows after merge of chips data table.");
                
				cohortData.deleteColumns("Field\\d+");
				
				// DEBUG
				// String csv1 = cohortData.toCsv();
				// File cfile1 = FileUtil.createTempFile("cohort-data-1-", ".csv");
				// FileUtils.writeStringToFile(cfile1, csv1, "UTF-8");
				
				log.info("chip data merged.");
				
				cohortData.enhance(pheneSelection, lowCutoff, highCutoff, this.discoveryCohortComparisonThreshold);

                // DEBUG
                // String csv2 = cohortData.toCsv();
                // File cfile2 = FileUtil.createTempFile("cohort-data-2-", ".csv");
                // FileUtils.writeStringToFile(cfile2, csv2, "UTF-8");
                
				log.info("Cohort data enhanced.");

				db.close();
				
				log.info("Database closed.");
				
				// Delete the temporary database file
				File file = new File(this.discoveryDbTempFileName);
				file.delete();

				//-------------------------------------------
				// Create cohort and cohort CSV file
				//-------------------------------------------
				log.info("Discovery cohort phene selection: \"" + pheneSelection + "\".");
				CohortTable cohort = cohortData.getDiscoveryCohort(pheneSelection, lowCutoff, highCutoff, this.discoveryCohortComparisonThreshold);
				cohort.sort("Subject", "PheneVisit"); 
                this.cohortGeneratedTime = new Date();
				
                // DEBUG
                // String csv3 = cohortData.toCsv();
                // File cfile3 = FileUtil.createTempFile("cohort-data-3-", ".csv");
                // FileUtils.writeStringToFile(cfile3, csv3, "UTF-8");
                
				this.numberOfSubjects = cohort.getNumberOfSubjects();
				this.lowVisits = cohort.getLowVisits();
				this.highVisits = cohort.getHighVisits();
				
				ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
                
				DataTable infoTable = this.createCohortInfoTable();
				
                // DEBUG
                // String csv4 = cohortData.toCsv();
                // File cfile4 = FileUtil.createTempFile("cohort-data-4-", ".csv");
                // FileUtils.writeStringToFile(cfile4, csv4, "UTF-8");
                
				LinkedHashMap<String, DataTable> cohortTables = new LinkedHashMap<String, DataTable>();
				cohortTables.put(CfeResultsSheets.DISCOVERY_COHORT,  cohort);
				cohortTables.put(CfeResultsSheets.COHORT_DATA, cohortData);
				cohortTables.put(CfeResultsSheets.DISCOVERY_COHORT_INFO, infoTable);
				
				int rowAccessWindowSize = 100;
				Workbook cohortWorkbook = DataTable.createStreamingWorkbook(cohortTables, rowAccessWindowSize);
				//cohortData.enhanceCohortDataSheet(cohortWorkbook, "cohort data", pheneSelection, lowCutoff, highCutoff);
                  
				// NO LONGER NEEDED ???
				//File cohortXlsxTempFile = File.createTempFile("cohort-", ".xlsx");
				//out = new FileOutputStream(cohortXlsxTempFile);
				//cohortWorkbook.write(out);
				//out.close();
				//this.cohortXlsxFile = cohortXlsxTempFile.getAbsolutePath();

	                
		        // Save the discovery cohort results in the CFE database
                CfeResults cfeResults = new CfeResults(cohortWorkbook, CfeResultsType.DISCOVERY_COHORT,
                        this.cohortGeneratedTime, this.pheneSelection,
                        lowCutoff, highCutoff);
                
                // Save all the discovery cohort data files
                cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT, cohort.toCsv());
                cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT_DATA, cohortData.toCsv());
                cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT_INFO, infoTable.toCsv());
                
                CfeResultsService.save(cfeResults);
                this.cfeResultsId = cfeResults.getCfeResultsId();
			} catch (Exception exception) {
				result = ERROR;
				String message = exception.getMessage();
				this.setErrorMessage(message);
				log.warning("*** ERROR: " + message);
				exception.printStackTrace();
			}
		}
		
		return result;
	}
	
	/**
	 * Select discovery cohort creation for scoring
	 * @return
	 * @throws Exception
	 */
	public String cohortSelection() throws Exception {
	    String result = SUCCESS;
	    
	    if (!Authorization.isAdmin(webSession)) {
	        result = LOGIN;
	    } else {
	        this.discoveryCohortResultsList = CfeResultsService.getMetadata(CfeResultsType.DISCOVERY_COHORT);
	        Collections.sort(this.discoveryCohortResultsList, new CfeResultsNewestFirstComparator());
	    }
	    
	    return result;
	}
	
	public String discoveryScoringSpecification() throws Exception {
	    String result = SUCCESS;
	    
	    log.info("Discovery scoring specification started.");
	    
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else if (discoveryId == null && discoveryCohortFile == null) {
            this.setErrorMessage("No discovery cohort specified.");
            result = ERROR;
        }
        else {
            try {
                this.discoveryPercentileScores = new PercentileScores();
                
                CfeResults discoveryCohortFileResults = null;
                
                //--------------------------------------------------------------------------------------
                // If a discovery cohort file was uploaded (as opposed to selecting saved results)
                //--------------------------------------------------------------------------------------
                if (this.discoveryCohortFile != null) {
                    log.info("DISCOVERY COHORT FILE UPLOADED.");
                    FileInputStream input = new FileInputStream(discoveryCohortFile);
                    XSSFWorkbook discoveryCohortWorkbook = new XSSFWorkbook(input);
                    
                    //--------------------------------------------------------
                    // Get phene and low and high cutoff
                    //--------------------------------------------------------
                    XSSFSheet sheet = discoveryCohortWorkbook.getSheet( CfeResultsSheets.DISCOVERY_COHORT_INFO );
                    if (sheet == null) {
                        discoveryCohortWorkbook.close();
                        throw new Exception("Could not get \"" + CfeResultsSheets.DISCOVERY_COHORT_INFO
                                + "\" sheet from discovery cohort data workbook.");
                    }
                    
                    DataTable discoveryCohortInfo = new DataTable("attribute");
                    discoveryCohortInfo.initializeToWorkbookSheet(sheet);
                    
                    // get phene
                    ArrayList<String> row = discoveryCohortInfo.getRow("Phene");
                    if (row == null || row.size() == 0) {
                        discoveryCohortWorkbook.close();
                        throw new Exception("Could not find Phene row in \"" + CfeResultsSheets.DISCOVERY_COHORT_INFO
                                + "\" sheet in discovery cohort data workbook.");
                    }
                    String phene = row.get(1);
                    
                    // get low cutoff
                    row = discoveryCohortInfo.getRow("Low Cutoff");
                    if (row == null || row.size() == 0) {
                        discoveryCohortWorkbook.close();
                        throw new Exception("Could not find low cutoff row in \"" + CfeResultsSheets.DISCOVERY_COHORT_INFO
                                + "\" sheet in discovery cohort data workbook.");
                    }
                    String lowCutoffString = row.get(1);
                    Double lowCutoff = 0.0;
                    lowCutoff = Double.parseDouble(lowCutoffString);

                    // get low cutoff
                    row = discoveryCohortInfo.getRow("High Cutoff");
                    if (row == null || row.size() == 0) {
                        discoveryCohortWorkbook.close();
                        throw new Exception("Could not find high cutoff row in \"" + CfeResultsSheets.DISCOVERY_COHORT_INFO
                                + "\" sheet in discovery cohort data workbook.");
                    }
                    String highCutoffString = row.get(1);
                    Double highCutoff = 0.0;
                    highCutoff = Double.parseDouble(highCutoffString);
                    
                    // generated time
                    Date uploadTime = new Date();
                    
                    discoveryCohortFileResults = new CfeResults(
                            discoveryCohortWorkbook,
                            CfeResultsType.DISCOVERY_COHORT, 
                            uploadTime,   // generated time
                            phene,
                            lowCutoff,
                            highCutoff
                    );
                    discoveryCohortFileResults.setUploaded(true); // Set uploaded flag
                    CfeResultsService.save(discoveryCohortFileResults);
                    this.discoveryId = discoveryCohortFileResults.getCfeResultsId();
                }
                
                ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

                CfeResults results = null;
                if (discoveryCohortFileResults != null) {
                    results = discoveryCohortFileResults;
                    log.info("Using an uploaded discovery cohort file.");
                }
                else {
                    log.info("Getting discovery cohort results from the database with ID: " + this.discoveryId + ".");
                    results = CfeResultsService.get(this.discoveryId);
                }
                
                XSSFWorkbook workbook = results.getResultsSpreadsheet();
            
                if (workbook == null) {
                    String message = "The selected discovery cohort does not contain a spreadsheet.";
                    log.severe(message);
                    throw new Exception(message);
                }
                
                // Use these to get files
                // results.getFileAsDataTable(CfeResultsFileType.DISCOVERY_COHORT_INFO);
                // results.getFileAsDataTable(CfeResultsFileType.DISCOVERY_COHORT_DATA);
                // results.getFileAsDataTable(CfeResultsFileType.DISCOVERY_COHORT);
                
                this.lowCutoff      = results.getLowCutoff();
                this.highCutoff     = results.getHighCutoff();
                this.pheneSelection = results.getPhene();
                
                if (this.lowCutoff >= this.highCutoff) {
                    throw new Exception("The low cutoff (" + this.lowCutoff + ") is not less than the "
                            + "high cutoff (" + this.highCutoff + ").");
                }
                
                ArrayList<String> row;
                
                // Get the phene table from the cohort info sheet
                // OLD CODE:
                XSSFSheet discoveryCohortInfoSheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO);
                
                if (discoveryCohortInfoSheet == null) {
                    String message = "The discovery cohort workbook is missing sheet \"" + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".";
                    log.severe(message);
                    throw new Exception(message);    
                }
                
                DataTable cohortInfo = new DataTable("attribute");
                cohortInfo.initializeToWorkbookSheet(discoveryCohortInfoSheet);
                
                // NEW CODE:
                // DataTable cohortInfo = results.getFileAsDataTable(CfeResultsFileType.DISCOVERY_COHORT_INFO);
                // 
                // if (cohortInfo == null) {
                //    String message = "Could not find file \"" + CfeResultsFileType.DISCOVERY_COHORT_INFO + "\".";
                //    log.severe(message);
                //    throw new Exception(message);    
                //}
                //cohortInfo.setKey("attribute");
                
                row = cohortInfo.getRow("Phene Table");
                if (row == null) {
                    throw new Exception("Unable to find Phene Table row in sheet \""
                            + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".");
                }
                this.pheneTable = row.get(1);
                if (this.pheneTable == null || this.pheneTable.isEmpty()) {
                    throw new Exception("Could not get phene table information from workbook sheet \""
                            + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".");
                }
                
                row = cohortInfo.getRow("Microarray Table");
                if (row != null && row.size() >= 2) {
                    this.genomicsTable = row.get(1);
                }
                
                row = cohortInfo.getRow("Number of Cohort Subjects");
                if (row != null && row.size() >= 2) {
                    String numSubjects = row.get(1);
                    try {
                        this.numberOfSubjects = Integer.parseInt(numSubjects);
                    }
                    catch (NumberFormatException exception) {
                        ; // ignore
                    }
                }
                
                row = cohortInfo.getRow("Number of Cohort Low Visits");
                if (row != null && row.size() >= 2) {
                    String numSubjects = row.get(1);
                    try {
                        this.lowVisits = Integer.parseInt(numSubjects);
                    }
                    catch (NumberFormatException exception) {
                        ; // ignore
                    }
                }
                
                row = cohortInfo.getRow("Number of Cohort High Visits");
                if (row != null && row.size() >= 2) {
                    String numSubjects = row.get(1);
                    try {
                        this.highVisits = Integer.parseInt(numSubjects);
                    }
                    catch (NumberFormatException exception) {
                        ; // ignore
                    }
                }
                
                // Get diagnosis codes
                // OLD CODE:
                XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
                if (sheet == null) {
                    String message = "Could not find sheet \"" + CfeResultsSheets.COHORT_DATA + "\" in spreadsheet.";
                    log.severe(message);
                    throw new Exception(message);
                }
                DataTable diagnosisData = new DataTable("DxCode");
                diagnosisData.initializeToWorkbookSheet(sheet);
                
                // NEW CODE:
                //DataTable diagnosisData = results.getFileAsDataTable(CfeResultsFileType.DISCOVERY_COHORT_DATA);
                //
                //if (diagnosisData == null) {
                //    String message = "Could not find file \"" + CfeResultsFileType.DISCOVERY_COHORT_DATA + "\".";
                //    log.severe(message);
                //    throw new Exception(message);      
                //}
                
                diagnosisCodes = new HashMap<String,String>();

                for (int i = 0; i < diagnosisData.getNumberOfRows(); i++) {
                    String code    = diagnosisData.getValue(i, "DxCode");
                    String example = diagnosisData.getValue(i, "Primary DIGS DX").trim();

                    String examples = "";
                    if (diagnosisCodes.containsKey(code)) {
                        examples = diagnosisCodes.get(code);
                    }

                    if (examples.isEmpty()) {
                        examples = example;
                    }
                    else {
                        if (!examples.contains(example)) {
                            examples += "; " + example;
                        }
                    }

                    this.diagnosisCodes.put(code, examples);
                }
            }
            catch (Exception exception) {
                String message = exception.getLocalizedMessage();
                if (message == null) {
                    message = "An unknown error occurred.";
                }
                this.setErrorMessage(message);
                String stackTrace = ExceptionUtils.getStackTrace(exception);
                log.severe("STACK TRACE: " + stackTrace);
                this.setExceptionStack(stackTrace);
                result = ERROR;
            }
        }

	    return result;
	}
	
	/**
	 * Calculates the discovery results.
	 * 
	 * @return the status of this action.
	 * 
	 * @throws Exception
	 */
	public String calculate() throws Exception {
		String result = SUCCESS;
		
		log.info("Discovery calculation phase started");
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
        //else if (this.discoveryDb == null || this.discoveryDbFileName == null) {
        //    this.errorMessage = "No Phene Visit database file was specified.";
        //    result = INPUT;
        //}
        //else if (!this.discoveryDbFileName.endsWith(".accdb")) {
        //    this.er)rorMessage = "Phene Visit database file \"" + discoveryDbFileName
        //            + "\" is not a \".accdb\" (MS Access) file.";
        //    result = INPUT;
        //}		
		else if (this.discoveryCsv == null || this.discoveryCsvFileName == null) {
	        this.setErrorMessage("No gene expression CSV file was specified.");
	        result = INPUT;
	    }
	    else if (!this.discoveryCsvFileName.endsWith(".csv")) {
	        this.setErrorMessage("Gene expression file \"" + discoveryCsvFileName
	                + "\" is not a \".csv\" file.");
	        result = INPUT;
	    }
	    else if (this.probesetMappingDb == null || this.probesetMappingDbFileName == null) {
	        this.setErrorMessage("No probeset to gene mapping database file was specified.");
	        result = INPUT;
	    }
	    else if (!this.probesetMappingDbFileName.endsWith(".accdb")) {
	        this.setErrorMessage("Probeset to gene mapping database file \"" + probesetMappingDbFileName
	                + "\" is not a \".accdb\" (MS Access) file.");
	        result = INPUT;
	    }
	    else {
            try {
                log.info("Starting Discovery calculation");
                log.info("Diagnosis code: " + this.diagnosisCode);
                this.baseDir = WebAppProperties.getRootDir();

                this.scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
                this.scriptFile = new File(getClass().getResource("/R/DEdiscovery.R").toURI()).getAbsolutePath();

                //this.tempDir = System.getProperty("java.io.tmpdir");
                this.tempDir = FileUtil.getTempDir();
                
                //---------------------------------------------------------------------
                // Create discovery cohort file that will be passed to the R script
                //---------------------------------------------------------------------
                CfeResults results = CfeResultsService.get(this.discoveryId);
                XSSFWorkbook workbook = results.getResultsSpreadsheet();
                
                XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT);
                DataTable discoveryCohort = new DataTable("PheneVisit");
                
                discoveryCohort.initializeToWorkbookSheet(sheet);
                String cohortCsv = discoveryCohort.toCsv();

                File cohortCsvTempFile = FileUtil.createTempFile("cohort-", ".csv");
                FileUtils.writeStringToFile(cohortCsvTempFile, cohortCsv, "UTF-8");
                this.cohortCsvFile = cohortCsvTempFile.getAbsolutePath();
                
                //----------------------------------------------------
                // Get bigData
                //----------------------------------------------------
                CohortDataTable cohortData = new CohortDataTable(this.pheneTable);
                sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
                cohortData.initializeToWorkbookSheet(sheet);
                
                DataTable bigData = cohortData.getBigData(this.pheneSelection);
                
                File bigDataTmp = FileUtil.createTempFile("bigData-", ".csv");
                if (bigDataTmp != null) {
                    FileUtils.writeStringToFile(bigDataTmp, bigData.toCsv(), "UTF-8");
                } else {
                    throw new Exception("Could not create bigData temporary file.");
                }
                this.bigDataTempFileName = bigDataTmp.getAbsolutePath();
                
                //--------------------------------------------------------------------------------
                // Get the phene database file
                //--------------------------------------------------------------------------------
                //File discoveryDbTmp = File.createTempFile("discovery-db-", ".accdb");
                //if (discoveryDbTmp != null) {
                //    FileUtils.copyFile(this.discoveryDb, discoveryDbTmp);
                //}
                //this.discoveryDbTempFileName = discoveryDbTmp.getAbsolutePath();
                
                //------------------------------------------------------
                // Get the gene expression CSV file
                //------------------------------------------------------
                File discoveryCsvTmp = FileUtil.createTempFile("discovery-csv-",  ".csv");
                if (this.discoveryCsv != null) {
                    FileUtils.copyFile(this.discoveryCsv, discoveryCsvTmp);
                }
                this.discoveryCsvTempFileName = discoveryCsvTmp.getAbsolutePath();

                //------------------------------------------------------------
                // Get the probeset to mapping information
                //------------------------------------------------------------
                String key = "Probe Set ID";
                DataTable probesetMapping = new DataTable(key);
                
                ProbesetMappingParser dbParser = new ProbesetMappingParser(this.probesetMappingDb.getAbsolutePath());
                Table table = dbParser.getMappingTable();
                probesetMapping.initializeToAccessTable(table);
                
                //---------------------------------------------
                // Process diagnosis code
                //---------------------------------------------
                if (this.diagnosisCode == null) this.diagnosisCode = "";
                this.diagnosisCode = this.diagnosisCode.trim();
                if (this.diagnosisCode.equals("") || this.diagnosisCode.equalsIgnoreCase("ALL")) {
                    this.diagnosisCode = "All";
                }

                //---------------------------------------
                // Create R script command
                //---------------------------------------
                String[] rScriptCommand = new String[12];
                rScriptCommand[0] = WebAppProperties.getRscriptPath();    // Full path of the Rscript command
                rScriptCommand[1] = scriptFile;     // The R script to run
                rScriptCommand[2] = scriptDir;
                rScriptCommand[3] = this.cohortCsvFile;   // Change - name of cohort CSV File
                rScriptCommand[4] = this.diagnosisCode;
                //rScriptCommand[5] = this.discoveryDbTempFileName;
                rScriptCommand[5] = this.discoveryCsvTempFileName;
                rScriptCommand[6] = this.pheneSelection;
                rScriptCommand[7] = this.pheneTable;
                rScriptCommand[8] = this.lowCutoff + "";
                rScriptCommand[9] = this.highCutoff + "";
                rScriptCommand[10] = this.tempDir;
                rScriptCommand[11] = this.bigDataTempFileName;

                // Log a version of the command used for debugging (OUT OF DATE - needs to be fixed or removed)
                //String logRScriptCommand = WebAppProperties.getRscriptPath() + " " + scriptFile 
                //        + " " + scriptDir 
                //        + " " + "\"" + this.cohortCsvFile + "\"" + " " + "\"" + this.diagnosisCode + "\"" 
                //        + " \"" + discoveryDbFileName + "\" \"" + discoveryCsvFileName + "\"" + this.pheneSelection;
                //log.info("LOG RSCRIPT COMMAND: " + logRScriptCommand);

                log.info("RSCRIPT COMMAND: " + String.join(" ", rScriptCommand));
                
                this.discoveryScoringCommand = "\"" + String.join("\" \"",  rScriptCommand) + "\"";
                
                scriptOutput = this.runCommand(rScriptCommand);
                
                log.info("Returned from DEdiscovery.R script");
                
                this.scoresGeneratedTime = new Date();

                //-------------------------------------------------------
                // Set up script output file log
                //-------------------------------------------------------
                if (this.debugDiscoveryScoring) {
                    File scriptOutputTextTempFile = FileUtil.createTempFile("discovery-r-script-output-", ".txt");
                    FileOutputStream out = new FileOutputStream(scriptOutputTextTempFile);
                    PrintWriter writer = new PrintWriter(out);
                    writer.write(scriptOutput);
                    writer.close();
                    this.scriptOutputTextFile = scriptOutputTextTempFile.getAbsolutePath();
                    //this.scriptOutputTextFileName = "script-output.txt";
                    log.info("script output text file: " + scriptOutputTextFile);
                }
                
                //---------------------------------------------------------------
                // Get the output, report and timing file paths
                //---------------------------------------------------------------
                String outputFilePatternString = "Output file created: (.*)";
                String reportFilePatternString = "Report file created: (.*)";
                String timingFilePatternString = "Timing file created: (.*)";

                Pattern outputFilePattern = Pattern.compile(outputFilePatternString);
                Pattern reportFilePattern = Pattern.compile(reportFilePatternString);
                Pattern timingFilePattern = Pattern.compile(timingFilePatternString);

                String lines[] = scriptOutput.split("\\r?\\n");
                for (String line: lines) {
                    Matcher outputMatcher = outputFilePattern.matcher(line);
                    Matcher reportMatcher = reportFilePattern.matcher(line);
                    Matcher timingMatcher = timingFilePattern.matcher(line);

                    if (outputMatcher.find()) {
                        outputFile = outputMatcher.group(1).trim();
                        log.info("Output file pattern found: \"" + outputFile + "\".");
                    }

                    if (reportMatcher.find()) {
                        reportFile = reportMatcher.group(1).trim();
                        log.info("Report file pattern found: \"" + reportFile + "\".");
                    }

                    if (timingMatcher.find()) {
                        timingFile = timingMatcher.group(1).trim();
                        log.info("Timing file pattern found: \"" + timingFile + "\".");
                    }				
                }

                //--------------------------------------------
                // Create results workbook
                //--------------------------------------------
                
                CfeResults discoveryCohortResults = CfeResultsService.get(discoveryId);
                XSSFWorkbook cohortWorkbook = discoveryCohortResults.getResultsSpreadsheet();
                
                // Create output data table from the output CSV file generated by the
                // Discovery R Script
                DataTable outputDataTable = new DataTable(null);
                if (outputFile == null) {
                    String errorMessage = "No output file generated for discovery calculation.";
                    log.severe(errorMessage);
                    this.setErrorMessage(errorMessage);
                    result = ERROR;
                }
                else {
                    outputDataTable.initializeToCsv(outputFile);
                    // Add Genecards Symbols
                    outputDataTable.renameColumn("DEscores", "DE Raw Score");
                    outputDataTable.addColumn("DE Percentile", "");
                    outputDataTable.addColumn("DE Score", "");
                    outputDataTable.addColumn(ProbesetMappingParser.GENECARDS_SYMBOL_COLUMN, "");
                    outputDataTable.setColumnName(0, ProbesetMappingParser.PROBE_SET_ID_COLUMN);
                
                    log.info("Before call to deScoring.");
                    this.deScoring(outputDataTable);  // calculate percentiles and scores
                    log.info("After call to deScoring.");
                    
                    for (int rowIndex = 0; rowIndex < outputDataTable.getNumberOfRows(); rowIndex++) {
                        String keyValue = outputDataTable.getValue(rowIndex, 0);
                        String genecardsSymbol = probesetMapping.getValue(keyValue, ProbesetMappingParser.GENECARDS_SYMBOL_COLUMN);
                        outputDataTable.setValue(rowIndex, ProbesetMappingParser.GENECARDS_SYMBOL_COLUMN, genecardsSymbol);
                    }
                }
                outputDataTable.deleteRow("Probe Set ID", "VisitNumber");
                
                log.info("Calculation of outputDataTable complete.");

                // Create "Discovery Report" data table
                DataTable reportDataTable = new DataTable(null);
                if (reportFile != null && !reportFile.isEmpty()) {
                    reportDataTable.initializeToCsv(reportFile);
                }

                // Create "Discovery Scores Info" data table
                DataTable discoveryScoresInfoDataTable = this.createDiscoveryScoresInfoTable();
                
                Sheet streamingSheet;
                
                // Create "Discovery Cohort" data table
                DataTable discoveryCohortDataTable = new DataTable(null);
                streamingSheet = cohortWorkbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT);
                discoveryCohortDataTable.initializeToWorkbookStreamingSheet(streamingSheet);
                log.info("Discovery cohort data table created.");

                // Create "Cohort Data" data table
                CohortDataTable cohortDataDataTable = new CohortDataTable();
                streamingSheet = cohortWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
                cohortDataDataTable.initializeToWorkbookStreamingSheet(streamingSheet);            
                log.info("Cohort data data table created.");
                
                // Create "Discovery Cohort Info" data table
                DataTable discoveryCohortInfoDataTable = new DataTable(null);
                streamingSheet = cohortWorkbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO);
                discoveryCohortInfoDataTable.initializeToWorkbookStreamingSheet(streamingSheet);
                log.info("Discovery cohort info data table created.");
                
                // Create "Discovery R script log" data table
                //DataTable discoveryRScriptLogDataTable = new DataTable(null);
                //discoveryRScriptLogDataTable.addColumn("Discovery R Script Log", "");
                //ArrayList<String> dataRow = new ArrayList<String>();
                //dataRow.add(this.scriptOutput);
                //discoveryRScriptLogDataTable.addRow(dataRow);
                
                
                LinkedHashMap<String, DataTable> resultsTables = new LinkedHashMap<String, DataTable>();

                resultsTables.put(CfeResultsSheets.DISCOVERY_SCORES, outputDataTable);
                resultsTables.put(CfeResultsSheets.DISCOVERY_REPORT, reportDataTable);
                resultsTables.put(CfeResultsSheets.DISCOVERY_SCORES_INFO, discoveryScoresInfoDataTable);
                resultsTables.put(CfeResultsSheets.DISCOVERY_COHORT, discoveryCohortDataTable);
                resultsTables.put(CfeResultsSheets.DISCOVERY_COHORT_INFO, discoveryCohortInfoDataTable);
                resultsTables.put(CfeResultsSheets.COHORT_DATA, cohortDataDataTable);
                log.info("resultsTables created - size: " + resultsTables.size());
                
                Workbook resultsWorkbook = DataTable.createWorkbook(resultsTables);
                log.info("resultsWorkbook created.");

                //log.info("pheneSelection = \"" + pheneSelection + "\" - low cutoff: " + lowCutoff + " - high cutoff: " + highCutoff);
                //cohortDataDataTable.enhanceCohortDataSheet(resultsWorkbook, "cohort data", pheneSelection, lowCutoff, highCutoff);
                //log.info("resultsWoorkbook enhanced.");
                //WorkbookUtil.setCellForLongText(resultsWorkbook, CfeResultsSheets.DISCOVERY_R_SCRIPT_LOG, 1, 0);

                // Timing CSV (WORK IN PROGRESS)
                //DataTable timingTable = new DataTable(null);
                //timingTable.initializeToCsv(timingFile);
                //File timingCsvTempFile = File.createTempFile("discovery-timing-", ".csv");
                //FileOutputStream timingOut = new FileOutputStream(timingCsvTempFile);
                //timingOut.close();
                
                // Save the results in the database
                CfeResults cfeResults = new CfeResults(resultsWorkbook, CfeResultsType.DISCOVERY_SCORES,
                        this.scoresGeneratedTime, this.pheneSelection,
                        lowCutoff, highCutoff);
                log.info("cfeResults object created.");
                
                
                cfeResults.setDiscoveryRScriptLog(scriptOutput);
                log.info("Discovery scoring results object created.");
                
                // Add a file for the R script log
                cfeResults.addTextFile(CfeResultsFileType.DISCOVERY_R_SCRIPT_LOG, scriptOutput);
                
                CfeResultsService.save(cfeResults);
                log.info("Discovery scoring results object saved.");
                
                this.cfeResultsId = cfeResults.getCfeResultsId();
                log.info("Discovery calculation - CFE Results ID: " + cfeResultsId);
                
                //--------------------------------
                // Clean up temporary files
                //--------------------------------
                File file;
                
                // R script input files
                if (!this.debugDiscoveryScoring) {
                    file = new File(this.cohortCsvFile);
                    file.delete();
                
                    file = new File(this.discoveryCsvTempFileName);
                    file.delete();
                
                    file = new File(this.bigDataTempFileName);
                    file.delete();
                }
                
                // R script output files:
                if (outputFile != null && !outputFile.isEmpty()) {
                    file = new File(outputFile);
                    file.delete();
                }
                
                if (reportFile != null && !reportFile.isEmpty()) {
                    file = new File(reportFile);
                    file.delete();
                }
                
                if (timingFile != null && !timingFile.isEmpty()) {
                    file = new File(timingFile);
                    file.delete();
                }
            }
            catch (Exception exception) {
                result = ERROR;
                if (exception != null) {
                    this.setErrorMessage("Discovery calculation failed: " + exception.getLocalizedMessage());
                    String stackTrace = ExceptionUtils.getStackTrace(exception);
                    this.setExceptionStack(stackTrace);
                }
            }
        }

		return result;
	}
	
	public void deScoring(DataTable scoring) throws Exception {
	    Double negativeMax = null;
	    Double positiveMax = null;
	    
	    // Get positive and negative min and max
	    for (int rowNum = 0; rowNum < scoring.getNumberOfRows(); rowNum++) {
	        Map<String, String> rowMap = scoring.getRowMap(rowNum);
	        String score = rowMap.get("DE Raw Score");
	        if (score != null) {
	            try {
	                double rawScore = Double.parseDouble(score);
	                if (rawScore >= 0.0) {
	                    if (positiveMax == null) {
	                        positiveMax = rawScore;
	                    }
	                    else {
	                        if (rawScore > positiveMax) {
	                            positiveMax = rawScore;
	                        }
	                    }	                
	                }
	                else {
	                    if (negativeMax == null) {
	                        negativeMax = rawScore;
	                    }
	                    else {
	                        if (rawScore < negativeMax) {
	                            negativeMax = rawScore;
	                        }
	                    }
	                }
	            }
	            catch (NumberFormatException exception) {
	                ;  // no score - skip
	            }
	        }
	    }
	    
	    for (int rowNum = 0; rowNum < scoring.getNumberOfRows(); rowNum++) {
	        Map<String, String> rowMap = scoring.getRowMap(rowNum);
	        String score = rowMap.get("DE Raw Score");
	        if (score != null) {
	            try {
	                double rawScore = Double.parseDouble(score);
	                double dePercentile;
	                if (rawScore >= 0.0) {
	                    dePercentile = rawScore / positiveMax;
	                }
	                else {
	                    dePercentile = rawScore / negativeMax;
	                }
	                scoring.setValue(rowNum, "DE Percentile", dePercentile + "");

	                double deScore = 0.0;

	                deScore = this.discoveryPercentileScores.getScore(dePercentile);
	                
	                /*
	                if (dePercentile < 0.333333333333) {
	                    deScore = this.dePercentileScore1;
	                }
	                else if (dePercentile < 0.50) {
	                    deScore = this.dePercentileScore2;
	                }
	                else if (dePercentile < 0.80) {
	                    deScore = this.dePercentileScore3;
	                }
	                else {
	                    deScore = this.dePercentileScore4;
	                }
	                */

	                scoring.setValue(rowNum, "DE Score", deScore + "");
	            }
	            catch (NumberFormatException exception) {
	                ;  // no score - skip
	            }
	        }
	    }
	}
	
	public DataTable createPheneDataTable(DiscoveryDatabaseParser dbParser) throws Exception {
	    String keyColumn = "PheneVisit";
	    DataTable pheneDataTable = null;
	    
	    Set<String> pheneTableNames = new TreeSet<String>();
	    pheneTableNames = dbParser.getPheneTables();
	    
	    for (String pheneTableName: pheneTableNames) {
	        DataTable dataTable = new DataTable(keyColumn);
	        dataTable.initializeToAccessTable( dbParser.getTable(pheneTableName) );
	        if (pheneDataTable == null) {
	            pheneDataTable = dataTable;
	        }
	        else {
	            String joinColumn1 = keyColumn;
	            String joinColumn2 = keyColumn;
	            DataTable.join(keyColumn, joinColumn1, joinColumn2, pheneDataTable, dataTable, DataTable.JoinType.OUTER);
	        }
	    }
	    
	    return pheneDataTable;
	}
	
	public DataTable createDiscoveryScoresInfoTable() throws Exception {
        DataTable infoTable = new DataTable("attribute");
        infoTable.insertColumn("attribute", 0, "");
        infoTable.insertColumn("value",  1,  "");
        
        ArrayList<String> row = new ArrayList<String>();
        row.add("CFE Version");
        row.add(VersionNumber.VERSION_NUMBER);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Time Scores Generated");
        row.add(this.scoresGeneratedTime.toString());
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("");
        row.add("");
        infoTable.addRow(row);
        
        /*
        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.00 <= x < 0.33)");
        row.add("" + this.dePercentileScore1);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.33 <= x < 0.50)");
        row.add("" + this.dePercentileScore2);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.50 <= x < 0.80)");
        row.add("" + this.dePercentileScore3);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.80 <= x < 1.00)");
        row.add("" + this.dePercentileScore4);
        infoTable.addRow(row);
        */
        
        List<Double> lowerBounds = this.discoveryPercentileScores.getLowerBounds();
        List<Double> upperBounds = this.discoveryPercentileScores.getUpperBounds();
        List<Double> scores      = this.discoveryPercentileScores.getScores();
        for (int i = 0; i < lowerBounds.size(); i++) {
            row = new ArrayList<String>();
            row.add("DE Percentile Score (" + lowerBounds.get(i) + " <= x <= " + upperBounds.get(i) + ")");
            row.add("" + scores.get(i));
            infoTable.addRow(row);
        }
        
        row = new ArrayList<String>();
        row.add("");
        row.add("");
        infoTable.addRow(row);               
        
        row = new ArrayList<String>();
        row.add("Diagnosis Code");
        row.add(this.diagnosisCode);
        infoTable.addRow(row);
        
        if (this.timingFile != null && !this.timingFile.isEmpty()) {
            DataTable timing = new DataTable(null);
            timing.initializeToCsv(timingFile);
            
            // Add blank spacing row
            row = new ArrayList<String>();
            row.add("");
            row.add("");
            infoTable.addRow(row);
            
            for (int i = 1; i < timing.getNumberOfRows(); i++) {
                ArrayList<String> time = timing.getRow(i);
                row = new ArrayList<String>();
                if (time.get(1).equals("total")) {
                    row.add("total time (minutes)");
                } else {
                    row.add("time for " + time.get(1));
                }
                row.add(time.get(2));
                infoTable.addRow(row);
            }
        }

        
	    return infoTable;
	}
	
    public DataTable createCohortInfoTable() throws Exception {
		DataTable infoTable = new DataTable("attribute");
		infoTable.insertColumn("attribute", 0, "");
		infoTable.insertColumn("value",  1,  "");
		
		ArrayList<String> row = new ArrayList<String>();
		row.add("CFE Version");
		row.add(VersionNumber.VERSION_NUMBER);
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Time Cohort Generated");
		row.add(this.cohortGeneratedTime.toString());
		infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Test Database");
        row.add(this.discoveryDbFileName);
        infoTable.addRow(row);
        
		row = new ArrayList<String>();
		row.add("Phene Table");
		row.add(this.pheneTable);
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Phene");
		row.add(this.pheneSelection);
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Low Cutoff");
		row.add(this.lowCutoff + "");
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("High Cutoff");
		row.add(this.highCutoff + "");
		infoTable.addRow(row);
		
		row = new ArrayList<String>();
		row.add("Microarray Table");
		row.add(this.genomicsTable);
		infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of Cohort Subjects");
        row.add(this.numberOfSubjects + "");
        infoTable.addRow(row);
        
	    row = new ArrayList<String>();
	    row.add("Number of Cohort Low Visits");
	    row.add(this.lowVisits + "");
	    infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of Cohort High Visits");
        row.add(this.highVisits + "");
        infoTable.addRow(row);	        
		return infoTable;
    }
    
    /** 
     * WORK IN PROGRESS
     * For checking that gene expression file has phene visit, instead of affy visit, columns. 
     */
    public void checkGeneExpressionFile(String geneExpressionFile) throws Exception {
        FileReader filereader = new FileReader(geneExpressionFile);
        CSVReader csvReader = new CSVReader(filereader);
        String[] visits;
     
        visits = csvReader.readNext();
        for (String visit: visits) {
            // Need to compare these to pheneVisits in the cohort data
            // ...
        }

    }
    
	/** 
	 * Executes the specified command and returns the output from the command.
	 *
	 * @param command the command to execute
	 * @return the output generated by the command
	 * @throws Exception
	 */
	public String runCommand(String[] command) throws Exception {
		StringBuilder output = new StringBuilder();
		
        //Process process = Runtime.getRuntime().exec(command);
		
		log.info("run command: " + String.join(" ", command));
        
		// This allows debugging:
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // redirect standard error to standard output
        
        Process process = processBuilder.start();


	    BufferedReader reader = new BufferedReader(
	    new InputStreamReader(process.getInputStream()));

	    String line;
	    while ((line = reader.readLine()) != null) {
	        output.append(line + "\n");
	    }

	    log.info("Waiting for process...");
	    int status = process.waitFor();
	    if (status != 0) {
            //throw new Exception("Command \"" + command + "\" exited with code " + status);
	    }
	    
	    reader.close();
		return output.toString();
	}
	
	public void setSession(Map<String, Object> session) {
		this.webSession = session;
		
	}
	
	public void validate() {
	}

	public Map<String, Object> getSession() {
		return webSession;
	}

	public File getDiscoveryCsv() {
		return discoveryCsv;
	}

	public void setDiscoveryCsv(File discoveryCsv) {
		this.discoveryCsv = discoveryCsv;
	}

	public String getDiscoveryCsvContentType() {
		return discoveryCsvContentType;
	}

	public void setDiscoveryCsvContentType(String discoveryCsvContentType) {
		this.discoveryCsvContentType = discoveryCsvContentType;
	}

	public String getDiscoveryCsvFileName() {
		return discoveryCsvFileName;
	}

	public void setDiscoveryCsvFileName(String discoveryCsvFileName) {
		this.discoveryCsvFileName = discoveryCsvFileName;
	}

	public File getDiscoveryDb() {
		return discoveryDb;
	}

	public void setDiscoveryDb(File discoveryDb) {
		this.discoveryDb = discoveryDb;
	}

	public String getDiscoveryDbContentType() {
		return discoveryDbContentType;
	}

	public void setDiscoveryDbContentType(String discoverDbContentType) {
		this.discoveryDbContentType = discoverDbContentType;
	}

	public String getDiscoveryDbFileName() {
		return discoveryDbFileName;
	}

	public void setDiscoveryDbFileName(String dicoveryDbFileName) {
		this.discoveryDbFileName = dicoveryDbFileName;
	}
	
	
	public File getProbesetMappingDb() {
        return probesetMappingDb;
    }

    public void setProbesetMappingDb(File probesetMappingDb) {
        this.probesetMappingDb = probesetMappingDb;
    }

    public String getProbesetMappingDbContentType() {
        return probesetMappingDbContentType;
    }

    public void setProbesetMappingDbContentType(String probesetMappingDbContentType) {
        this.probesetMappingDbContentType = probesetMappingDbContentType;
    }

    public String getProbesetMappingDbFileName() {
        return probesetMappingDbFileName;
    }

    public void setProbesetMappingDbFileName(String probesetMappingDbFileName) {
        this.probesetMappingDbFileName = probesetMappingDbFileName;
    }

    
    
    public String getBaseDir() {
		return baseDir;
	}	
	
	public String getScriptDir() {
	    return scriptDir;	
	}
	
	public String getScriptFile() {
		return scriptFile;
	}
	
	public String getScriptOutput() {
		return scriptOutput;
	}
	
	public List<String> getCohorts() {
		return cohorts;
	}
	
	public Map<String,String> getDiagnosisCodes() {
		return diagnosisCodes;
	}
	
	public String getDbFileName() {
		return this.dbFileName;
	}

	public void setDbFileName(String dbFileName) {
	    this.dbFileName = dbFileName;
	}

	public String getCohort() {
		return cohort;
	}

	public void setCohort(String cohort) {
		this.cohort = cohort;
	}

	public String getDiagnosisCode() {
		return diagnosisCode;
	}

	public void setDiagnosisCode(String diagnosisCode) {
		this.diagnosisCode = diagnosisCode;
	}

	public String getDiscoveryDbTempFileName() {
		return discoveryDbTempFileName;
	}

	public void setDiscoveryDbTempFileName(String discoveryDbTempFileName) {
		this.discoveryDbTempFileName = discoveryDbTempFileName;
	}

	public String getDiscoveryCsvTempFileName() {
		return discoveryCsvTempFileName;
	}

	public void setDiscoveryCsvTempFileName(String discoveryCsvTempFileName) {
		this.discoveryCsvTempFileName = discoveryCsvTempFileName;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	public Set<String> getPheneTables() {
		return pheneTables;
	}

	public void setPheneTables(Set<String> pheneTables) {
		this.pheneTables = pheneTables;
	}

	public Map<String, ArrayList<ColumnInfo>> getPhenes() {
		return phenes;
	}

	public void setPhenes(Map<String, ArrayList<ColumnInfo>> phenes) {
		this.phenes = phenes;
	}

	public String getPheneSelection() {
		return pheneSelection;
	}

	public void setPheneSelection(String pheneSelection) {
		this.pheneSelection = pheneSelection;
	}

	public double getLowCutoff() {
		return lowCutoff;
	}

	public void setLowCutoff(double lowCutoff) {
		this.lowCutoff = lowCutoff;
	}

	public double getHighCutoff() {
		return highCutoff;
	}

	public void setHighCutoff(double highCutoff) {
		this.highCutoff = highCutoff;
	}

	public String getPheneTable() {
		return pheneTable;
	}

	public void setPheneTable(String pheneTable) {
		this.pheneTable = pheneTable;
	}

	public String getCohortDataCsv() {
		return cohortDataCsv;
	}

	public void setCohortDataCsv(String cohortDataCsv) {
		this.cohortDataCsv = cohortDataCsv;
	}

	public String getCohortDataCsvFile() {
		return cohortDataCsvFile;
	}

	public void setCohortDataCsvFile(String cohortDataCsvFile) {
		this.cohortDataCsvFile = cohortDataCsvFile;
	}

	public String getCohortCsvFile() {
		return cohortCsvFile;
	}

	public void setCohortCsvFile(String cohortCsvFile) {
		this.cohortCsvFile = cohortCsvFile;
	}

	public Set<String> getGenomicsTables() {
        return genomicsTables;
    }

    public void setGenomicsTables(Set<String> genomicsTables) {
        this.genomicsTables = genomicsTables;
    }

    public String getGenomicsTable() {
        return genomicsTable;
    }

    public void setGenomicsTable(String genomicsTable) {
        this.genomicsTable = genomicsTable;
    }

    public String getCohortDataXlsxFile() {
		return cohortDataXlsxFile;
	}

	public void setCohortDataXlsxFile(String cohortDataXlsxFile) {
		this.cohortDataXlsxFile = cohortDataXlsxFile;
	}

	public String getCohortXlsxFile() {
		return cohortXlsxFile;
	}

	public void setCohortXlsxFile(String cohortXlsxFile) {
		this.cohortXlsxFile = cohortXlsxFile;
	}

	public int getNumberOfSubjects() {
		return numberOfSubjects;
	}

	public void setNumberOfSubjects(int numberOfSubjects) {
		this.numberOfSubjects = numberOfSubjects;
	}

	public int getLowVisits() {
		return lowVisits;
	}

	public void setLowVisits(int lowVisits) {
		this.lowVisits = lowVisits;
	}

	public int getHighVisits() {
		return highVisits;
	}

	public void setHighVisits(int highVisits) {
		this.highVisits = highVisits;
	}

	public String getScriptOutputTextFile() {
		return scriptOutputTextFile;
	}

	public void setScriptOutputTextFile(String scriptOutputTextFile) {
		this.scriptOutputTextFile = scriptOutputTextFile;
	}

	public String getScriptOutputTextFileName() {
		return scriptOutputTextFileName;
	}

	public void setScriptOutputTextFileName(String scriptOutputTextFileName) {
		this.scriptOutputTextFileName = scriptOutputTextFileName;
	}

	public String getResultsXlsxFile() {
        return resultsXlsxFile;
    }

    public void setResultsXlsxFile(String resultsXlsxFile) {
        this.resultsXlsxFile = resultsXlsxFile;
    }

    public String getTempDir() {
		return tempDir;
	}

	public void setTempDir(String tempDir) {
		this.tempDir = tempDir;
	}

    public String getTimingFile() {
        return timingFile;
    }

    public void setTimingFile(String timingFile) {
        this.timingFile = timingFile;
    }

    public Date getScoresGeneratedTime() {
        return scoresGeneratedTime;
    }

    public void setScoresGeneratedTime(Date scoresGeneratedTime) {
        this.scoresGeneratedTime = scoresGeneratedTime;
    }

    public Date getCohortGeneratedTime() {
        return cohortGeneratedTime;
    }

    public void setCohortGeneratedTime(Date cohortGeneratedTime) {
        this.cohortGeneratedTime = cohortGeneratedTime;
    }

    public List<CfeResults> getDiscoveryCohortResultsList() {
        return discoveryCohortResultsList;
    }

    public void setDiscoveryCohortResultsList(List<CfeResults> discoveryCohortResultsList) {
        this.discoveryCohortResultsList = discoveryCohortResultsList;
    }

    public Long getDiscoveryId() {
        return discoveryId;
    }

    public void setDiscoveryId(Long discoveryId) {
        this.discoveryId = discoveryId;
    }

    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    public String getBigDataTempFileName() {
        return bigDataTempFileName;
    }

    public void setBigDataTempFileName(String bigDataTempFileName) {
        this.bigDataTempFileName = bigDataTempFileName;
    }

    public boolean getDebugDiscoveryScoring() {
        return debugDiscoveryScoring;
    }

    public void setDebugDiscoveryScoring(boolean debugDiscoveryScoring) {
        this.debugDiscoveryScoring = debugDiscoveryScoring;
    }

    public String getDiscoveryScoringCommand() {
        return discoveryScoringCommand;
    }

    public void setDiscoveryScoringCommand(String discoveryScoringCommand) {
        this.discoveryScoringCommand = discoveryScoringCommand;
    }

    /*
    public double getDePercentileScore1() {
        return dePercentileScore1;
    }

    public void setDePercentileScore1(double dePercentileScore1) {
        this.dePercentileScore1 = dePercentileScore1;
    }

    public double getDePercentileScore2() {
        return dePercentileScore2;
    }

    public void setDePercentileScore2(double dePercentileScore2) {
        this.dePercentileScore2 = dePercentileScore2;
    }

    public double getDePercentileScore3() {
        return dePercentileScore3;
    }

    public void setDePercentileScore3(double dePercentileScore3) {
        this.dePercentileScore3 = dePercentileScore3;
    }

    public double getDePercentileScore4() {
        return dePercentileScore4;
    }

    public void setDePercentileScore4(double dePercentileScore4) {
        this.dePercentileScore4 = dePercentileScore4;
    }
    */

    public File getDiscoveryCohortFile() {
        return discoveryCohortFile;
    }

    public void setDiscoveryCohortFile(File discoveryCohortFile) {
        this.discoveryCohortFile = discoveryCohortFile;
    }

    public String getDiscoveryCohortFileContentType() {
        return discoveryCohortFileContentType;
    }

    public void setDiscoveryCohortFileContentType(String discoveryCohortFileContentType) {
        this.discoveryCohortFileContentType = discoveryCohortFileContentType;
    }

    public String getDiscoveryCohortFileName() {
        return discoveryCohortFileName;
    }

    public void setDiscoveryCohortFileName(String discoveryCohortFileName) {
        this.discoveryCohortFileName = discoveryCohortFileName;
    }

    public double getDiscoveryCohortComparisonThreshold() {
        return discoveryCohortComparisonThreshold;
    }

    public void setDiscoveryCohortComparisonThreshold(double discoveryCohortComparisonThreshold) {
        this.discoveryCohortComparisonThreshold = discoveryCohortComparisonThreshold;
    }

    public PercentileScores getDiscoveryPercentileScores() {
        return discoveryPercentileScores;
    }

    public void setDiscoveryPercentileScores(PercentileScores discoveryPercentileScores) {
        this.discoveryPercentileScores = discoveryPercentileScores;
    }

}
