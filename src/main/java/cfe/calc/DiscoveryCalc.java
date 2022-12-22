package cfe.calc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Table;

import cfe.model.CfeResults;
import cfe.model.CfeResultsFileType;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.PercentileScores;
import cfe.model.VersionNumber;
import cfe.parser.DiscoveryDatabaseParser;
import cfe.parser.ProbesetMappingParser;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.CohortTable;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.WebAppProperties;

public class DiscoveryCalc {
    
    private static final Logger log = Logger.getLogger(DiscoveryCalc.class.getName());
    
    public static CfeResults createDiscoveryCohort(
            String testingDatabaseFileName,
            String pheneTable,
            String phene,
            double pheneLowCutoff,
            double pheneHighCutoff,
            double comparisonThreshold,
            String genomicsTable) throws Exception {
        
        if (genomicsTable == null || genomicsTable.trim().isEmpty()) {
            String message = "No genomics table specified for discovery cohort creation.";
            throw new Exception(message);
        }
        else if (pheneTable == null || pheneTable.trim().isEmpty()) {
            String message = "No phene table specified for discovery cohort creation.";
            throw new Exception(message);
        }
        
        String pheneSelection = pheneTable + "." + phene;
        
        DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(testingDatabaseFileName);
        
        //-------------------------------------------------
        // Create the cohort data table
        //-------------------------------------------------
        Database db = dbParser.getDatabase();

        Table subjectIdentifiers = db.getTable("Subject Identifiers");
        Table demographics       = db.getTable("Demographics");
        Table diagnosis          = db.getTable("Diagnosis");
        //Table pheneData          = db.getTable(this.pheneTable);
        Table chips              = db.getTable(genomicsTable);
        
        if (chips == null) {
            String message = "Genomcis table \"" + genomicsTable + "\" not found in the testing database.";
            throw new Exception(message);
        }

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
        String csv1 = cohortData.toCsv();
        File cfile1 = FileUtil.createTempFile("cohort-data-1-", ".csv");
        FileUtils.writeStringToFile(cfile1, csv1, "UTF-8");
        
        log.info("chip data merged.");
        
        cohortData.enhance(pheneSelection, pheneLowCutoff, pheneHighCutoff, comparisonThreshold);

        // DEBUG
        // String csv2 = cohortData.toCsv();
        // File cfile2 = FileUtil.createTempFile("cohort-data-2-", ".csv");
        // FileUtils.writeStringToFile(cfile2, csv2, "UTF-8");
        
        log.info("Cohort data enhanced.");

        db.close();
        
        log.info("Database closed.");
        
        // Delete the temporary database file
        File file = new File(testingDatabaseFileName);
        file.delete();

        //-------------------------------------------
        // Create cohort and cohort CSV file
        //-------------------------------------------
        log.info("Discovery cohort phene selection: \"" + pheneSelection + "\".");
        CohortTable cohort = cohortData.getDiscoveryCohort(pheneSelection, pheneLowCutoff, pheneHighCutoff, comparisonThreshold);
        cohort.sort("Subject", "PheneVisit"); 
        Date cohortGeneratedTime = new Date();
        
        // DEBUG
        //String csv3 = cohortData.toCsv();
        //File cfile3 = FileUtil.createTempFile("cohort-data-3-", ".csv");
        //FileUtils.writeStringToFile(cfile3, csv3, "UTF-8");

        
        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

        
        int numberOfSubjects = cohort.getNumberOfSubjects();
        int lowVisits = cohort.getLowVisits();
        int highVisits = cohort.getHighVisits();
        
        DataTable infoTable = createCohortInfoTable(
                    testingDatabaseFileName,
                    pheneTable,
                    pheneSelection,
                    pheneLowCutoff,
                    pheneHighCutoff,
                    genomicsTable,
                    numberOfSubjects,
                    lowVisits,
                    highVisits,
                    cohortGeneratedTime
                );
        
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
                cohortGeneratedTime, 
                pheneSelection, pheneLowCutoff, pheneHighCutoff);
        
        // Save all the discovery cohort data files
        cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT, cohort.toCsv());
        cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT_DATA, cohortData.toCsv());
        cfeResults.addCsvFile(CfeResultsFileType.DISCOVERY_COHORT_INFO, infoTable.toCsv());
        
        CfeResultsService.save(cfeResults);
        
        return cfeResults;
    }
    
    public static DataTable createCohortInfoTable(
            String testingDatabaseFileName,
            String pheneTable,
            String phene,
            double pheneLowCutoff,
            double pheneHighCutoff,
            String genomicsTable,
            int numberOfSubjects,
            int lowVisits,
            int highVisits,
            Date cohortGeneratedTime
    ) throws Exception {
        DataTable infoTable = new DataTable("attribute");
        infoTable.insertColumn("attribute", 0, "");
        infoTable.insertColumn("value",  1,  "");
        
        ArrayList<String> row = new ArrayList<String>();
        row.add("CFE Version");
        row.add(VersionNumber.VERSION_NUMBER);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Time Cohort Generated");
        row.add(cohortGeneratedTime.toString());
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Test Database");
        row.add(testingDatabaseFileName);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Phene Table");
        row.add(pheneTable);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Phene");
        row.add(phene);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Low Cutoff");
        row.add(pheneLowCutoff + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("High Cutoff");
        row.add(pheneHighCutoff + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Microarray Table");
        row.add(genomicsTable);
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of Cohort Subjects");
        row.add(numberOfSubjects + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Number of Cohort Low Visits");
        row.add(lowVisits + "");
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of Cohort High Visits");
        row.add(highVisits + "");
        infoTable.addRow(row);          
        return infoTable;
    }
        
    
    /* =================================================================================================== */
    
    public static CfeResults calculateScores(
            Long discoveryCohortId,
            File geneExpressionCsvFile,
            String probesetMappingDbFileName,
            String pheneTable,
            String phene,
            double pheneLowCutoff,
            double pheneHighCutoff,
            String diagnosisCode,
            PercentileScores percentileScores,
            String scriptDir,
            String scriptFile,
            boolean debug
        ) throws Exception {
        log.info("Discovery calculation phase started");
        
        CfeResults cfeResults = null;
        
        if (geneExpressionCsvFile == null) {
            String message = "No gene expression CSV file was specified for Discovery scores calculation.";
            throw new Exception(message);
        }
        else if (probesetMappingDbFileName == null || probesetMappingDbFileName.trim().isEmpty()) {
            String message = "No probeset to gene mapping database file was specified for Discovery scores calculation.";
            throw new Exception(message);
        }
        else {
            log.info("Starting Discovery calculation");
            log.info("Diagnosis code: " + diagnosisCode);

            String pheneSelection = pheneTable + "." + phene;

            String baseDir = WebAppProperties.getRootDir();

            //this.tempDir = System.getProperty("java.io.tmpdir");
            String tempDir = FileUtil.getTempDir();

            //---------------------------------------------------------------------
            // Create discovery cohort file that will be passed to the R script
            //---------------------------------------------------------------------
            CfeResults results = CfeResultsService.get(discoveryCohortId);
            XSSFWorkbook workbook = results.getResultsSpreadsheet();

            XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_COHORT);
            DataTable discoveryCohort = new DataTable("PheneVisit");

            discoveryCohort.initializeToWorkbookSheet(sheet);
            String cohortCsv = discoveryCohort.toCsv();

            File cohortCsvTempFile = FileUtil.createTempFile("cohort-", ".csv");
            FileUtils.writeStringToFile(cohortCsvTempFile, cohortCsv, "UTF-8");
            String cohortCsvFile = cohortCsvTempFile.getAbsolutePath();

            //----------------------------------------------------
            // Get bigData
            //----------------------------------------------------
            CohortDataTable cohortData = new CohortDataTable(pheneTable);
            sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
            cohortData.initializeToWorkbookSheet(sheet);

            DataTable bigData = cohortData.getBigData(pheneSelection);

            File bigDataTmp = FileUtil.createTempFile("bigData-", ".csv");
            if (bigDataTmp != null) {
                FileUtils.writeStringToFile(bigDataTmp, bigData.toCsv(), "UTF-8");
            } else {
                throw new Exception("Could not create bigData temporary file.");
            }
            String bigDataTempFileName = bigDataTmp.getAbsolutePath();

            //------------------------------------------------------
            // Get the gene expression CSV file
            //------------------------------------------------------
            File geneExpressionCsvTmp = FileUtil.createTempFile("discovery-gene-expression-",  ".csv");
            if (geneExpressionCsvFile != null) {
                FileUtils.copyFile(geneExpressionCsvFile, geneExpressionCsvTmp);
            }
            String geneExpressionCsvTempFileName = geneExpressionCsvTmp.getAbsolutePath();

            //------------------------------------------------------------
            // Get the probeset to mapping information
            //------------------------------------------------------------
            String key = "Probe Set ID";
            DataTable probesetMapping = new DataTable(key);

            ProbesetMappingParser dbParser = new ProbesetMappingParser(probesetMappingDbFileName);
            Table table = dbParser.getMappingTable();
            probesetMapping.initializeToAccessTable(table);

            //---------------------------------------------
            // Process diagnosis code
            //---------------------------------------------
            if (diagnosisCode == null) diagnosisCode = "";

            diagnosisCode = diagnosisCode.trim();
            if (diagnosisCode.equals("") || diagnosisCode.equalsIgnoreCase("ALL")) {
                diagnosisCode = "All";
            }

            //---------------------------------------
            // Create R script command
            //---------------------------------------
            String[] rScriptCommand = new String[12];
            rScriptCommand[0] = WebAppProperties.getRscriptPath();    // Full path of the Rscript command
            rScriptCommand[1] = scriptFile;     // The R script to run
            rScriptCommand[2] = scriptDir;
            rScriptCommand[3] = cohortCsvFile;   // Change - name of cohort CSV File
            rScriptCommand[4] = diagnosisCode;
            //rScriptCommand[5] = this.discoveryDbTempFileName;
            rScriptCommand[5] = geneExpressionCsvTempFileName;
            rScriptCommand[6] = pheneSelection;
            rScriptCommand[7] = pheneTable;
            rScriptCommand[8] = pheneLowCutoff + "";
            rScriptCommand[9] = pheneHighCutoff + "";
            rScriptCommand[10] = tempDir;
            rScriptCommand[11] = bigDataTempFileName;

            // Log a version of the command used for debugging (OUT OF DATE - needs to be fixed or removed)
            //String logRScriptCommand = WebAppProperties.getRscriptPath() + " " + scriptFile 
            //        + " " + scriptDir 
            //        + " " + "\"" + this.cohortCsvFile + "\"" + " " + "\"" + this.diagnosisCode + "\"" 
            //        + " \"" + discoveryDbFileName + "\" \"" + discoveryCsvFileName + "\"" + this.pheneSelection;
            //log.info("LOG RSCRIPT COMMAND: " + logRScriptCommand);
            
            String discoveryScoringCommand = "\"" + String.join("\" \"",  rScriptCommand) + "\"";
            log.info("RSCRIPT COMMAND: " + discoveryScoringCommand);
            
            String scriptOutput = CalcUtil.runCommand(rScriptCommand);

            log.info("Returned from DEdiscovery.R script");

            Date scoresGeneratedTime = new Date();

            //-------------------------------------------------------
            // Set up script output file log
            //-------------------------------------------------------
            if (debug) {
                File scriptOutputTextTempFile = FileUtil.createTempFile("discovery-r-script-output-", ".txt");
                FileOutputStream out = new FileOutputStream(scriptOutputTextTempFile);
                PrintWriter writer = new PrintWriter(out);
                writer.write(scriptOutput);
                writer.close();
                String scriptOutputTextFile = scriptOutputTextTempFile.getAbsolutePath();
                //this.scriptOutputTextFileName = "script-output.txt";
                log.info("script output text file: " + scriptOutputTextFile);
            }

            //---------------------------------------------------------------
            // Get the output, report and timing file paths
            //---------------------------------------------------------------
            String outputFile = null;
            String reportFile = null;
            String timingFile = null;

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

            CfeResults discoveryCohortResults = CfeResultsService.get(discoveryCohortId);
            XSSFWorkbook cohortWorkbook = discoveryCohortResults.getResultsSpreadsheet();

            // Create output data table from the output CSV file generated by the
            // Discovery R Script
            DataTable outputDataTable = new DataTable(null);
            if (outputFile == null) {
                String errorMessage = "No output file generated for Discovery scores calculation.";
                log.severe(errorMessage);
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
                deScoring(outputDataTable, percentileScores);  // calculate percentiles and scores
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

            double dePercentileScore1 = percentileScores.getScores().get(0);
            double dePercentileScore2 = percentileScores.getScores().get(1);
            double dePercentileScore3 = percentileScores.getScores().get(2);
            double dePercentileScore4 = percentileScores.getScores().get(3);

            // Create "Discovery Scores Info" data table
            DataTable discoveryScoresInfoDataTable = createDiscoveryScoresInfoTable(
                    scoresGeneratedTime,
                    dePercentileScore1,
                    dePercentileScore2,
                    dePercentileScore3,
                    dePercentileScore4,
                    diagnosisCode,
                    timingFile                        
                    );

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

            int rowAccessWindowSize = 100;
            Workbook resultsWorkbook = DataTable.createStreamingWorkbook(resultsTables, rowAccessWindowSize);
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
                    scoresGeneratedTime, pheneSelection,
                    pheneLowCutoff, pheneHighCutoff);
            log.info("cfeResults object created.");


            cfeResults.setDiscoveryRScriptLog(scriptOutput);
            log.info("Discovery scoring results object created.");


            // Add files for the R script command and log
            cfeResults.addTextFile(CfeResultsFileType.DISCOVERY_R_SCRIPT_COMMAND, discoveryScoringCommand);
            cfeResults.addTextFile(CfeResultsFileType.DISCOVERY_R_SCRIPT_LOG, scriptOutput);

            CfeResultsService.save(cfeResults);
            log.info("Discovery scoring results object saved.");

            Long cfeResultsId = cfeResults.getCfeResultsId();
            log.info("Discovery calculation - CFE Results ID: " + cfeResultsId);

            //--------------------------------
            // Clean up temporary files
            //--------------------------------
            File file;

            // R script input files
            if (debug) {
                file = new File(cohortCsvFile);
                file.delete();

                file = new File(geneExpressionCsvTempFileName);
                file.delete();

                file = new File(bigDataTempFileName);
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

        return cfeResults;
    }

    public static DataTable createDiscoveryScoresInfoTable(
            Date scoresGeneratedTime,
            double dePercentileScore1,
            double dePercentileScore2,
            double dePercentileScore3,
            double dePercentileScore4,
            String diagnosisCode,
            String timingFileName
            ) throws Exception {

        DataTable infoTable = new DataTable("attribute");
        infoTable.insertColumn("attribute", 0, "");
        infoTable.insertColumn("value",  1,  "");

        ArrayList<String> row = new ArrayList<String>();
        row.add("CFE Version");
        row.add(VersionNumber.VERSION_NUMBER);
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Time Scores Generated");
        row.add(scoresGeneratedTime.toString());
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("");
        row.add("");
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.00 <= x < 0.33)");
        row.add("" + dePercentileScore1);
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.33 <= x < 0.50)");
        row.add("" + dePercentileScore2);
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.50 <= x < 0.80)");
        row.add("" + dePercentileScore3);
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("DE Percentile Score (0.80 <= x < 1.00)");
        row.add("" + dePercentileScore4);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("");
        row.add("");
        infoTable.addRow(row);               
        
        row = new ArrayList<String>();
        row.add("Diagnosis Code");
        row.add(diagnosisCode);
        infoTable.addRow(row);
        
        if (timingFileName != null && !timingFileName.isEmpty()) {
            DataTable timing = new DataTable(null);
            timing.initializeToCsv(timingFileName);
            
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
    
    public static void deScoring(DataTable scoring, PercentileScores percentileScores) throws Exception {
        Double negativeMax = null;
        Double positiveMax = null;
        
        double dePercentileScore1 = percentileScores.getScores().get(0);
        double dePercentileScore2 = percentileScores.getScores().get(1);
        double dePercentileScore3 = percentileScores.getScores().get(2);
        double dePercentileScore4 = percentileScores.getScores().get(3);
        
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

                    if (dePercentile < 0.333333333333) {
                        deScore = dePercentileScore1;
                    }
                    else if (dePercentile < 0.50) {
                        deScore = dePercentileScore2;
                    }
                    else if (dePercentile < 0.80) {
                        deScore = dePercentileScore3;
                    }
                    else {
                        deScore = dePercentileScore4;
                    }

                    scoring.setValue(rowNum, "DE Score", deScore + "");
                }
                catch (NumberFormatException exception) {
                    ;  // no score - skip
                }
            }
        }
    }
    
}
