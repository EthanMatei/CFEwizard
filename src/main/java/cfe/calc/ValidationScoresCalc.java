package cfe.calc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;

import cfe.model.CfeResults;
import cfe.model.CfeResultsFileType;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.services.CfeResultsService;
import cfe.utils.CsvUtil;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.WebAppProperties;

/**
 * Class for calculating validation scores.
 * 
 * @author Jim Mullen
 *
 */
public class ValidationScoresCalc {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ValidationScoresCalc.class.getName());

    private String errorMessage;
    
    private File geneExpressionCsv;
    //private String geneExpressionCsvContentType;
    private String geneExpressionCsvFileName;
	
	private List<CfeResults> validationCohorts;
    
    private Long validationDataId;	
	//private Long prioritizationId;

	private String validationMasterSheetFile;
	private String predictorListFile;

	private String phene;
	
	private Date scoresGeneratedTime;
	
	private double scoreCutoff = 6.0;
    private double comparisonThreshold = 0.0001;
	
	private List<String> genesNotFoundInPrioritization = new ArrayList<String>();
    private String validationScoringCommand;
    private String scriptOutput;
    private String scriptOutputFile;
    private String tempDir;
    
    private String validationOutputFile;
    private Long cfeResultsId;
    
    // Slashes and hyphens in predictors will cause an error in the R script, so these
    // characters are replaced with the following values
    public static final String PREDICTOR_SLASH_REPLACEMENT  = "88888";
    public static final String PREDICTOR_HYPHEN_REPLACEMENT = "77777";
	

    double bonferroniScore  = 6;
    double nominalScore     = 4;
    double stepwiseScore    = 2;
    double nonStepwiseScore = 0;

    private File updatedMasterSheet;
    private String updatedMasterSheetFileName;
    private String updatedMasterSheetTempFileName;  // Need to create temp file for R-script to use
    
    private File updatedPredictorList;
    private String updatedPredictorListFileName;
    private String updatedPredictorListTempFileName;


    public List<String> createValidationPredictorListAndMasterSheetFiles(
            Long validationCohortId,
            File geneExpressionCsvFile
    ) throws Exception {

        this.geneExpressionCsv = geneExpressionCsvFile;
        
        List<String> fileNames = new ArrayList<String>(2);
        
        log.info("Starting to create predictor list and master sheet for validation scoring.");

        if (validationCohortId == null) {
            throw new Exception("No validation cohort ID speified for validation predictor list creation.");
        }
        
        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

        DataTable predictorList = this.createPredictorList(validationCohortId);
        if (predictorList == null) {
            throw new Exception("Could not create validation predictor list.");
        }

        String predictorListCsv = predictorList.toCsv();
        File predictorListCsvTmp = FileUtil.createTempFile("validation-predictor-list-",  ".csv");
        if (predictorListCsv != null) {
            FileUtils.write(predictorListCsvTmp, predictorListCsv, "UTF-8");
        }
        String predictorListFileName = predictorListCsvTmp.getAbsolutePath();

        if (predictorListFileName == null || predictorListFileName.isEmpty()) {
            throw new Exception("Could not create validation predictor list file.");
        }
        log.info("Predictor List file in validation scoring specification: \"" + predictorListFileName + "\" created.");               

        fileNames.set(0,predictorListFileName);

        log.info("Starting to create master sheet for validation scoring.");

        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

        String validationMasterSheetFileName = this.createValidationMasterSheet(
                validationCohortId,
                predictorList,
                this.geneExpressionCsv
        );

        log.info("Master Sheet file name: " + validationMasterSheetFileName);

        if (validationMasterSheetFileName == null || validationMasterSheetFileName.isEmpty()) {
            throw new Exception("Could not create validation master sheet.");
        }
        
        fileNames.set(1, validationMasterSheetFileName);
        
        return fileNames; // [0] => predictorListFileName, [1] => masterSheetFileName
    }


     /**
	 * Calculates the validation results.
	 * 
	 * @return the status of this action.
	 * 
	 * @throws Exception
	 */
	public CfeResults calculateValidationScores(
	        CfeResults validationCohort,
	        double scoreCutoff,
	        double comparisonThreshold,
            double bonferroniScore,
            double nominalScore,
            double stepwiseScore,
            double nonStepwiseScore,
            String masterSheetFileName,
            String updatedMasterSheetFileName,
            String predictorListFileName,
            String updatedPredictorListFileName
    ) throws Exception {

	    log.info("Validation scoring phase started");

	    CfeResults cfeResults = null;

	    //--------------------------------------------------
	    // Assign member variables to parameter values
	    //--------------------------------------------------
	    this.scoreCutoff         = scoreCutoff;
	    this.comparisonThreshold = comparisonThreshold;
	    this.bonferroniScore     = bonferroniScore;
	    this.nominalScore        = nominalScore;
	    this.stepwiseScore       = stepwiseScore;
	    this.nonStepwiseScore    = nonStepwiseScore;
        
	    this.validationMasterSheetFile    = masterSheetFileName;
        this.updatedMasterSheetFileName   = updatedMasterSheetFileName;
        this.predictorListFile            = predictorListFileName;	    
        this.updatedPredictorListFileName = updatedPredictorListFileName;
        
        
	    try {
	        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

	        if (validationCohort == null) {
	            throw new Exception("No validation cohort specified.");    
	        }

	        this.validationDataId = validationCohort.getCfeResultsId();
	        if (this.validationDataId == null) {
	            throw new Exception("No ID found for the validation cohort.");
	        }

	        this.phene = validationCohort.getPhene();
	        if (this.phene == null || this.phene.isEmpty()) {
	            throw new Exception("No phene specified in validation cohort.");
	        }


	        if (this.validationMasterSheetFile == null || this.validationMasterSheetFile.isEmpty()) {
	            throw new Exception("No master sheet specified for validation scoring.");
	        }


	        if (this.predictorListFile == null || this.predictorListFile.isEmpty()) {
	            throw new Exception("No predictor list specified for validation scoring.");
	        }

	        log.info("Starting validation scoring");

	        String masterSheetArg   = this.validationMasterSheetFile;
	        String predictorListArg = this.predictorListFile;

	        //-------------------------------------------------------
	        // Check for updated master sheet and predictor list
	        //-------------------------------------------------------
	        if (this.updatedMasterSheetFileName != null && !this.updatedMasterSheetFileName.isEmpty()) {
	            // Updated master sheet provided
	            File updatedMasterSheetTempFile = FileUtil.createTempFile("validation-updated-master-sheet-", ".csv");
	            FileUtils.copyFile(this.updatedMasterSheet, updatedMasterSheetTempFile);
	            this.updatedMasterSheetTempFileName = updatedMasterSheetTempFile.getAbsolutePath();
	            masterSheetArg = this.updatedMasterSheetTempFileName;
	        }


	        if (this.updatedPredictorListFileName != null && !this.updatedPredictorListFileName.isEmpty()) {
	            // Updated predictor list provided
	            File updatedPredictorListTempFile = FileUtil.createTempFile("validation-updated-predictor-list-", ".csv");
	            FileUtils.copyFile(this.updatedPredictorList, updatedPredictorListTempFile);
	            this.updatedPredictorListTempFileName = updatedPredictorListTempFile.getAbsolutePath();
	            predictorListArg = this.updatedPredictorListTempFileName; 
	        }

	        String scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
	        String scriptFile = new File(getClass().getResource("/R/Validation.R").toURI()).getAbsolutePath();

	        if (scriptDir == null || scriptDir.isEmpty()) {
	            throw new Exception("The R script directory could not be determined for validation scoring.");    
	        }

	        if (scriptFile == null || scriptFile.isEmpty()) {
	            throw new Exception("The validation scoring script could not be located.");
	        }

	        this.tempDir = FileUtil.getTempDir();

	        // Get the validation data
	        CfeResults validationData = CfeResultsService.get(validationDataId);
	        if (validationData == null) {
	            throw new Exception("Could not find validation data with ID: " + validationDataId);    
	        }

	        DataTable cohortData = validationData.getSheetAsDataTable(CfeResultsSheets.COHORT_DATA, null);
	        Set<String> diagnosesSet = cohortData.getUniqueValues("DxCode");
	        String diagnoses = String.join(",", diagnosesSet);

	        Set<String> genderDiagnosesSet = cohortData.getUniqueCombinedValues("Gender(M/F)", "DxCode", "-");
	        String genderDiagnoses = String.join(",", genderDiagnosesSet);
	        log.info("Gender Diagnoses: " + genderDiagnoses);

	        // Create the R script command
	        String[] rScriptCommand = new String[7];
	        rScriptCommand[0] = WebAppProperties.getRscriptPath();    // Full path of the Rscript command
	        rScriptCommand[1] = scriptFile;     // The R script to run
	        rScriptCommand[2] = scriptDir;   // The directory that contains R scripts
	        rScriptCommand[3] = this.phene;
	        //rScriptCommand[4] = diagnoses;
	        //rScriptCommand[5] = genderDiagnoses;
	        rScriptCommand[4] = masterSheetArg;
	        rScriptCommand[5] = predictorListArg;
	        rScriptCommand[6] = this.tempDir;

	        this.validationScoringCommand = "\"" + String.join("\" \"",  rScriptCommand) + "\"";
	        log.info("Validation Scoring Command: " + this.validationScoringCommand);

	        this.scriptOutput = this.runCommand(rScriptCommand);

	        // Set generate time
	        this.scoresGeneratedTime = new Date();

	        File tempFile = FileUtil.createTempFile("validation-r-script-output", ".txt");
	        FileUtils.write(tempFile, scriptOutput, "UTF-8");
	        this.scriptOutputFile = tempFile.getAbsolutePath();

	        //---------------------------------------------------------------
	        // Get the Validation script output
	        //---------------------------------------------------------------
	        String validationFilePatternString = "Validation output file created: (.*)";

	        Pattern validationFilePattern = Pattern.compile(validationFilePatternString);

	        String lines[] = scriptOutput.split("\\r?\\n");
	        for (String line: lines) {
	            Matcher validationMatcher = validationFilePattern.matcher(line);

	            if (validationMatcher.find()) {
	                validationOutputFile = validationMatcher.group(1).trim();
	                log.info("Validation output file pattern found: \"" + validationOutputFile + "\".");
	            }             
	        }

	        if (validationOutputFile == null || validationOutputFile.isEmpty()) {
	            errorMessage = "Can't find output file from validation scoring R script.";
	            log.severe(errorMessage);
	            throw new Exception(errorMessage);
	        }

	        //--------------------------------------------
	        // Create results workbook
	        //--------------------------------------------
	        double lowCutoff  = validationData.getLowCutoff();
	        double highCutoff = validationData.getHighCutoff();

	        //CfeResults prioritizationData = CfeResultsService.get(this.prioritizationId);
	        //if (prioritizationData == null) {
	        //    throw new Exception("Could not find prioritization data with ID: " + prioritizationId);
	        //}

	        // Create validation scoring data table from the output CSV file
	        // generated by the Validation R Script
	        
	        DataTable validationScoringDataTable = new DataTable(null);
	        validationScoringDataTable.initializeToCsv(validationOutputFile);
	        log.info("Validation scoring data data has been created.");

	        String scoreColumn = "ValidationScore";
	        validationScoringDataTable.addColumn(scoreColumn, "");

	        for (int rowIndex = 0; rowIndex < validationScoringDataTable.getNumberOfRows(); rowIndex++) {
	            double validationScore = 0;
	            int stepwiseIndex = validationScoringDataTable.getColumnIndex("Stepwise.Test");
	            int pValueIndex   = validationScoringDataTable.getColumnIndex("ANOVA.p.value");

	            if (stepwiseIndex < 0) {
	                throw new Exception("Could not find stepwise column in validation scoring.");
	            }

	            if (pValueIndex < 0) {
	                throw new Exception("Could not find p-value column in validation scoring.");
	            }

	            String stepwise = validationScoringDataTable.getValue(rowIndex, stepwiseIndex);
	            String pValueString = validationScoringDataTable.getValue(rowIndex, pValueIndex);

	            if (!stepwise.equalsIgnoreCase("Stepwise")) {
	                validationScore = this.nonStepwiseScore;
	            }
	            else {
	                int numberOfBiomarkers = validationScoringDataTable.getNumberOfRows();
	                try {
	                    double pValue = Double.parseDouble(pValueString);
	                    if (pValue <= (0.05 / numberOfBiomarkers)) {
	                        validationScore = this.bonferroniScore;
	                    }
	                    else if (pValue <= 0.05) {
	                        validationScore = this.nominalScore;
	                    }
	                    else {
	                        validationScore = this.stepwiseScore;
	                    }
	                } catch (Exception exception) {
	                    log.warning("Unable to parse p-value \"" + pValueString + "\".");
	                    validationScore = this.stepwiseScore;
	                }
	            }

	            validationScoringDataTable.setValue(rowIndex, scoreColumn, validationScore + "");
	        }

	        DataTable validationScoresInfo = this.createValidationScoresInfoTable();

	        // Map from sheet name to data table
	        LinkedHashMap<String, DataTable> resultsTables = new LinkedHashMap<String, DataTable>();

	        resultsTables = validationData.getDataTables();
	        //resultsTables.putAll(prioritizationData.getDataTables());

	        resultsTables.put(CfeResultsSheets.VALIDATION_SCORES, validationScoringDataTable);
	        log.info("resultsTables created - size: " + resultsTables.size());

	        resultsTables.put(CfeResultsSheets.VALIDATION_SCORES_INFO, validationScoresInfo);

	        int rowAccessWindowSize = 100;
	        //Workbook resultsWorkbook = DataTable.createStreamingWorkbook(resultsTables, rowAccessWindowSize);
	        XSSFWorkbook resultsWorkbook = DataTable.createWorkbook(resultsTables);
	        log.info("resultsWorkbook created.");

	        // Save the results in the database
	        cfeResults = new CfeResults(
	                resultsWorkbook,
	                CfeResultsType.VALIDATION_SCORES,
	                this.scoresGeneratedTime, this.phene,
	                lowCutoff, highCutoff
	                );
	        log.info("cfeResults object created.");
	        log.info("CFE RESULTS: \n" + cfeResults.asString());

	        // Add files from input results
	        cfeResults.addCsvAndTextFiles(validationData);

	        // Add the validation R script command
	        cfeResults.addTextFile(CfeResultsFileType.VALIDATION_R_SCRIPT_COMMAND, this.validationScoringCommand);

	        // Add the validation R script log file
	        cfeResults.addTextFile(CfeResultsFileType.VALIDATION_R_SCRIPT_LOG, this.scriptOutput);

	        //----------------------------------------------------------
	        // Add the master sheet file
	        //----------------------------------------------------------
	        File file = new File(this.validationMasterSheetFile);
	        String masterSheetContents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
	        cfeResults.addCsvFile(CfeResultsFileType.VALIDATION_MASTER_SHEET, masterSheetContents);

	        //----------------------------------------------------------
	        // Add the predictor list file
	        //----------------------------------------------------------
	        File predictorFile = new File(this.predictorListFile);
	        String predictorListContents = FileUtils.readFileToString(predictorFile, StandardCharsets.UTF_8);
	        cfeResults.addCsvFile(CfeResultsFileType.VALIDATION_PREDICTOR_LIST, predictorListContents);

	        //---------------------------------------------------------------------
	        // Add the updated master sheet, if any.
	        // Also modify the results sheet for changes in the updated
	        // master sheet's ValCategory column.
	        //---------------------------------------------------------------------
	        if (this.updatedMasterSheet != null) {
	            String updatedMasterSheetContents = FileUtils.readFileToString(this.updatedMasterSheet, StandardCharsets.UTF_8);
	            cfeResults.addCsvFile(CfeResultsFileType.VALIDATION_UPDATED_MASTER_SHEET, updatedMasterSheetContents);

	            //-----------------------------------------------------------------------------------------------------------
	            // Update cohort data and validation cohort info based on ValCategory column of the master sheet
	            // Only include ValCategory of "Clinical" in the validation cohort, and the "Low" and "High" values
	            // may change, and the values for these should be placed in the Validation Cohort Info sheet (whether
	            // there is an updated master sheet or not - for no update case, can use the Discovery Cohort Info values
	            // (or calculate from generated master sheet?)
	            //-----------------------------------------------------------------------------------------------------------
	            String key = "Subject Idenitifers.PheneVisit";
	            DataTable updatedMasterSheetValues = new DataTable(key);
	            // Want all columns up to "Biomarkers"
	            List<String> header = Arrays.asList( CsvUtil.getHeader(updatedMasterSheetContents) );
	            int biomarkersIndex = header.indexOf("Biomarkers");
	            if (biomarkersIndex < 0) {
	                throw new Exception("Updated validation master sheet does not contain required column \"Biomarkers\".");
	            }
	            String[] columns = header.subList(0, biomarkersIndex).toArray(new String[0]);
	            updatedMasterSheetValues.initializeToCsvString(updatedMasterSheetContents, columns);

	            this.updateCohortData(cfeResults, updatedMasterSheetValues);
	        }

	        //--------------------------------------------------------
	        // Add the updated predictor list, if any
	        //--------------------------------------------------------
	        if (this.updatedPredictorList != null) {
	            String updatedPredictorListContents = FileUtils.readFileToString(this.updatedPredictorList, StandardCharsets.UTF_8);
	            cfeResults.addCsvFile(CfeResultsFileType.VALIDATION_UPDATED_PREDICTOR_LIST, updatedPredictorListContents);
	        }

	        CfeResultsService.save(cfeResults);
	        log.info("cfeResults object saved.");

	        this.cfeResultsId = cfeResults.getCfeResultsId();
	        if (this.cfeResultsId < 1) {
	            throw new Exception("Validation scoring results id is not >= 1: " + cfeResultsId);
	        }


	    }
	    catch (Exception exception) {
	        if (exception != null) {
	            String message = "Validation scoring failed: " + exception.getLocalizedMessage();
	            this.setErrorMessage(message);
	        }
	    }
	    return cfeResults;
	}

	
	
	public void updateCohortData(CfeResults cfeResults, DataTable updatedMasterSheet) throws Exception {
	    String dataTableKey = "Subject Identifiers.PheneVisit";

	    XSSFWorkbook workbook = cfeResults.getResultsSpreadsheet();

	    Map<String, DataTable> dataTables = DataTable.createDataTables(workbook);

	    DataTable cohortData = dataTables.get(CfeResultsSheets.COHORT_DATA);
	    cohortData.setKey(dataTableKey);

	    // Delete phene visits in cohort data that are not in the updated master sheet
	    // (unless they are in the discpvery cohort????)
	    for (String key: cohortData.getKeys()) {
	        if (!updatedMasterSheet.containsKey()) {
	            // cohortData.dele
	        }
	    }

	    // Make changes here ...
	    // Delete entries not in mastersheet
	    // Modify entries in mastersheet

	    dataTables.put(CfeResultsSheets.COHORT_DATA, cohortData);

	    workbook = DataTable.createWorkbook(dataTables);

	    cfeResults.setResultsSpreadsheet(workbook);
	}

	
	public String createValidationMasterSheet(Long validationDataId, DataTable predictorList, File geneExpressionCsvFile)
	        throws Exception
	{
	    ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
        
	    CfeResults cfeResults = CfeResultsService.get(validationDataId);
	    if (cfeResults == null) {
	        throw new Exception("Could not get saved results for ID " + validationDataId + ".");
	    }
	    
	    XSSFWorkbook workbook = cfeResults.getResultsSpreadsheet();
	    
	    if (workbook == null) {
	        throw new Exception("Unable to get results spreadsheet from database for results ID "
	            + validationDataId + ".");
	    }
	    
	    String key = "Subject Identifiers.PheneVisit";
	    
	    XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.VALIDATION_COHORT);
	    if (sheet == null) {
	        sheet = workbook.getSheet(CfeResultsSheets.CLINICAL_COHORT); // check for old deprecated name
	        if (sheet == null) {
	            throw new Exception("Could not find \"" + CfeResultsSheets.VALIDATION_COHORT + "\" sheet in results workbook.");
	        }
	    }
	    
	    DataTable masterSheet = new DataTable(key);
	    masterSheet.initializeToWorkbookSheet(sheet);
	    
	    masterSheet.deleteColumn("TestingCohort");
	    
	    // Create dx column (gender + diagnosis code)
	    masterSheet.insertColumn("dx", 9, "");
        for (int i = 0; i < masterSheet.getNumberOfRows(); i++) {
            String gender = masterSheet.getValue(i, "Gender(M/F)");
            String dxCode = masterSheet.getValue(i, "DxCode");
            masterSheet.setValue(i, "dx", gender + "-" + dxCode);
        }
        
        // Create Biomarkers column (contains pheneVisit as value)
	    masterSheet.addColumn("Biomarkers",  "");
	    for (int i = 0; i < masterSheet.getNumberOfRows(); i++) {
	        String pheneVisit = masterSheet.getValue(i, key);
	        masterSheet.setValue(i, "Biomarkers", pheneVisit);
	    }
	    
	    // Add predictor columns (combination of gene cards symbol, "biom" and pro)beset)
	    for (int i = 0; i < predictorList.getNumberOfRows(); i++) {
	        String predictor = predictorList.getValue(i, "Predictor");
	        masterSheet.addColumn(predictor, "");
	    }
	    
	    // Set "Validation Cohort" to 1 where "ValCategory" is "Low" or "High"
	    for (int rowIndex = 0; rowIndex < masterSheet.getNumberOfRows(); rowIndex++) {
	        String valCategory = masterSheet.getValue(rowIndex, "ValCategory");
	        if (valCategory.equalsIgnoreCase("Low") || valCategory.equalsIgnoreCase("High")) {
                masterSheet.setValue(rowIndex, "ValidationCohort", "1");
	        }
	    }
	       
        //---------------------------------------------------------------------------
        // Read in the gene expression CSV file. It has the following format:
        //
        // ID            <phene-visit> <phene-visit> <phene-visit>
        // <probeset>    <value>       <value>       <value>
        // <probeset>    <value>       <value>       <value>
        //---------------------------------------------------------------------------
	    FileReader filereader = new FileReader(geneExpressionCsv);
        CSVReader csvReader = new CSVReader(filereader);
        
        String[] header = csvReader.readNext();
        
        // Create map from probesets to predictors
        HashMap<String,String> probesetToPredictorMap = new HashMap<String,String>();
        List<String> columns = masterSheet.getColumnNames();
        for (String column: columns) {
            
            // If this is a predictor
            if (column.contains("biom")) {
                String predictor = column;
                predictor = predictor.replaceAll("/", PREDICTOR_SLASH_REPLACEMENT);
                predictor = predictor.replaceAll("-", PREDICTOR_HYPHEN_REPLACEMENT);
                
                String[] mapValues = column.split("biom");
                String probeset = mapValues[1];
                probeset = probeset.replaceAll("/", PREDICTOR_SLASH_REPLACEMENT);
                probeset = probeset.replaceAll("-", PREDICTOR_HYPHEN_REPLACEMENT);        
                
                probesetToPredictorMap.put(probeset,  predictor);
            }
        }
        
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            String probeset = row[0];
            probeset = probeset.replaceAll("/", PREDICTOR_SLASH_REPLACEMENT);
            probeset = probeset.replaceAll("-", PREDICTOR_HYPHEN_REPLACEMENT);
            
            String predictor = probesetToPredictorMap.get(probeset);

            if (predictor != null && !predictor.isEmpty()) {
                for (int i = 1; i < row.length; i++) {
                    String pheneVisit = header[i];
                    String value = row[i];
                    
                    predictor = predictor.replaceAll("/", PREDICTOR_SLASH_REPLACEMENT);
                    predictor = predictor.replaceAll("-", PREDICTOR_HYPHEN_REPLACEMENT);

                    // Set mastersheet values
                    //
                    // ... Biomarker     <gene>biom<probeset>  <gene>biom<probeset> ...
                    // ... <phene-visit> <value>               <value>
                    // ... <phene-visit> <value>               <value>
                    int rowIndex = masterSheet.getRowIndex("Biomarkers", pheneVisit);
                    if (rowIndex >= 0) {
                        masterSheet.setValue(rowIndex, predictor, value);    
                    }
                }
            }
        }
        csvReader.close();
        
	    //-----------------------------)--------------------------
	    // Write the master sheet to a CSV file
	    //-------------------------------------------------------
	    String masterSheetCsv = masterSheet.toCsv();
        File validationMasterSheetCsvTmp = FileUtil.createTempFile("validation-master-sheet-",  ".csv");
        if (masterSheetCsv != null) {
            FileUtils.write(validationMasterSheetCsvTmp, masterSheetCsv, "UTF-8");
        }
        else {
            throw new Exception("Unable to create validation mastersheet.");
        }
        
        return validationMasterSheetCsvTmp.getAbsolutePath();
	}

	
	public DataTable createPredictorList(Long validationDataId /*, Long prioritizationId*/) throws Exception
	{
	    ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

	    TreeMap<String,Double> prioritizationScores = this.getPrioritizationScores(validationDataId /* prioritizationId */);

	    
	    //----------------------------------------
	    // Get the discovery scores
	    //----------------------------------------
	    CfeResults discoveryScoresAndCohorts = CfeResultsService.get(validationDataId);
	    XSSFWorkbook workbook = discoveryScoresAndCohorts.getResultsSpreadsheet();
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_SCORES);
        
        DataTable discoveryScores = new DataTable("Probe Set ID");
        discoveryScores.initializeToWorkbookSheet(sheet);
        
        sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
        DataTable cohortData = new DataTable(null);
        cohortData.initializeToWorkbookSheet(sheet);
        Set<String> diagnoses = cohortData.getUniqueValues("DxCode");
	    
        
	    String key = "Predictor";

	    DataTable predictorList = new DataTable(key);
	    predictorList.addColumn(key, "");
	    predictorList.addColumn("Direction", "");
        predictorList.addColumn("Male", "");
        predictorList.addColumn("Female", "");
        
        // OLD (hard coded):
        //predictorList.addColumn("BP", "");
        //predictorList.addColumn("MDD", "");
        //predictorList.addColumn("SZ", "");
        //predictorList.addColumn("SZA", "");
        //predictorList.addColumn("PTSD", "");
        //predictorList.addColumn("PSYCH", "");
        //predictorList.addColumn("PSYCHOSIS", "");
        
        // NEW:
        for (String dx: diagnoses) {
            predictorList.addColumn(dx,  "");    
        }
        
        predictorList.addColumn("All", "");
        
        for (int i = 0; i < discoveryScores.getNumberOfRows(); i++) {
            double deScore = 0.0;
            double prioritizationScore = 0.0;
            double rawDeScore = 0.0;
            double dePercentile = 0.0;
            
            String gene = discoveryScores.getValue(i, "Genecards Symbol");
            String probeset = discoveryScores.getValue(i, "Probe Set ID");
            String rawDeScoreString = discoveryScores.getValue(i, "DE Raw Score");
            String dePercentileString = discoveryScores.getValue(i, "DE Percentile");
            String deScoreString = discoveryScores.getValue(i, "DE Score");
            
            try {
                rawDeScore = Double.parseDouble(rawDeScoreString);
            }
            catch (NumberFormatException exception) {
                rawDeScore = 0.0;
            }
            
            try {
                dePercentile = Double.parseDouble(dePercentileString);
            }
            catch (NumberFormatException exception) {
                dePercentile = 0.0;
            }
                        
            try {
                deScore = Double.parseDouble(deScoreString);
            }
            catch (NumberFormatException exception) {
                deScore = 0.0;
            }

            if (prioritizationScores.containsKey(gene)) {
                prioritizationScore = prioritizationScores.get(gene);
            }
            else {
                prioritizationScore = 0.0;
                this.genesNotFoundInPrioritization.add(gene);
            }

            double cfe2Score = deScore + prioritizationScore;
            if (dePercentile >= 0.3333333333 && cfe2Score >= (this.scoreCutoff - this.comparisonThreshold)) {
                String direction = "I";
                if (rawDeScore < 0.0) {
                    direction = "D";
                }

                ArrayList<String> row = new ArrayList<String>();

                String predictor = gene + "biom" + probeset;
                predictor = predictor.replaceAll("/", PREDICTOR_SLASH_REPLACEMENT);
                predictor = predictor.replaceAll("-", PREDICTOR_HYPHEN_REPLACEMENT);

                row.add(predictor);
                row.add(direction);
                row.add("0"); // Male
                row.add("0"); // Female
                
                for (String dx: diagnoses) {
                    row.add("0");
                }
                
                // OLD CODE (hard coded):
                // row.add("0"); // BP
                // row.add("0"); // MDD
                // row.add("0"); // SZ
                // row.add("0"); // SZA
                // row.add("0"); // PTSD
                // row.add("0"); // PSYCH
                // row.add("0"); // PSYCHOSIS

                row.add("1"); // All

                predictorList.addRow(row);
            }
        }
        
        return predictorList;
	}
	
    public TreeMap<String,Double> getPrioritizationScores(Long prioritizationId) throws Exception
    {
        TreeMap<String,Double> scores = new TreeMap<String, Double>(String.CASE_INSENSITIVE_ORDER);
        
        CfeResults cfeResults = CfeResultsService.get(prioritizationId);
        XSSFWorkbook workbook = cfeResults.getResultsSpreadsheet();
        
        String key = "Gene";
        
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.PRIORITIZATION_SCORES);
        DataTable cfgScoresSheet = new DataTable(key);
        cfgScoresSheet.initializeToWorkbookSheet(sheet);

        // Get scores
        for (int i = 0; i < cfgScoresSheet.getNumberOfRows(); i++) {
            String gene  = cfgScoresSheet.getValue(i, "Gene");
            String score = cfgScoresSheet.getValue(i, "Score");
            
            String[] geneList = gene.split(",");  // If list, change to single upper-case value
            gene = geneList[0].toUpperCase();
            
            Double scoreValue = 0.0;
            try {
                scoreValue = Double.parseDouble(score);
            } catch (NumberFormatException exception) {
                scoreValue = 0.0;
            }
            
            scores.put(gene, scoreValue);
        }

        return scores;
    }
	
	public DataTable createValidationScoresInfoTable() throws Exception {
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
        row.add("Score Cutoff");
        row.add(this.scoreCutoff + "");
        infoTable.addRow(row);

        row = new ArrayList<String>();
        row.add("Comparison Threshold");
        row.add(this.comparisonThreshold + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Bonferroni Score");
        row.add(this.bonferroniScore + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Nominal Score");
        row.add(this.nominalScore + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Stepwise Score");
        row.add(this.stepwiseScore + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Non-Stepwise Score");
        row.add(this.nonStepwiseScore + "");
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Gene Expression CSV File");
        row.add(this.geneExpressionCsvFileName);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Updated Master Sheet File");
        row.add(this.updatedMasterSheetFileName);
        infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("Updated Predictor List File");
        row.add(this.updatedPredictorListFileName);
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
		return output.toString();
	}
	
    public Long getCfeResultsId() {
        return cfeResultsId;
    }

    public void setCfeResultsId(Long cfeResultsId) {
        this.cfeResultsId = cfeResultsId;
    }

    public String getPhene() {
        return phene;
    }

    public void setPhene(String phene) {
        this.phene = phene;
    }
    
    public File getGeneExpressionCsv() {
        return geneExpressionCsv;
    }

    public void setGeneExpressionCsv(File geneExpressionCsv) {
        this.geneExpressionCsv = geneExpressionCsv;
    }

    public String getGeneExpressionCsvFileName() {
        return geneExpressionCsvFileName;
    }

    public void setGeneExpressionCsvFileName(String geneExpressionCsvFileName) {
        this.geneExpressionCsvFileName = geneExpressionCsvFileName;
    }

    //public List<CfeResults> getDiscoveryScores() {
    //    return discoveryScores;
    //}

    //public void setDiscoveryScores(List<CfeResults> discoveryScores) {
    //    this.discoveryScores = discoveryScores;
    //}

    //public List<CfeResults> getPrioritizationScores() {
    //    return prioritizationScores;
    //}

    //public void setPrioritizationScores(List<CfeResults> prioritizationScores) {
    //    this.prioritizationScores = prioritizationScores;
    //}

    public Long getValidationDataId() {
        return validationDataId;
    }

    public void setValidationDataId(Long validationDataId) {
        this.validationDataId = validationDataId;
    }

    //public Long getPrioritizationId() {
    //    return prioritizationId;
    //}

    //public void setPrioritizationId(Long prioritizationId) {
    //    this.prioritizationId = prioritizationId;
    //}

    public String getValidationMasterSheetFile() {
        return validationMasterSheetFile;
    }

    public void setValidationMasterSheetFile(String validationMasterSheetFile) {
        this.validationMasterSheetFile = validationMasterSheetFile;
    }

    public String getPredictorListFile() {
        return predictorListFile;
    }

    public void setPredictorListFile(String predictorListFile) {
        this.predictorListFile = predictorListFile;
    }

    public double getScoreCutoff() {
        return scoreCutoff;
    }

    public void setScoreCutoff(double scoreCutoff) {
        this.scoreCutoff = scoreCutoff;
    }

    public double getComparisonThreshold() {
        return comparisonThreshold;
    }

    public void setComparisonThreshold(double comparisonThreshold) {
        this.comparisonThreshold = comparisonThreshold;
    }

    public List<String> getGenesNotFoundInPrioritization() {
        return genesNotFoundInPrioritization;
    }

    public void setGenesNotFoundInPrioritization(List<String> genesNotFoundInPrioritization) {
        this.genesNotFoundInPrioritization = genesNotFoundInPrioritization;
    }

    public String getValidationScoringCommand() {
        return validationScoringCommand;
    }

    public void setValidationScoringCommand(String validationScoringCommand) {
        this.validationScoringCommand = validationScoringCommand;
    }

    public String getScriptOutputFile() {
        return scriptOutputFile;
    }

    public void setScriptOutputFile(String scriptOutputFile) {
        this.scriptOutputFile = scriptOutputFile;
    }

    public String getScriptOutput() {
        return scriptOutput;
    }

    public void setScriptOutput(String scriptOutput) {
        this.scriptOutput = scriptOutput;
    }

    public String getTempDir() {
        return tempDir;
    }

    public void setTempDir(String tempDir) {
        this.tempDir = tempDir;
    }

    public String getValidationOutputFile() {
        return validationOutputFile;
    }

    public void setValidationOutputFile(String validationOutputFile) {
        this.validationOutputFile = validationOutputFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Date getScoresGeneratedTime() {
        return scoresGeneratedTime;
    }

    public void setScoresGeneratedTime(Date scoresGeneratedTime) {
        this.scoresGeneratedTime = scoresGeneratedTime;
    }

    public double getBonferroniScore() {
        return bonferroniScore;
    }

    public void setBonferroniScore(double bonferroniScore) {
        this.bonferroniScore = bonferroniScore;
    }

    public double getNominalScore() {
        return nominalScore;
    }

    public void setNominalScore(double nominalScore) {
        this.nominalScore = nominalScore;
    }

    public double getStepwiseScore() {
        return stepwiseScore;
    }

    public void setStepwiseScore(double stepwiseScore) {
        this.stepwiseScore = stepwiseScore;
    }

    public double getNonStepwiseScore() {
        return nonStepwiseScore;
    }

    public void setNonStepwiseScore(double nonStepwiseScore) {
        this.nonStepwiseScore = nonStepwiseScore;
    }

    public List<CfeResults> getValidationCohorts() {
        return validationCohorts;
    }

    public void setValidationCohorts(List<CfeResults> validationCohorts) {
        this.validationCohorts = validationCohorts;
    }

    public File getUpdatedMasterSheet() {
        return updatedMasterSheet;
    }

    public void setUpdatedMasterSheet(File updatedMasterSheet) {
        this.updatedMasterSheet = updatedMasterSheet;
    }

    public String getUpdatedMasterSheetFileName() {
        return updatedMasterSheetFileName;
    }

    public void setUpdatedMasterSheetFileName(String updatedMasterSheetFileName) {
        this.updatedMasterSheetFileName = updatedMasterSheetFileName;
    }

    public File getUpdatedPredictorList() {
        return updatedPredictorList;
    }

    public void setUpdatedPredictorList(File updatedPredictorList) {
        this.updatedPredictorList = updatedPredictorList;
    }

    public String getUpdatedPredictorListFileName() {
        return updatedPredictorListFileName;
    }

    public void setUpdatedPredictorListFileName(String updatedPredictorListFileName) {
        this.updatedPredictorListFileName = updatedPredictorListFileName;
    }

}
