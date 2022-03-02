package cfe.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;

import com.opencsv.CSVReader;

import cfe.enums.StudyType;
import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.services.CfeResultsService;
import cfe.utils.Authorization;
import cfe.utils.CohortDataTable;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.WebAppProperties;


public class TestingScoringAction extends BaseAction implements SessionAware {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(TestingScoringAction.class.getName());
	
	private static final String END_OF_PHENES_MARKER   = "END_OF_PHENES";
	private static final String START_OF_PHENES_MARKER = "START_OF_PHENES";
	
	public static final String CROSS_SECTIONAL = "cross-sectional";
	public static final String LONGITUDINAL    = "longitudinal";

	private Map<String, Object> webSession;

    private File geneExpressionCsv;
    private String geneExpressionCsvContentType;
    private String geneExpressionCsvFileName;

    private File specialPredictorListCsv;
    private String specialPredictorListCsvContentType;
    private String specialPredictorListCsvFileName;
    
    private String specialPredictorListTempFile;
    
	private Long testingDataId;
	private List<CfeResults> cfeResults;
	
	private String predictorListFile;
	private String testingMasterSheetFile;
	   
	private CfeResults testingData;
    private String scriptDir;
    private String scriptFile;
    
    private List<String> genesNotFoundInPrioritization = new ArrayList<String>();
    private String scriptOutput;
    private String tempDir;
    private String testingScoringCommand;
    
    private double scoreCutoff = 8.0;
    
    private ArrayList<String> phenes;

    
    private boolean state;
    private boolean stateCrossSectional;
    private boolean stateLongitudinal;
    
    private String predictionPhene;
    private Integer predictionPheneHighCutoff;

    private boolean firstYear;
    private boolean firstYearCrossSectional;
    private boolean firstYearLongitudinal;

    private boolean future;
    private boolean futureCrossSectional;
    private boolean futuretLongitudinal;
    
    private String finalMasterSheetFile;

	/**
	 * Select testing data
	 * @return
	 * @throws Exception
	 */
	public String selectTestingData() throws Exception {
	    String result = SUCCESS;
	    
	    if (!Authorization.isAdmin(webSession)) {
	        result = LOGIN;
	    } else {
	        this.cfeResults = CfeResultsService.getMetadata(
	                CfeResultsType.ALL_COHORTS_PLUS_DISCOVERY_SCORES,
	                CfeResultsType.ALL_COHORTS_PLUS_VALIDATION_SCORES
	            );
	    }
	    
	    return result;
	}
	
	public String specifyTestingScoringOptions() throws Exception {
	    String result = SUCCESS;
	    
        if (!Authorization.isAdmin(webSession)) {
            result = LOGIN;
        }
        else if (testingDataId == null) {
            this.setErrorMessage("No testing data selected.");
            result = INPUT;
        }
        else if (this.geneExpressionCsv == null || this.geneExpressionCsvFileName == null) {
            this.setErrorMessage("No gene expression CSV file specified.");
            result = INPUT;
        }
        else if (!this.geneExpressionCsvFileName.endsWith(".csv")) {
            this.setErrorMessage("This gene expression file \"" + this.geneExpressionCsvFileName + "\" is not a .csv file.");
            result = INPUT;
        }
        else {
            try {
                this.specialPredictorListTempFile = "";
                if (this.specialPredictorListCsvFileName != null && this.specialPredictorListCsvFileName != "") {
                    File tempFile = FileUtil.createTempFile("testing-special-predictor-list-", ".csv");
                    FileUtils.copyFile(this.getSpecialPredictorListCsv(), tempFile);
                    this.specialPredictorListTempFile = tempFile.getAbsolutePath();
                }

                testingData = CfeResultsService.get(testingDataId);
                if (testingData == null) {
                    throw new Exception("Unable to retrieve testing data for ID " + testingDataId + ".");
                }

                XSSFWorkbook workbook = testingData.getResultsSpreadsheet();
                if (workbook == null) {
                    throw new Exception("Unable to retrieve resulta workbook for testing data ID " + testingDataId + ".");
                }

                XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
                if (sheet == null) {
                    String message = "Could not find sheet \"" + CfeResultsSheets.COHORT_DATA + "\""
                                    + " for testing scoring data workbook.";
                    throw new Exception(message);
                }
                
                CohortDataTable cohortData = new CohortDataTable();
                cohortData.initializeToWorkbookSheet(sheet);
                cohortData.setKey("Subject Identifiers.PheneVisit");

                phenes = new ArrayList<String>();
                phenes.add("");
                phenes.addAll(cohortData.getPheneList());

                //--------------------------------------------
                // Create predictor list
                //--------------------------------------------
                DataTable predictorList = this.createPredictorList(this.testingDataId);
                if (predictorList == null) {
                    throw new Exception("Could not create validation predictor list.");
                }

                String predictorListCsv = predictorList.toCsv();
                File predictorListCsvTmp = FileUtil.createTempFile("predictor-list-",  ".csv");
                if (predictorListCsv != null) {
                    FileUtils.write(predictorListCsvTmp, predictorListCsv, "UTF-8");
                }
                this.predictorListFile = predictorListCsvTmp.getAbsolutePath();

                if (this.predictorListFile == null || this.predictorListFile.isEmpty()) {
                    throw new Exception("Could not create validation predictor list file.");
                }
                log.info("Predictor List file in testing scoring specification: \"" + predictorListFile + "\" created.");

                //--------------------------------------------------------------------
                // Create testing master sheet
                //--------------------------------------------------------------------
                this.testingMasterSheetFile = this.createTestingMasterSheet(
                        this.testingDataId,
                        predictorList,
                        this.geneExpressionCsv
                        );

                log.info("Master Sheet file name: " + this.testingMasterSheetFile);

                if (this.testingMasterSheetFile == null || this.testingMasterSheetFile.isEmpty()) {
                    throw new Exception("Could not create testing master sheet.");
                }
            }
            catch (Exception exception) {
                result = ERROR;
                if (exception != null) {
                    this.setErrorMessage("Testing scoring specification failed: " + exception.getLocalizedMessage());
                    String stackTrace = ExceptionUtils.getStackTrace(exception);
                    this.setExceptionStack(stackTrace);
                }
            }
        }

        return result;
	}
	
	/**
	 * Calculates the testing results.
	 * 
	 * @return the status of this action.
	 * 
	 * @throws Exception
	 */
	public String calculateTestingScores() throws Exception {
		String result = SUCCESS;
		
		log.info("Testing scoring phase started");
		
		if (!Authorization.isAdmin(webSession)) {
			result = LOGIN;
		}
        else if (testingDataId == null) {
            this.setErrorMessage("No testing data selected.");
            result = INPUT;
        }
        else if (testingMasterSheetFile == null || testingMasterSheetFile.trim().isEmpty()) {
            this.setErrorMessage("No master sheet found.");
            result = INPUT;
        }
	    else {

            try {                
                log.info("Starting testing scoring");
                
                DataTable masterSheet = new DataTable("Subject Identifiers.PheneVisit");
                masterSheet.initializeToCsv(testingMasterSheetFile);
                
                int startIndex = masterSheet.getColumnIndex(START_OF_PHENES_MARKER);
                int endIndex   = masterSheet.getColumnIndex(END_OF_PHENES_MARKER);
                
                // Delete unneeded phene columns
                for (int i = endIndex; i >= startIndex; i--) {
                    if (predictionPhene != null && !predictionPhene.isEmpty()
                            && predictionPhene.equals(masterSheet.getColumnName(i))) {
                        ; // keep this column
                    }
                    else {
                        masterSheet.deleteColumn(i);
                    }
                }
                
                String csv = masterSheet.toCsv();
                File tempFile = FileUtil.createTempFile("final-master-sheet", ".csv");
                FileUtils.write(tempFile,  csv, "UTF-8");
                this.finalMasterSheetFile = tempFile.getAbsolutePath();
                
                /*
                testingData = CfeResultsService.get(testingDataId);
                if (testingData == null) {
                    throw new Exception("Unable to retrieve testing data for ID " + testingDataId + ".");
                }
                
                XSSFWorkbook workbook = testingData.getResultsSpreadsheet();
                if (workbook == null) {
                    String message = "Unable to retrieve resulta workbook for testing data ID "
                        + testingDataId + ".";
                    throw new Exception(message);
                }                
                
                XSSFSheet testingCohortDataSheet = workbook.getSheet(CfeResultsSheets.TESTING_COHORT_DATA);
                if (testingCohortDataSheet == null) {
                    String message = "Could not find sheet \"" + CfeResultsSheets.TESTING_COHORT_DATA
                            + "\" in testing data results.";
                    throw new Exception(message);
                }
                */
                
                if (this.state && this.stateCrossSectional) {
                    
                }
                
                if (this.state && this.stateLongitudinal) {
                    
                }
                
                if (this.firstYear && this.firstYearCrossSectional) {
                    
                }
                
                if (this.firstYear && this.firstYearLongitudinal) {
                    
                }
                
                if (this.future && this.futureCrossSectional) {
                    
                }
                
                if (this.future && this.futuretLongitudinal) {
                    
                }

            }
            catch (Exception exception) {
                result = ERROR;
                if (exception != null) {
                    this.setErrorMessage("Testing scoring failed: " + exception.getLocalizedMessage());
                    String stackTrace = ExceptionUtils.getStackTrace(exception);
                    this.setExceptionStack(stackTrace);
                }
            }
        }

		return result;
	}
	
    public String createTestingMasterSheet(Long testingDataId, DataTable predictorList, File geneExpressionCsvFile)
            throws Exception
    {
        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included
        
        // Get the results from the previous pipeline step
        CfeResults cfeResults = CfeResultsService.get(testingDataId);
        if (cfeResults == null) {
            throw new Exception("Could not get saved results for ID " + testingDataId + ".");
        }
        
        XSSFWorkbook workbook = cfeResults.getResultsSpreadsheet();
        if (workbook == null) {
            throw new Exception("Unable to get results spreadsheet from database for results ID "
                + testingDataId + ".");
        }
        
        String key = "Subject Identifiers.PheneVisit";
        
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.TESTING_COHORT_DATA);
        if (sheet == null) {
            throw new Exception("Could not find \"" + CfeResultsSheets.TESTING_COHORT_DATA + "\" sheet in results workbook.");
        }
        
        DataTable masterSheetDataTable = new DataTable(key);
        masterSheetDataTable.initializeToWorkbookSheet(sheet);
        
        log.info("Initial master sheet data table number of rows: " + masterSheetDataTable.getNumberOfRows());
        log.info("Initial master sheet data table number of columns: " + masterSheetDataTable.getNumberOfColumns());
        
        masterSheetDataTable.deleteColumn(0);
        
        masterSheetDataTable.moveColumn("Subject", 0);
        
        masterSheetDataTable.moveColumn("Subject Identifiers.PheneVisit", 1);
        
        masterSheetDataTable.moveColumn("AffyVisit", 2);
        
        masterSheetDataTable.moveColumn("Visit Date", 3);
        
        masterSheetDataTable.moveColumn("Demographics.PheneVisit", 4);
        
        masterSheetDataTable.insertColumn("dx", 5, "");
        for (int rowIndex = 0; rowIndex < masterSheetDataTable.getNumberOfRows(); rowIndex++) {
            String value = masterSheetDataTable.getValue(rowIndex, "DxCode")
                    + "-" + masterSheetDataTable.getValue(rowIndex, "Gender(M/F)");
            masterSheetDataTable.setValue(rowIndex, "dx", value);
        }
        
        masterSheetDataTable.moveColumn("Gender(M/F)", 6);
        masterSheetDataTable.moveColumn("Age at testing (Years)", 7);
        masterSheetDataTable.moveColumn("DxCode", 8);
        masterSheetDataTable.moveColumn("HospitalizationCohort", 9);
        masterSheetDataTable.insertColumn("Hospitalization.VisitNumber", 10, "");
        masterSheetDataTable.moveColumn("HospFreq", 11);

        masterSheetDataTable.moveColumn("First Year Cohort", 12);
        masterSheetDataTable.renameColumn("First Year Cohort", "FirstYearCohort");
        masterSheetDataTable.insertColumn("FirstYear.VisitNumber", 13, "");
        masterSheetDataTable.moveColumn("FirstYearScore", 14);
        masterSheetDataTable.moveColumn("time", 15);
        
        masterSheetDataTable.moveColumn("TestCohort", 16); 
        masterSheetDataTable.insertColumn("Test.VisitNumber", 17, "");
        masterSheetDataTable.insertColumn(START_OF_PHENES_MARKER, 18,  "");
        
        String[] unneededColumns = {"SubjectID copy", "Hospitalizations Follow-up Database.SubjectID",
                "TestingDate", "Last Note", "Date Order", "PheneVisit Date", "first - last",
                "Time Future", "Length of Follow up for Future", "yes/no", "duped phene", "Score", "First Date",
                "Number of all future Hospitilzation", "VisitNumber", "Cohort",
                "Validation", "ValCategory", "ValidationCohort",
                "TestingCohort", "Vet/Non-Vet?", "inpt or lab CHIP Examiner",
                "Checker+ Date", "Comments on check", "COVID positive?", "COVID vaccine date",
                "Blood draw&Meal time", "Age at Onset of Illness",
                "Race/Ethnicity", "Diagnosis.PheneVisit", "Primary DIGS DX",
                "Specifiers", "Per Chip", "Confidence Rating", "DIGS Rater", "Other Dx",
                "TestingVisit", "inpt or lab", "CHIP Examiner"
        };
        
        // Delete last column, which is microarray data table column
        int lastColumnIndex = masterSheetDataTable.getNumberOfColumns() - 1;
        String lastColumnName = masterSheetDataTable.getColumnName(lastColumnIndex);
        masterSheetDataTable.renameColumn(lastColumnName, END_OF_PHENES_MARKER);
        
        for (String column: unneededColumns) {
            masterSheetDataTable.deleteColumnIfExists(column);
        }
        
        //---------------------------------------------------------
        // Set the visit numbers
        //---------------------------------------------------------
        masterSheetDataTable.sort("Subject", "Visit Date");
        String previousSubject = "";
        int testVisitNumber            = 0;
        int firstYearVisitNumber       = 0;
        int hospitalizationVisitNumber = 0;
        
        for (int i = 0; i < masterSheetDataTable.getNumberOfRows(); i++) {
            String subject = masterSheetDataTable.getValue(i, "Subject");
            if (!subject.equals(previousSubject)) {
                previousSubject = subject;
                String testCohort            = masterSheetDataTable.getValue(i, "TestCohort");
                String firstYearCohort       = masterSheetDataTable.getValue(i, "FirstYearCohort");
                String hospitalizationCohort = masterSheetDataTable.getValue(i, "HospitalizationCohort");
                
                if (testCohort.equals("1")) {
                    testVisitNumber = 1;
                }
                else {
                    testVisitNumber = 101;
                }
                
                if (firstYearCohort.equals("1")) {
                    firstYearVisitNumber = 1;
                }
                else {
                    firstYearVisitNumber = 101;
                }
                
                if (hospitalizationCohort.equals("1")) {
                    hospitalizationVisitNumber = 1;
                }
                else {
                    hospitalizationVisitNumber = 101;
                }
            }
            else {
                testVisitNumber++;
                firstYearVisitNumber++;
                hospitalizationVisitNumber++;
            }
            
            masterSheetDataTable.setValue(i, "Hospitalization.VisitNumber", hospitalizationVisitNumber + "");
            masterSheetDataTable.setValue(i, "FirstYear.VisitNumber", firstYearVisitNumber + "");
            masterSheetDataTable.setValue(i, "Test.VisitNumber", testVisitNumber + "");
        }
        
        
        
        // Add predictor columns (combination of gene cards symbol, "biom" and pro)beset)
        for (int i = 0; i < predictorList.getNumberOfRows(); i++) {
            String predictor = predictorList.getValue(i, "Predictor");
            masterSheetDataTable.addColumn(predictor, "");
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
        List<String> columns = masterSheetDataTable.getColumnNames();
        
        for (String column: columns) {
            
            // If this is a predictor
            if (column.contains("biom")) {
                String predictor = column;
                predictor = predictor.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
                predictor = predictor.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);
                
                String[] mapValues = column.split("biom");
                String probeset = mapValues[1];
                probeset = probeset.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
                probeset = probeset.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);        
                
                probesetToPredictorMap.put(probeset,  predictor);
            }
        }
        
      
        String[] row;
        while ((row = csvReader.readNext()) != null) {
            String probeset = row[0];
            probeset = probeset.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
            probeset = probeset.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);
            
            String predictor = probesetToPredictorMap.get(probeset);

            if (predictor != null && !predictor.isEmpty()) {
                for (int i = 1; i < row.length; i++) {
                    String pheneVisit = header[i];
                    String value = row[i];
                    
                    predictor = predictor.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
                    predictor = predictor.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);

                    // Set mastersheet values
                    //
                    // ... Biomarker     <gene>biom<probeset>  <gene>biom<probeset> ...
                    // ... <phene-visit> <value>               <value>
                    // ... <phene-visit> <value>               <value>
                    int rowIndex = masterSheetDataTable.getRowIndex("Subject Identifiers.PheneVisit", pheneVisit);
                    if (rowIndex >= 0) {
                        masterSheetDataTable.setValue(rowIndex, predictor, value);    
                    }
                }
            }
        }
        csvReader.close();
        
        //-----------------------------)--------------------------
        // Write the master sheet to a CSV file
        //-------------------------------------------------------
        String masterSheetCsv = masterSheetDataTable.toCsv();
        File testingMasterSheetCsvTmp = FileUtil.createTempFile("testing-master-sheet-",  ".csv");
        if (masterSheetCsv != null) {
            FileUtils.write(testingMasterSheetCsvTmp, masterSheetCsv, "UTF-8");
        }
        else {
            throw new Exception("Unable to create testing master sheet.");
        }
        
        return testingMasterSheetCsvTmp.getAbsolutePath();
    }

    
    public DataTable createPredictorList(Long dataId) throws Exception
    {
        ZipSecureFile.setMinInflateRatio(0.001);   // Get an error if this is not included

        CfeResults data = CfeResultsService.get(dataId);
        if (data == null) {
            String message = "Could not find CFE results for ID " + dataId + " for creating testing predictor list.";
            log.severe(message);
            throw new Exception(message);
        }
        
        XSSFWorkbook workbook = data.getResultsSpreadsheet();
        if (workbook == null) {
            String message = "Could not find results workbook for CFE results with ID " + dataId + ".";
            log.severe(message);
            throw new Exception(message);
        }
        
        Map<String,Double> prioritizationScores = this.getPrioritizationScores(workbook);
        
        //----------------------------------------
        // Get the discovery scores
        //----------------------------------------
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.DISCOVERY_SCORES);
        if (sheet == null) {
            String message = "Could not find sheet \"" + CfeResultsSheets.DISCOVERY_SCORES
                    + "\" in data for testing scoring.";
            log.severe(message);
            throw new Exception(message);
        }
        
        DataTable discoveryScores = new DataTable("Probe Set ID");
        discoveryScores.initializeToWorkbookSheet(sheet);
        
        //---------------------------------------------
        // Get validation scores
        //---------------------------------------------
        XSSFSheet validationSheet = workbook.getSheet(CfeResultsSheets.VALIDATION_SCORES);
        
        DataTable validationScoresDataTable = null;
        Map<String,Double> validationScores = null;
        if (validationSheet != null) {
            validationScoresDataTable = new DataTable("Gene");
            validationScoresDataTable.initializeToWorkbookSheet(validationSheet);
            
            validationScores = new HashMap<String,Double>();
            
            for (int i = 0; i < validationScoresDataTable.getNumberOfRows(); i++) {
                String gene = validationScoresDataTable.getValue(i,  "Gene");
                String scoreString = validationScoresDataTable.getValue(i, "ValidationScore");
                double score = 0.0;
                
                try {
                    score = Double.parseDouble(scoreString);
                }
                catch (NumberFormatException exception) {
                    score = 0.0;
                }
                validationScores.put(gene, score);
            }
        }
        
        //---------------------------------------------
        // Create predictor list data table
        //---------------------------------------------
        String key = "Predictor";

        DataTable predictorList = new DataTable(key);
        predictorList.addColumn(key, "");
        predictorList.addColumn("Direction", "");
        predictorList.addColumn("Male", "");
        predictorList.addColumn("Female", "");
        predictorList.addColumn("BP", "");
        predictorList.addColumn("MDD", "");
        predictorList.addColumn("SZ", "");
        predictorList.addColumn("SZA", "");
        predictorList.addColumn("PTSD", "");
        predictorList.addColumn("PSYCH", "");
        predictorList.addColumn("PSYCHOSIS", "");
        predictorList.addColumn("All", "");
        
        for (int i = 0; i < discoveryScores.getNumberOfRows(); i++) {
            double deScore = 0.0;
            double prioritizationScore = 0.0;
            double rawDeScore = 0.0;
            double dePercentile = 0.0;
            double validationScore = 0.0;
            
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
        
            // ? Will validation scores have these substitutions made ???????????????????????
            String predictor = gene + "biom" + probeset;
            predictor = predictor.replaceAll("/", ValidationScoringAction.PREDICTOR_SLASH_REPLACEMENT);
            predictor = predictor.replaceAll("-", ValidationScoringAction.PREDICTOR_HYPHEN_REPLACEMENT);
            
            if (prioritizationScores.containsKey(gene)) {
                prioritizationScore = prioritizationScores.get(gene);
            }
            else {
                this.genesNotFoundInPrioritization.add(gene);
                prioritizationScore = 0.0;
            }
            
            if (validationScores != null && validationScores.containsKey(predictor)) {
                validationScore = validationScores.get(predictor);
            }
            else {
                validationScore = 0.0;
            }

            double score = deScore + prioritizationScore + validationScore;

            if (dePercentile >= 0.3333333333 && score > this.scoreCutoff) {
                String direction = "I";
                if (rawDeScore < 0.0) {
                    direction = "D";
                }

                ArrayList<String> row = new ArrayList<String>();

                row.add(predictor);
                row.add(direction);
                row.add("0"); // Male
                row.add("0"); // Female
                row.add("0"); // BP
                row.add("0"); // MDD
                row.add("0"); // SZ
                row.add("0"); // SZA
                row.add("0"); // PTSD
                row.add("0"); // PSYCH
                row.add("0"); // PSYCHOSIS
                row.add("1"); // All

                predictorList.addRow(row);
            }
        }
        
        return predictorList;
    }
    
    public Map<String,Double> getPrioritizationScores(XSSFWorkbook workbook) throws Exception
    {
        Map<String,Double> scores = new HashMap<String,Double>();
        
        String key = "Gene";
        
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.PRIORITIZATION_SCORES);
        if (sheet == null) {
            String message = "Could not find \"" + CfeResultsSheets.PRIORITIZATION_SCORES
                    + "\" sheet in results workbook when trying to get prioritization scores.";
            log.severe(message);
            throw new Exception(message);            
        }
        
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
    	
	public DataTable createTestingScoresInfoTable() throws Exception {
        DataTable infoTable = new DataTable("attribute");
        infoTable.insertColumn("attribute", 0, "");
        infoTable.insertColumn("value",  1,  "");
        
        ArrayList<String> row = new ArrayList<String>();
        row.add("CFE Version");
        row.add(VersionNumber.VERSION_NUMBER);
        infoTable.addRow(row);
        
        //row = new ArrayList<String>();
        //row.add("Time Scores Generated");
        //row.add(this.scoresGeneratedTime.toString());
        //infoTable.addRow(row);
        
        row = new ArrayList<String>();
        row.add("");
        row.add("");
        infoTable.addRow(row);
        
	    return infoTable;
	}
	

    public String runScript(String phene, double pheneHighCutoff, String studyType,
            String masterSheetFile, String predictorListFile, String specialPredictorListFile,
            String outputDir) throws Exception {
        String result = "";
        this.scriptDir  = new File(getClass().getResource("/R").toURI()).getAbsolutePath();
        this.scriptFile = new File(getClass().getResource("/R/Predictions-Script-ALL-Dx.R").toURI()).getAbsolutePath();
        
        this.tempDir = FileUtil.getTempDir();
        
        String[] rScriptCommand = new String[10];
        rScriptCommand[0] = WebAppProperties.getRscriptPath();
        rScriptCommand[1] = this.scriptFile;
        rScriptCommand[2] = scriptDir;
        rScriptCommand[3] = phene;
        rScriptCommand[4] = pheneHighCutoff + "";
        rScriptCommand[5] = studyType;
        rScriptCommand[6] = masterSheetFile;
        rScriptCommand[7] = predictorListFile;
        rScriptCommand[8] = specialPredictorListFile;
        rScriptCommand[9] = outputDir;
        
                /*
                scriptDir                   <- args[1]
                phene                       <- args[2]
                pheneHighCutoff             <- args[3]    # Ignored for "FirstYearScore" and "HospFreq" and 
                studyType                   <- args[4]    # "cross-sectional" or "longitudinal"
                masterSheetCsvFile          <- args[5]
                predictorListCsvFile        <- args[6]
                specialPredictorListCsvFile <- args[7]
                outputDir                   <- args[8]
                */
        
        this.testingScoringCommand = "\"" + String.join("\" \"",  rScriptCommand) + "\"";
        log.info("Testing Scoring Command: " + this.testingScoringCommand);
        
        //this.scriptOutput = this.runCommand(rScriptCommand);
        return result;
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
        
        log.info("Before prediction R script process start");
        Process process = processBuilder.start();


	    BufferedReader reader = new BufferedReader(
	    new InputStreamReader(process.getInputStream()));

	    String line;
	    while ((line = reader.readLine()) != null) {
	        output.append(line + "\n");
	    }

	    log.info("Going to wait for prediction R script process...");
	    int status = process.waitFor();
	    if (status != 0) {
            //throw new Exception("Command \"" + command + "\" exited with code " + status);
	    }
	    
	    reader.close();
        log.info("reader closed");
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

    public Long getTestingDataId() {
        return testingDataId;
    }

    public void setTestingDataId(Long testingDataId) {
        this.testingDataId = testingDataId;
    }

    public List<CfeResults> getCfeResults() {
        return cfeResults;
    }

    public void setCfeResults(List<CfeResults> cfeResults) {
        this.cfeResults = cfeResults;
    }

    public CfeResults getTestingData() {
        return testingData;
    }

    public void setTestingData(CfeResults testingData) {
        this.testingData = testingData;
    }

    public File getGeneExpressionCsv() {
        return geneExpressionCsv;
    }

    public String getGeneExpressionCsvContentType() {
        return geneExpressionCsvContentType;
    }

    public void setGeneExpressionCsvContentType(String geneExpressionCsvContentType) {
        this.geneExpressionCsvContentType = geneExpressionCsvContentType;
    }

    public String getGeneExpressionCsvFileName() {
        return geneExpressionCsvFileName;
    }

    public void setGeneExpressionCsvFileName(String geneExpressionCsvFileName) {
        this.geneExpressionCsvFileName = geneExpressionCsvFileName;
    }

    public void setGeneExpressionCsv(File geneExpressionCsv) {
        this.geneExpressionCsv = geneExpressionCsv;
    }

    public double getScoreCutoff() {
        return scoreCutoff;
    }

    public void setScoreCutoff(double scoreCutoff) {
        this.scoreCutoff = scoreCutoff;
    }

    public List<String> getGenesNotFoundInPrioritization() {
        return genesNotFoundInPrioritization;
    }

    public void setGenesNotFoundInPrioritization(List<String> genesNotFoundInPrioritization) {
        this.genesNotFoundInPrioritization = genesNotFoundInPrioritization;
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

    public String getScriptDir() {
        return scriptDir;
    }

    public void setScriptDir(String scriptDir) {
        this.scriptDir = scriptDir;
    }

    public String getScriptFile() {
        return scriptFile;
    }

    public void setScriptFile(String scriptFile) {
        this.scriptFile = scriptFile;
    }

    public String getTestingScoringCommand() {
        return testingScoringCommand;
    }

    public void setTestingScoringCommand(String testingScoringCommand) {
        this.testingScoringCommand = testingScoringCommand;
    }

    public ArrayList<String> getPhenes() {
        return phenes;
    }

    public void setPhenes(ArrayList<String> phenes) {
        this.phenes = phenes;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean isStateCrossSectional() {
        return stateCrossSectional;
    }

    public void setStateCrossSectional(boolean stateCrossSectional) {
        this.stateCrossSectional = stateCrossSectional;
    }

    public boolean isStateLongitudinal() {
        return stateLongitudinal;
    }

    public void setStateLongitudinal(boolean stateLongitudinal) {
        this.stateLongitudinal = stateLongitudinal;
    }

    public String getPredictionPhene() {
        return predictionPhene;
    }

    public void setPredictionPhene(String predictionPhene) {
        this.predictionPhene = predictionPhene;
    }

    public Integer getPredictionPheneHighCutoff() {
        return predictionPheneHighCutoff;
    }

    public void setPredictionPheneHighCutoff(Integer predictionPheneHighCutoff) {
        this.predictionPheneHighCutoff = predictionPheneHighCutoff;
    }

    public boolean isFirstYear() {
        return firstYear;
    }

    public void setFirstYear(boolean firstYear) {
        this.firstYear = firstYear;
    }

    public boolean isFirstYearCrossSectional() {
        return firstYearCrossSectional;
    }

    public void setFirstYearCrossSectional(boolean firstYearCrossSectional) {
        this.firstYearCrossSectional = firstYearCrossSectional;
    }

    public boolean isFirstYearLongitudinal() {
        return firstYearLongitudinal;
    }

    public void setFirstYearLongitudinal(boolean firstYearLongitudinal) {
        this.firstYearLongitudinal = firstYearLongitudinal;
    }

    public boolean isFuture() {
        return future;
    }

    public void setFuture(boolean future) {
        this.future = future;
    }

    public boolean isFutureCrossSectional() {
        return futureCrossSectional;
    }

    public void setFutureCrossSectional(boolean futureCrossSectional) {
        this.futureCrossSectional = futureCrossSectional;
    }

    public boolean isFuturetLongitudinal() {
        return futuretLongitudinal;
    }

    public void setFuturetLongitudinal(boolean futuretLongitudinal) {
        this.futuretLongitudinal = futuretLongitudinal;
    }

    public File getSpecialPredictorListCsv() {
        return specialPredictorListCsv;
    }

    public void setSpecialPredictorListCsv(File specialPredictorListCsv) {
        this.specialPredictorListCsv = specialPredictorListCsv;
    }

    public String getSpecialPredictorListCsvContentType() {
        return specialPredictorListCsvContentType;
    }

    public void setSpecialPredictorListCsvContentType(String specialPredictorListCsvContentType) {
        this.specialPredictorListCsvContentType = specialPredictorListCsvContentType;
    }

    public String getSpecialPredictorListCsvFileName() {
        return specialPredictorListCsvFileName;
    }

    public void setSpecialPredictorListCsvFileName(String specialPredictorListCsvFileName) {
        this.specialPredictorListCsvFileName = specialPredictorListCsvFileName;
    }

    public String getPredictorListFile() {
        return predictorListFile;
    }

    public void setPredictorListFile(String predictorListFile) {
        this.predictorListFile = predictorListFile;
    }

    public String getTestingMasterSheetFile() {
        return testingMasterSheetFile;
    }

    public void setTestingMasterSheetFile(String testingMasterSheetFile) {
        this.testingMasterSheetFile = testingMasterSheetFile;
    }

    public String getSpecialPredictorListTempFile() {
        return specialPredictorListTempFile;
    }

    public void setSpecialPredictorListTempFile(String specialPredictorListTempFile) {
        this.specialPredictorListTempFile = specialPredictorListTempFile;
    }

    public String getFinalMasterSheetFile() {
        return finalMasterSheetFile;
    }

    public void setFinalMasterSheetFile(String finalMasterSheetFile) {
        this.finalMasterSheetFile = finalMasterSheetFile;
    }

}
