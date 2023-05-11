package cfe.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cfe.action.BatchAction;
import cfe.utils.DataTable;

/**
 * Class for interacting with CFE results workbook.
 * 
 * @author Jim Mullen
 *
 */
public class CfeResultsWorkbook {
    
    private static Logger log = Logger.getLogger(CfeResultsWorkbook.class.getName());
    
    private XSSFWorkbook workbook;
    
    public CfeResultsWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }
    
    // WORK IN PROGRESS
    public static String inferResultsType(XSSFWorkbook workbook) {
        String resultsType = null;
        

        Set<String> sheetNames = new HashSet<String>();
        
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String name = workbook.getSheetName(i);
            sheetNames.add(name);
        }
        
        // Check for sheets - NEED TO CHECK IN ORDER OF LAST PHASE TO FIRST PHASE!
        if (sheetNames.contains(CfeResultsSheets.TESTING_SCORES_INFO)
                    || sheetNames.contains(CfeResultsSheets.TESTING_FIRST_YEAR_CROSS_SECTIONAL)
                    || sheetNames.contains(CfeResultsSheets.TESTING_FIRST_YEAR_LONGITUDINAL)
                    || sheetNames.contains(CfeResultsSheets.TESTING_FUTURE_CROSS_SECTIONAL)
                    || sheetNames.contains(CfeResultsSheets.TESTING_FUTURE_LONGITUDINAL)
                    || sheetNames.contains(CfeResultsSheets.TESTING_STATE_CROSS_SECTIONAL)
                    || sheetNames.contains(CfeResultsSheets.TESTING_STATE_LONGITUDINAL)
            ) {
                resultsType = CfeResultsType.TESTING_SCORES;
        }
        else if (sheetNames.contains(CfeResultsSheets.TESTING_COHORT)   // ??? Is this for testing or validation cohort???
                || sheetNames.contains(CfeResultsSheets.TESTING_COHORT_DATA)  // ????????? Is this for testing or validation cohort
                || sheetNames.contains(CfeResultsSheets.TESTING_COHORT_INFO)
            ) {
            resultsType = CfeResultsType.TESTING_COHORTS;
        }
        else if (sheetNames.contains(CfeResultsSheets.VALIDATION_SCORES)
                || sheetNames.contains(CfeResultsSheets.VALIDATION_SCORES_INFO)
            ) {
            resultsType = CfeResultsType.VALIDATION_SCORES;
        }
        else if (sheetNames.contains(CfeResultsSheets.VALIDATION_COHORT)
                || sheetNames.contains(CfeResultsSheets.VALIDATION_COHORT_INFO)
            ) {
            resultsType = CfeResultsType.VALIDATION_COHORT;
        }
        else if (sheetNames.contains(CfeResultsSheets.PRIORITIZATION_DISEASES)
                || sheetNames.contains(CfeResultsSheets.PRIORITIZATION_GENE_LIST)
                || sheetNames.contains(CfeResultsSheets.PRIORITIZATION_SCORE_DETAILS)
                || sheetNames.contains(CfeResultsSheets.PRIORITIZATION_SCORES)
                || sheetNames.contains(CfeResultsSheets.PRIORITIZATION_SCORES_INFO)
                || sheetNames.contains(CfeResultsSheets.PRIORITIZATION_SCORING_WEIGHTS)
            ) {
            resultsType = CfeResultsType.PRIORITIZATION_SCORES;
        }
        else if (sheetNames.contains(CfeResultsSheets.DISCOVERY_SCORES)) {
            resultsType = CfeResultsType.DISCOVERY_SCORES;
        }
        else if (sheetNames.contains(CfeResultsSheets.DISCOVERY_COHORT)) {
            resultsType = CfeResultsType.DISCOVERY_COHORT;
        }

        return resultsType;
    }
    
    /**
     * Gets the discovery phene and its low and high cutoffs from a CfeResults workbook.
     *
     * @param workbook
     * @return
     * @throws Exception
     */
    public static Triple<String, Double, Double> getPheneInfo(XSSFWorkbook workbook) throws Exception {

        String sheetName = CfeResultsSheets.DISCOVERY_COHORT_INFO;
        
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new Exception("Sheet \"" + sheetName + "\" not found in workbook.");
        }
        DataTable discoveryCohortInfo = new DataTable("attribute");
        discoveryCohortInfo.initializeToWorkbookSheet(sheet);
        
        //------------------------------------------------
        // Get the discovery phene
        //------------------------------------------------
        String phene = discoveryCohortInfo.getValue("Phene", "value");
        if (phene == null || phene.isEmpty()) {
            throw new Exception("No phene value found in workbook sheet \"" + sheetName + "\".");
        }
        
        //-------------------------------------------
        // Get the discovery phene low cutoff
        //-------------------------------------------
        String lowCutoffString = discoveryCohortInfo.getValue("Low Cutoff", "value");
        if (lowCutoffString == null || lowCutoffString.isEmpty()) {
            throw new Exception("No phene low cutoff specified in sheet \"" + sheetName + "\".");
        }
        
        Double lowCutoff = 0.0;
        try {
            lowCutoff = Double.parseDouble(lowCutoffString);
        }
        catch (NumberFormatException exception) {
            throw new Exception("The phene low cutoff \"" + lowCutoffString + "\" in sheet \"" + sheetName +
                    "\" is not a valid number.");
        }
        
        //-------------------------------------------
        // Get the discovery phene high cutoff
        //-------------------------------------------
        String highCutoffString = discoveryCohortInfo.getValue("High Cutoff", "value");
        if (highCutoffString == null || highCutoffString.isEmpty()) {
            throw new Exception("No phene high cutoff specifiied in sheet \"" + sheetName + "\".");
        }
        
        Double highCutoff = 0.0;
        try {
            highCutoff = Double.parseDouble(highCutoffString);
        }
        catch (NumberFormatException exception) {
            throw new Exception("The phene high cutoff \"" + highCutoffString + "\" in sheet \"" + sheetName +
                    "\" is not a valid number.");
        }        
        
        Triple<String, Double, Double> pheneNameLowHigh
            = new MutableTriple<String, Double, Double>(phene, lowCutoff, highCutoff);
        
        return pheneNameLowHigh;
    }
    
    
    /**
     * Gets the diagnosis codes.
     * 
     * @return
     * @throws Exception
     */
    public Map<String,String> getDiagnosisCodes() throws Exception {
        Map<String,String> diagnosisCodes = new HashMap<String,String>();
        
        XSSFSheet sheet = workbook.getSheet(CfeResultsSheets.COHORT_DATA);
        if (sheet == null) {
            String message = "Could not find sheet \"" + CfeResultsSheets.COHORT_DATA + "\" in spreadsheet.";
            log.severe(message);
            throw new Exception(message);
        }
        DataTable diagnosisData = new DataTable("DxCode");
        diagnosisData.initializeToWorkbookSheet(sheet);
        
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

            diagnosisCodes.put(code, examples);
        }
        
        return diagnosisCodes;
    }
    
}
