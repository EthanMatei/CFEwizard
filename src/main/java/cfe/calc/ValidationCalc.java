package cfe.calc;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cfe.model.CfeResults;
import cfe.model.CfeResultsSheets;
import cfe.model.CfeResultsType;
import cfe.model.VersionNumber;
import cfe.services.CfeResultsService;
import cfe.utils.CohortDataTable;
import cfe.utils.DataTable;
import cfe.utils.FileUtil;
import cfe.utils.PheneCondition;
import cfe.utils.WebAppProperties;

public class ValidationCalc {
	private static final Logger log = Logger.getLogger(ValidationCalc.class.getName());
	
	public static CfeResults createValidationCohort(
	        CfeResults prioritizationResults,
	        String discoveryPhene,
	        Double discoveryLowCutoff,
	        Double discoveryHighCutoff,
	        List<PheneCondition> pheneConditions,
	        double percentInValidation,          // [0,1]
	        double comparisonThreshold
	) throws Exception
	{
        XSSFWorkbook prioritizationWorkbook = prioritizationResults.getResultsSpreadsheet();
        LinkedHashMap<String,DataTable> dataTables = prioritizationResults.getDataTables();
        
        TreeSet<String> cohortSubjects     = new TreeSet<String>();
        TreeSet<String> validationSubjects = new TreeSet<String>();
        TreeSet<String> testingSubjects    = new TreeSet<String>();
        
        int numberOfValidationSubjects = 0;
        int numberOfTestingSubjects    = 0;
        
        //-------------------------------------------------------------------------------
        // Create new CFE results that has all the cohorts plus previous information
        //-------------------------------------------------------------------------------
        dataTables.remove(CfeResultsSheets.COHORT_DATA);  // Remove cohort data, which will be replaced
        XSSFWorkbook resultsWorkbook = DataTable.createWorkbook(dataTables);
        
        XSSFSheet sheet = prioritizationWorkbook.getSheet(CfeResultsSheets.COHORT_DATA);
        CohortDataTable cohortData = new CohortDataTable();
        cohortData.setKey(null);
        cohortData.initializeToWorkbookSheet(sheet);
        cohortData.setKey("Subject Identifiers.PheneVisit");

        // ...

        
        // Modify (all) cohort data table
        cohortData.addCohort("validation", validationSubjects);
        cohortData.addCohort("testing", testingSubjects);
        String[] sortColumns = {"Cohort", "Subject", "Subject Identifiers.PheneVisit"};
        cohortData.sortWithBlanksLast(sortColumns);            

        // Create validation cohort data table
        ArrayList<String> columns = new ArrayList<String>();
        columns.add("Subject");
        columns.add("VisitNumber");
        columns.add("Subject Identifiers.PheneVisit");
        columns.add("AffyVisit");
        columns.add("Visit Date");
        columns.add("Gender(M/F)");
        columns.add("Age at testing (Years)");
        columns.add("Race/Ethnicity");
        columns.add("DxCode");

        columns.add(discoveryPhene);
        for (PheneCondition condition: pheneConditions) {
            String conditionPhene = condition.getPhene();

            // Avoid adding duplicate phenes, e.g. one of the condition phenes
            // is the same as the discovery phene.
            if (!columns.contains(conditionPhene)) {
                columns.add(conditionPhene);
            }
        }

        columns.add("Validation");
        columns.add("ValCategory");
        columns.add("ValidationCohort");
        columns.add("TestingCohort");


        DataTable validationCohort = cohortData.filter("Subject Identifiers.PheneVisit", columns);

        String[] validationSortColumns = {"Subject", "VisitNumber"};
        validationCohort.sort(validationSortColumns);  

        validationCohort.addToWorkbook(resultsWorkbook, CfeResultsSheets.VALIDATION_COHORT);

        // Create testing cohort data table
        DataTable testingCohort = new DataTable("Subject");
        testingCohort.addColumn("Subject",  "");
        for (String subject: testingSubjects) {
            ArrayList<String> row = new ArrayList<String>();
            row.add(subject);
            testingCohort.addRow(row);
        }

        testingCohort.addToWorkbook(resultsWorkbook, CfeResultsSheets.TESTING_COHORT);

        // Create validation cohort info table
        DataTable validationCohortInfo = new DataTable();
        validationCohortInfo.addColumn("attribute", "");
        validationCohortInfo.addColumn("value", "");

        ArrayList<String> row;

        row = new ArrayList<String>();
        row.add("CFE Version");
        row.add(VersionNumber.VERSION_NUMBER);
        validationCohortInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Time Cohort Generated");
        row.add(new Date().toString());
        validationCohortInfo.addRow(row);


        row = new ArrayList<String>();
        row.add("% in validation cohort specified");
        String percentInValidationCohort = (percentInValidation * 100.0) + "";
        row.add(percentInValidationCohort);
        validationCohortInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Number of validation cohort subjects");
        row.add(numberOfValidationSubjects + "");
        validationCohortInfo.addRow(row);            

        row = new ArrayList<String>();
        row.add("Number of testing cohort subjects");
        row.add(numberOfTestingSubjects + "");
        validationCohortInfo.addRow(row); 

        row = new ArrayList<String>();
        row.add("Discovery Phene");
        row.add(discoveryPhene);
        validationCohortInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Discovery Low Cutoff");
        row.add(discoveryLowCutoff + "");
        validationCohortInfo.addRow(row);

        row = new ArrayList<String>();
        row.add("Discovery High Cutoff");
        row.add(discoveryHighCutoff + "");
        validationCohortInfo.addRow(row);


        for (int i = 1; i <= 3; i++) {
            row = new ArrayList<String>();
            row.add("Constraint " + i);
            
            if (pheneConditions.size() >= i) {
                PheneCondition pheneCondition = pheneConditions.get(i-1);
                row.add(pheneCondition.getPhene() + " " + pheneCondition.getOperator() + " " + pheneCondition.getValue());
            }
            else {
                row.add("");
            }
            validationCohortInfo.addRow(row);
        }

        validationCohortInfo.addToWorkbook(resultsWorkbook, CfeResultsSheets.VALIDATION_COHORT_INFO);

        cohortData.addToWorkbook(resultsWorkbook, CfeResultsSheets.COHORT_DATA);
        

        //-------------------------------------------------------
        // Get the cohort subjects
        //-------------------------------------------------------
        List<TreeSet<String>> cohortResults = cohortData.setValidationAndTestingCohorts(
                discoveryPhene, discoveryLowCutoff, discoveryHighCutoff,
                comparisonThreshold,
                // clinicalPhene, clinicalHighCutoff,
                pheneConditions, percentInValidation
                );
        
        validationSubjects = cohortResults.get(0);
        testingSubjects    = cohortResults.get(1);

        numberOfValidationSubjects = validationSubjects.size();
        numberOfTestingSubjects    = testingSubjects.size();

        List<String> subjects = new ArrayList<String>();
        subjects.addAll(cohortSubjects);        
        
	    //-------------------------------------------
	    // Create and save CFE results
	    //-------------------------------------------
	    CfeResults cfeResults = new CfeResults();
        
	    // To generalize this, this part would need to be expanded to handle case when input results
	    // are just a Discovery Cohort
	    cfeResults.setResultsType(CfeResultsType.VALIDATION_COHORT);
        cfeResults.addCsvAndTextFiles(prioritizationResults);
        
        cfeResults.setResultsSpreadsheet(resultsWorkbook);
	    cfeResults.setPhene(discoveryPhene);
	    cfeResults.setLowCutoff(discoveryLowCutoff);
	    cfeResults.setHighCutoff(discoveryHighCutoff);
	    cfeResults.setGeneratedTime(new Date());

	    CfeResultsService.save(cfeResults);

		return cfeResults;
	}
	
    public static CfeResults calculateValidationScores(
            CfeResults validationCohortResults,
            String validationMasterSheetFile,
            String updatedMasterSheetFileName,
            String predictorListFile,
            String updatedPredictorListFileName
    ) throws Exception {

        String masterSheetArg   = validationMasterSheetFile;
        String predictorListArg = predictorListFile;
        
        //-------------------------------------------------------
        // Check for updated master sheet and predictor list
        //-------------------------------------------------------
        if (updatedMasterSheetFileName != null && !updatedMasterSheetFileName.isEmpty()) {
            // Updated master sheet provided
            masterSheetArg = updatedMasterSheetFileName;
        }
        
        if (updatedPredictorListFileName != null && !updatedPredictorListFileName.isEmpty()) {
            // Updated predictor list provided
            predictorListArg = updatedPredictorListFileName; 
        }

        String scriptDir  = new File(ValidationCalc.class.getResource("/R").toURI()).getAbsolutePath();
        String scriptFile = new File(ValidationCalc.class.getResource("/R/Validation.R").toURI()).getAbsolutePath();
        
        if (scriptDir == null || scriptDir.isEmpty()) {
            throw new Exception("The R script directory could not be determined for validation scoring.");    
        }
        
        if (scriptFile == null || scriptFile.isEmpty()) {
            throw new Exception("The validation scoring script could not be located.");
        }
        
        String tempDir = FileUtil.getTempDir();
        

        DataTable cohortData = validationCohortResults.getSheetAsDataTable(CfeResultsSheets.COHORT_DATA, null);
        Set<String> diagnosesSet = cohortData.getUniqueValues("DxCode");
        String diagnoses = String.join(",", diagnosesSet);
        
        Set<String> genderDiagnosesSet = cohortData.getUniqueCombinedValues("Gender(M/F)", "DxCode", "-");
        String genderDiagnoses = String.join(",", genderDiagnosesSet);
        log.info("Gender Diagnoses: " + genderDiagnoses);
        
        //-----------------------------------------------------------
        // Get Discovery Phene info
        //-----------------------------------------------------------
        String phene = validationCohortResults.getPhene();
        Double lowCutoff = validationCohortResults.getLowCutoff();
        Double highCutoff = validationCohortResults.getHighCutoff();
        
        // Create the R script command
        String[] rScriptCommand = new String[7];
        rScriptCommand[0] = WebAppProperties.getRscriptPath();    // Full path of the Rscript command
        rScriptCommand[1] = scriptFile;     // The R script to run
        rScriptCommand[2] = scriptDir;   // The directory that contains R scripts
        rScriptCommand[3] = phene;
        rScriptCommand[4] = masterSheetArg;
        rScriptCommand[5] = predictorListArg;
        rScriptCommand[6] = tempDir;
        
        
        String validationScoringCommand = "\"" + String.join("\" \"",  rScriptCommand) + "\"";
        log.info("Validation Scoring Command: " + validationScoringCommand);
        
        //String scriptOutput = this.runCommand(rScriptCommand);
        
        // Map from sheet name to data table
        LinkedHashMap<String, DataTable> resultsTables = new LinkedHashMap<String, DataTable>();
        
        resultsTables = validationCohortResults.getDataTables();
        //resultsTables.put(CfeResultsSheets.VALIDATION_SCORES, validationScoringDataTable);
        //resultsTables.put(CfeResultsSheets.VALIDATION_SCORES_INFO, validationScoresInfo);
        
        XSSFWorkbook resultsWorkbook = DataTable.createWorkbook(resultsTables);
        
        // ...
        

        
        // ...
        

        // ...

        
        // Set generate time
        Date scoresGeneratedTime = new Date();
        
        // Save the results in the database
        CfeResults cfeResults = new CfeResults(
                resultsWorkbook,
                CfeResultsType.VALIDATION_SCORES,
                scoresGeneratedTime, phene,
                lowCutoff, highCutoff
        );
        
        return cfeResults;    
    }
    
}
