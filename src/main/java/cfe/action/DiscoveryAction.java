package cfe.action;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;
import com.mysql.cj.x.protobuf.MysqlxDatatypes.Array;

import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.parser.PheneVisitParser;
import cfe.parser.ProbesetMappingParser;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.CohortTable;
import cfe.utils.ColumnInfo;
import cfe.utils.DataTable;
import cfe.utils.WebAppProperties;
import cfe.utils.WorkbookUtil;

public class DiscoveryAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(DiscoveryAction.class);

	private Map<String, Object> webSession;
	
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
	
	private int lowCutoff;
	private int highCutoff;
	
	private String cohortDataCsv;
	private String cohortDataCsvFile;
	private String cohortDataXlsxFile;
	
	private String cohortCsvFile;
	private String cohortXlsxFile;
	
	private Set<String> microarrayTables;
	private String microarrayTable;
	
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
	
	private String errorMessage;
	
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
		    this.errorMessage = "No discovery database was specified.";
		    result = ERROR;
		}
		else if (!this.discoveryDbFileName.endsWith(".accdb")) {
		    this.errorMessage = "Discovery database file \"" + discoveryDbFileName
		            + "\" does not have MS Access database file extension \".accdb\".";
		    result = ERROR;
		}
		else {
		    try {
			    // Copy the upload files to temporary files, because the upload files get deleted
			    // and they are needed beyond this method
			    File discoveryDbTmp = File.createTempFile("discovery-db-", ".accdb");
			    FileUtils.copyFile(this.discoveryDb, discoveryDbTmp);
			    this.discoveryDbTempFileName = discoveryDbTmp.getAbsolutePath();
			
			    this.dbFileName = discoveryDb.getAbsolutePath();

			    DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(this.discoveryDbTempFileName);
			
	            dbParser.checkCoreTables();
	         
			    this.pheneTables = dbParser.getPheneTables();

			    this.phenes = dbParser.getTableColumnMap();
			
			    this.microarrayTables = dbParser.getMicroarrayTables();
		    } catch (Exception exception) {
		        this.errorMessage = "The Discovery database could not be processed. " + exception.getLocalizedMessage();
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
				Table pheneData          = db.getTable(this.pheneTable);
				Table chips              = db.getTable(this.microarrayTable);

				CohortDataTable subjectIdentifiersData = new CohortDataTable();
				subjectIdentifiersData.initializeToAccessTable(subjectIdentifiers);

				CohortDataTable demographicsData = new CohortDataTable();
				demographicsData.initializeToAccessTable(demographics);

				CohortDataTable diagnosisData = new CohortDataTable();
				diagnosisData.initializeToAccessTable(diagnosis);

				CohortDataTable pheneDataData = new CohortDataTable(this.pheneTable);
				pheneDataData.initializeToAccessTable(pheneData);

				CohortDataTable chipsData = new CohortDataTable();
				chipsData.initializeToAccessTable(chips);

				CohortDataTable cohortData = subjectIdentifiersData.merge(demographicsData);
				
				cohortData = cohortData.merge(diagnosisData);
				cohortData = cohortData.merge(pheneDataData);
				cohortData = cohortData.merge(chipsData);
				
				cohortData.enhance(pheneSelection, lowCutoff, highCutoff);
				
				// How to add a column (name, position, value):
				//cohortData.addColumnBefore("testColumn", 2, "123");
				
				// NO LONGER NEEDED ???
				//this.cohortDataCsv = cohortData.toCsv();

				// NO LONGER NEEDED ???
				// Create an Xlsx (spreadsheet) version of the cohort data
				//File cohortDataXlsxTempFile = File.createTempFile("cohort-data-", ".xlsx");
				//FileOutputStream out = new FileOutputStream(cohortDataXlsxTempFile);
				//cohortData.toXlsx().write(out);
				//out.close();
				//this.cohortDataXlsxFile = cohortDataXlsxTempFile.getAbsolutePath();

				//File cohortDataCsvTempFile = File.createTempFile("cohort-data-", ".csv");
				//FileUtils.writeStringToFile(cohortDataCsvTempFile, cohortDataCsv, "UTF-8");
				//this.cohortDataCsvFile = cohortDataCsvTempFile.getAbsolutePath();

				db.close();
				
				// Delete the temporary database file
				File file = new File(this.discoveryDbTempFileName);
				file.delete();

				//-------------------------------------------
				// Create cohort and cohort CSV file
				//-------------------------------------------
				CohortTable cohort = cohortData.getDiscoveryCohort(pheneSelection, lowCutoff, highCutoff);
				cohort.sort("Subject", "PheneVisit"); 
                this.cohortGeneratedTime = new Date();
				
				this.numberOfSubjects = cohort.getNumberOfSubjects();
				this.lowVisits = cohort.getLowVisits();
				this.highVisits = cohort.getHighVisits();
				
				// NO LONGER NEEDED ???
				// String cohortCsv = cohort.toCsv();
                //
				// File cohortCsvTempFile = File.createTempFile("cohort-", ".csv");
				// FileUtils.writeStringToFile(cohortCsvTempFile, cohortCsv, "UTF-8");
				// this.cohortCsvFile = cohortCsvTempFile.getAbsolutePath();

				// Create an Xlsx (spreadsheet) version of the cohort data
				ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
                
				DataTable infoTable = this.createCohortInfoTable();
				
				LinkedHashMap<String, DataTable> cohortTables = new LinkedHashMap<String, DataTable>();
				cohortTables.put(CfeResultsSheets.DISCOVERY_COHORT,  cohort);
				cohortTables.put(CfeResultsSheets.COHORT_DATA, cohortData);
				cohortTables.put(CfeResultsSheets.DISCOVERY_COHORT_INFO, infoTable);
				
				XSSFWorkbook cohortWorkbook = DataTable.createWorkbook(cohortTables);
				cohortData.enhanceCohortDataSheet(cohortWorkbook, "cohort data", pheneSelection, lowCutoff, highCutoff);
                  
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
                CfeResultsService.save(cfeResults);
                this.cfeResultsId = cfeResults.getCfeResultsId();
			} catch (Exception exception) {
				result = ERROR;
				log.error("*** ERROR: " + exception.getMessage());
				errorMessage = exception.getLocalizedMessage();
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
	        this.discoveryCohortResultsList =
	                CfeResultsService.getMetadata(CfeResultsType.DISCOVERY_COHORT);
	    }
	    
	    return result;
	}
	
	public String discoveryScoringSpecification() throws Exception {
	    String result = SUCCESS;
	    
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else if (discoveryId == null) {
            this.errorMessage = "No discovery cohort selected.";
            result = ERROR;
        }
        else {
            try {
                ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

                CfeResults results = CfeResultsService.get(this.discoveryId);
                XSSFWorkbook workbook = results.getResultsSpreadsheet();
                
                this.lowCutoff      = results.getLowCutoff();
                this.highCutoff     = results.getHighCutoff();
                this.pheneSelection = results.getPhene(); 
                
                // Get the phene table from the cohort info sheet
                XSSFSheet discoveryCohortInfoSheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO);
                DataTable cohortInfo = new DataTable("attribute");
                cohortInfo.initializeToWorkbookSheet(discoveryCohortInfoSheet);
                
                ArrayList<String> row = cohortInfo.getRow("Phene Table");
                if (row == null) {
                    throw new Exception("Unable to find Phene Table row in sheet \""
                            + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".");
                }
                this.pheneTable = row.get(1);
                if (this.pheneTable == null || this.pheneTable.isEmpty()) {
                    throw new Exception("Could not get phene table information from workbook sheet \""
                            + CfeResultsSheets.DISCOVERY_COHORT_INFO + "\".");
                }
                
                // Get "Microarray Table" (todo...)
                
                // Get diagnosis codes
                XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
                DataTable diagnosisData = new DataTable("DxCode");
                diagnosisData.initializeToWorkbookSheet(sheet);

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
                this.errorMessage = exception.getLocalizedMessage();
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
        //    this.errorMessage = "Phene Visit database file \"" + discoveryDbFileName
        //            + "\" is not a \".accdb\" (MS Access) file.";
        //    result = INPUT;
        //}		
		else if (this.discoveryCsv == null || this.discoveryCsvFileName == null) {
	        this.errorMessage = "No gene expression CSV file was specified.";
	        result = INPUT;
	    }
	    else if (!this.discoveryCsvFileName.endsWith(".csv")) {
	        this.errorMessage = "Gene expression file \"" + discoveryCsvFileName
	                + "\" is not a \".csv\" file.";
	        result = INPUT;
	    }
	    else if (this.probesetMappingDb == null || this.probesetMappingDbFileName == null) {
	        this.errorMessage = "No probeset to gene mapping database file was specified.";
	        result = INPUT;
	    }
	    else if (!this.probesetMappingDbFileName.endsWith(".accdb")) {
	        this.errorMessage = "Probeset to gene mapping database file \"" + probesetMappingDbFileName
	                + "\" is not a \".accdb\" (MS Access) file.";
	        result = INPUT;
	    }
	    else {
            try {
                log.info("Starting Discovery calculation");
                log.info("Diagnosis code: " + this.diagnosisCode);
                this.baseDir = WebAppProperties.getRootDir();

                this.scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
                this.scriptFile = new File(getClass().getResource("/R/DEdiscovery.R").toURI()).getAbsolutePath();

                this.tempDir = System.getProperty("java.io.tmpdir");
                
                //--------------------------------------------------------------
                // Get the discovery cohort file
                //--------------------------------------------------------------
                CfeResults results = CfeResultsService.get(this.discoveryId);
                XSSFWorkbook workbook = results.getResultsSpreadsheet();
                
                XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT);
                DataTable discoveryCohort = new DataTable("PheneVisit");
                discoveryCohort.initializeToWorkbookSheet(sheet);
                String cohortCsv = discoveryCohort.toCsv();

                File cohortCsvTempFile = File.createTempFile("cohort-", ".csv");
                FileUtils.writeStringToFile(cohortCsvTempFile, cohortCsv, "UTF-8");
                this.cohortCsvFile = cohortCsvTempFile.getAbsolutePath();
                
                //----------------------------------------------------
                // Get bigData
                //----------------------------------------------------
                CohortDataTable cohortData = new CohortDataTable(this.pheneTable);
                sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
                cohortData.initializeToWorkbookSheet(sheet);
                
                DataTable bigData = cohortData.getBigData(this.pheneSelection);
                
                File bigDataTmp = File.createTempFile("bigData-", ".csv");
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
                File discoveryCsvTmp = File.createTempFile("discovery-csv-",  ".csv");
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
                
                scriptOutput = this.runCommand(rScriptCommand);
                
                this.scoresGeneratedTime = new Date();

                //-------------------------------------------------------
                // Set up script output file log
                //-------------------------------------------------------
                //File scriptOutputTextTempFile = File.createTempFile("discovery-r-script-output-", ".txt");
                //FileOutputStream out = new FileOutputStream(scriptOutputTextTempFile);
                //PrintWriter writer = new PrintWriter(out);
                //writer.write(scriptOutput);
                //writer.close();
                //this.scriptOutputTextFile = scriptOutputTextTempFile.getAbsolutePath();
                //this.scriptOutputTextFileName = "script-output.txt";
                //log.info("script output text file: " + scriptOutputTextFile);

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
                    }

                    if (reportMatcher.find()) {
                        reportFile = reportMatcher.group(1).trim();
                    }

                    if (timingMatcher.find()) {
                        timingFile = timingMatcher.group(1).trim();
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
                    errorMessage = "No output file generated for discovery calculation.";
                    result = ERROR;
                }
                else {
                    outputDataTable.initializeToCsv(outputFile);
                    // Add Genecards Symbols
                    outputDataTable.addColumn("DE Percentile", "");
                    outputDataTable.addColumn("DE Score", "");
                    outputDataTable.addColumn(ProbesetMappingParser.GENECARDS_SYMBOL_COLUMN, "");
                    outputDataTable.setColumnName(0, ProbesetMappingParser.PROBE_SET_ID_COLUMN);
                
                    this.deScoring(outputDataTable);  // calculate percentiles and scores
                
                    for (int rowIndex = 0; rowIndex < outputDataTable.getNumberOfRows(); rowIndex++) {
                        String keyValue = outputDataTable.getValue(rowIndex, 0);
                        String genecardsSymbol = probesetMapping.getValue(keyValue, ProbesetMappingParser.GENECARDS_SYMBOL_COLUMN);
                        outputDataTable.setValue(rowIndex, ProbesetMappingParser.GENECARDS_SYMBOL_COLUMN, genecardsSymbol);
                    }
                }

                // Create "Discovery Report" data table
                DataTable reportDataTable = new DataTable(null);
                if (reportFile != null && !reportFile.isEmpty()) {
                    reportDataTable.initializeToCsv(reportFile);
                }

                // Create "Discovery Scores Info" data table
                DataTable discoveryScoresInfoDataTable = this.createDiscoveryScoresInfoTable();
                
                // Create "Discovery Cohort" data table
                DataTable cohortDataTable = new DataTable(null);
                sheet = cohortWorkbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT);
                cohortDataTable.initializeToWorkbookSheet(sheet);

                // Create "Cohort Data" data table
                CohortDataTable cohortDataDataTable = new CohortDataTable();
                sheet = cohortWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
                cohortDataDataTable.initializeToWorkbookSheet(sheet);            

                // Create "Discovery Cohort Info" data table
                DataTable discoveryCohortInfoDataTable = new DataTable(null);
                sheet = cohortWorkbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO);
                discoveryCohortInfoDataTable.initializeToWorkbookSheet(sheet);
                
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
                resultsTables.put(CfeResultsSheets.DISCOVERY_COHORT, cohortDataTable);
                resultsTables.put(CfeResultsSheets.DISCOVERY_COHORT_INFO, discoveryCohortInfoDataTable);
                resultsTables.put(CfeResultsSheets.COHORT_DATA, cohortDataDataTable);
                
                XSSFWorkbook resultsWorkbook = DataTable.createWorkbook(resultsTables);
                cohortDataDataTable.enhanceCohortDataSheet(resultsWorkbook, "cohort data", pheneSelection, lowCutoff, highCutoff);
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
                
                
                cfeResults.setDiscoveryRScriptLog(scriptOutput);
               
                CfeResultsService.save(cfeResults);
                this.cfeResultsId = cfeResults.getCfeResultsId();
                log.info("**************** DISCOVERY CALCULATE - CFE RESULTS ID: " + cfeResultsId);
                
                //--------------------------------
                // Clean up temporary files
                //--------------------------------
                File file;
                
                // R script input files
                file = new File(this.cohortCsvFile);
                file.delete();
                
                file = new File(this.discoveryCsvTempFileName);
                file.delete();
                
                file = new File(this.bigDataTempFileName);
                file.delete();
                
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
	        String score = rowMap.get("DEscores");
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
	    
	    for (int rowNum = 0; rowNum < scoring.getNumberOfRows(); rowNum++) {
	        Map<String, String> rowMap = scoring.getRowMap(rowNum);
	        String score = rowMap.get("DEscores");
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
                
                int deScore = 0;
                
                if (dePercentile < 0.333333333333) {
                    deScore = 0;
                }
                else if (dePercentile < 0.50) {
                    deScore = 1;
                }
                else if (dePercentile < 0.80) {
                    deScore = 2;
                }
                else {
                    deScore = 4;
                }
                
                scoring.setValue(rowNum, "DE Score", deScore + "");
	        }
	        catch (NumberFormatException exception) {
	            ;  // no score - skip
	        }
	    }
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
	
    public DataTable createCohortInfoTable()
    {
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
		row.add(this.microarrayTable);
		infoTable.addRow(row);
		
		return infoTable;
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
        
        log.info("*** Before process start");
        Process process = processBuilder.start();


	    BufferedReader reader = new BufferedReader(
	    new InputStreamReader(process.getInputStream()));

	    String line;
	    while ((line = reader.readLine()) != null) {
	        output.append(line + "\n");
	    }

	    log.info("*** Going to wait for process...");
	    int status = process.waitFor();
	    if (status != 0) {
            //throw new Exception("Command \"" + command + "\" exited with code " + status);
	    }
	    
	    reader.close();
        log.info("*** reader closed");
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

	public int getLowCutoff() {
		return lowCutoff;
	}

	public void setLowCutoff(int lowCutoff) {
		this.lowCutoff = lowCutoff;
	}

	public int getHighCutoff() {
		return highCutoff;
	}

	public void setHighCutoff(int highCutoff) {
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

	public Set<String> getMicroarrayTables() {
		return microarrayTables;
	}

	public void setMicroarrayTables(Set<String> microarrayTables) {
		this.microarrayTables = microarrayTables;
	}

	public String getMicroarrayTable() {
		return microarrayTable;
	}

	public void setMicroarrayTable(String microarrayTable) {
		this.microarrayTable = microarrayTable;
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

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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

}
