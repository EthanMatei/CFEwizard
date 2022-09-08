package cfe.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
