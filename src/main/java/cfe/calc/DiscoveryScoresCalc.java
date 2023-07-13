package cfe.calc;

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
import cfe.utils.GeneExpressionFile;
import cfe.utils.Util;
import cfe.utils.WebAppProperties;

public class DiscoveryScoresCalc {

	private static final long serialVersionUID = 1L;
	//private static final Log log = LogFactory.getLog(DiscoveryAction.class);
    private static Logger log = Logger.getLogger(DiscoveryScoresCalc.class.getName());

	private Map<String, Object> webSession;
	
	private File discoveryCohortFile;
	private String discoveryCohortFileContentType;
	private String discoveryCohortFileName;
	
	private File discoveryCsv;
	private String discoveryCsvContentType;
	private String discoveryCsvFileName;
	    
	//private File discoveryDb;
	//private String discoveryDbContentType;
	//private String discoveryDbFileName;
	
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
	
	private double discoveryPheneLowCutoff;
	private double discoveryPheneHighCutoff;
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

	
	
	/**
	 * Calculates the discovery results.
	 * 
	 * @return the status of this action.
	 * 
	 * @throws Exception
	 */
	public CfeResults calculate(
	        CfeResults discoveryCohortResults,
	        File discoveryCsv,
	        File probeSetMappingDb,
	        String probeSetMappingDbFileName,
	        String diagnosisCode,
	        PercentileScores discoveryPercentileScores,
	        boolean debugDiscoveryScoring
	) throws Exception {
	    

        
	    log.info("Discovery scores calculation phase started");
	    
	    CfeResults cfeResults = null;
	    
	    //try {
	        this.discoveryCsv          = discoveryCsv;
	        
	        this.probesetMappingDb         = probeSetMappingDb;
	        this.probesetMappingDbFileName = probeSetMappingDbFileName;
            
	        this.diagnosisCode         = diagnosisCode;
            this.discoveryPercentileScores = discoveryPercentileScores;
	        this.debugDiscoveryScoring = debugDiscoveryScoring;
	        
	        if (discoveryCohortResults == null) {
	            throw new Exception("No discovery cohort specified.");
	        }
	        
	        this.discoveryId = discoveryCohortResults.getCfeResultsId();
	        if (this.discoveryId == null) {
	            throw new Exception("No ID found for the discovery cohort results.");
	        }
	        
	        this.pheneSelection = discoveryCohortResults.getPhene();
	        if (pheneSelection == null || pheneSelection.isEmpty()) {
	            throw new Exception("No phene found in discovery cohort.");
	        }
	        
	        this.discoveryPheneLowCutoff  = discoveryCohortResults.getLowCutoff();
	        this.discoveryPheneHighCutoff = discoveryCohortResults.getHighCutoff();
	        
	        if (this.discoveryPercentileScores == null || this.discoveryPercentileScores.getScores().size() <= 0) {
	            String errorMessage = "No percentile scores were specified.";
	            log.severe(errorMessage);
	            throw new Exception(errorMessage);
	        }
	        
	        if (this.probesetMappingDb == null || this.probesetMappingDbFileName == null) {
	            String message = "No probeset to gene mapping database was specified.";
	            throw new Exception(message);
	        }
	        else if (!this.probesetMappingDbFileName.endsWith(".accdb")) {
	            String message = "Probeset to gene mapping database file \"" + probesetMappingDbFileName
	                    + "\" does not have MS Access database file extension \".accdb\".";
	            throw new Exception(message);
	        }
	        
	        // Get phene table name from the phene selection - the phene selection whould be "phene-table.phene-name"
	        this.pheneTable = "";
	        String[] pheneInfo = pheneSelection.split("\\.", 2);
	        if (pheneInfo.length >= 2) {
	            this.pheneTable = pheneInfo[0];
	        }
	        

            log.info("Diagnosis code: " + this.diagnosisCode);
            

            this.baseDir = WebAppProperties.getRootDir();

            this.scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
            this.scriptFile = new File(getClass().getResource("/R/DEdiscovery.R").toURI()).getAbsolutePath();

            this.tempDir = FileUtil.getTempDir();
            
            //---------------------------------------------------------------------
            // Create discovery cohort file that will be passed to the R script
            //---------------------------------------------------------------------
            XSSFWorkbook workbook = discoveryCohortResults.getResultsSpreadsheet();
            if (workbook == null) {
                String errorMessage = "Discovery cohort results do not contain a spreadsheet.";
                log.severe(errorMessage);
                throw new Exception(errorMessage);
            }
            
            XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT);
            if (sheet == null) {
                String errorMessage = "Discovery cohort results spreadsheet does not contain a \""
                        + CfeResultsSheets.DISCOVERY_COHORT + "\" sheet.";
                log.severe(errorMessage);
                throw new Exception(errorMessage);
            }
            
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
            if (sheet == null) {
                String errorMessage = "Discovery cohort results spreadsheet does not contain a \""
                        + CfeResultsSheets.COHORT_DATA + "\" sheet.";
                log.severe(errorMessage);
                throw new Exception(errorMessage);
            }
            
            cohortData.setKey(null);
            cohortData.initializeToWorkbookSheet(sheet);
            
            DataTable bigData = cohortData.getBigData(this.pheneSelection);
            
            File bigDataTmp = FileUtil.createTempFile("bigData-", ".csv");
            if (bigDataTmp != null) {
                FileUtils.writeStringToFile(bigDataTmp, bigData.toCsv(), "UTF-8");
            } else {
                throw new Exception("Could not create bigData temporary file.");
            }
            this.bigDataTempFileName = bigDataTmp.getAbsolutePath();
            

            //------------------------------------------------------
            // Get the gene expression CSV file
            //------------------------------------------------------
            GeneExpressionFile.checkFile(discoveryCsv);    // Check for errors
            
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
            rScriptCommand[8] = this.discoveryPheneLowCutoff + "";
            rScriptCommand[9] = this.discoveryPheneHighCutoff + "";
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
            
            this.scriptOutput = this.runCommand(rScriptCommand);
            
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
            XSSFWorkbook cohortWorkbook = discoveryCohortResults.getResultsSpreadsheet();
            if (cohortWorkbook == null) {
                String errorMessage = "Discovery cohort results does not contain a spreadsheet.";
                log.severe(errorMessage);
                throw new Exception(errorMessage);
            }
            
            // Create output data table from the output CSV file generated by the
            // Discovery R Script
            DataTable outputDataTable = new DataTable(null);
            if (outputFile == null) {
                String errorMessage = "No output file generated for discovery calculation.";
                throw new Exception(errorMessage);
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
            outputDataTable.deleteRows("Probe Set ID", "VisitNumber");
            
            
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
            cohortDataDataTable.setKey(null);
            streamingSheet = cohortWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
            cohortDataDataTable.initializeToWorkbookStreamingSheet(streamingSheet);            
            log.info("Cohort data data table created.");
            
            // Create "Discovery Cohort Info" data table
            DataTable discoveryCohortInfoDataTable = new DataTable(null);
            streamingSheet = cohortWorkbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT_INFO);
            discoveryCohortInfoDataTable.initializeToWorkbookStreamingSheet(streamingSheet);
            log.info("Discovery cohort info data table created.");
            
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
            cfeResults = new CfeResults(resultsWorkbook, CfeResultsType.DISCOVERY_SCORES,
                    this.scoresGeneratedTime, this.pheneSelection,
                    this.discoveryPheneLowCutoff, this.discoveryPheneHighCutoff);
            log.info("cfeResults object created.");
            
            cfeResults.copyAttributes(discoveryCohortResults);
            
            cfeResults.setDiscoveryRScriptLog(scriptOutput);
            log.info("Discovery scoring results object created.");
            
            // Add a file for the R script command
            cfeResults.addTextFile(CfeResultsFileType.DISCOVERY_R_SCRIPT_COMMAND, this.discoveryScoringCommand);           
            
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
	    //}
	    //catch (Exception exception) {
	    //    String errorMessage = "Discovery scoring failed: " + exception.getLocalizedMessage();
	    //    log.severe(errorMessage);
	    //    throw new Exception(errorMessage, exception);
	    //}

		return cfeResults;
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
        row.add("Probeset to Gene Mapping DB");
        row.add(this.probesetMappingDbFileName);
        infoTable.addRow(row);
        
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
        row.add("Diagnosis Code");
        row.add(this.diagnosisCode);
        infoTable.addRow(row);
        
        if (this.timingFile != null && !this.timingFile.isEmpty()) {
            DataTable timing = new DataTable(null);
            timing.initializeToCsv(timingFile);
            
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


	public double getDiscoveryPheneLowCutoff() {
        return discoveryPheneLowCutoff;
    }


    public void setDiscoveryPheneLowCutoff(double discoveryPheneLowCutoff) {
        this.discoveryPheneLowCutoff = discoveryPheneLowCutoff;
    }


    public double getDiscoveryPheneHighCutoff() {
        return discoveryPheneHighCutoff;
    }


    public void setDiscoveryPheneHighCutoff(double discoveryPheneHighCutoff) {
        this.discoveryPheneHighCutoff = discoveryPheneHighCutoff;
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
