package cfe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that lists the possible file types for CFE Results.
 * 
 * @author Jim Mullen
 *
 */
public class CfeResultsFileType {
    
    public final static String DISCOVERY_COHORT      = "discovery_cohort";
    public final static String DISCOVERY_COHORT_DATA = "discovery_cohort_data";
    public final static String DISCOVERY_COHORT_INFO = "discovery_cohort_info";
    
    public final static String DISCOVERY_R_SCRIPT_COMMAND = "discovery_r_script_command";
    public final static String DISCOVERY_R_SCRIPT_LOG     = "discovery_r_script_log";
    
    public final static String PREDICTION_STATE_CROSS_SECTIONAL              = "prediction_state_cross_sectional";
    public final static String PREDICTION_STATE_LONGITUDINAL                 = "prediction_state_longitudinal";
    public final static String PREDICTION_STATE_CROSS_SECTIONAL_R_SCRIPT_LOG = "prediction_state_cross_sectional_r_script_log";
    public final static String PREDICTION_STATE_LONGITUDINAL_R_SCRIPT_LOG    = "prediction_state_longitudinal_r_script_log";

    public final static String PREDICTION_FIRST_YEAR_CROSS_SECTIONAL              = "prediction_first_year_cross_sectional";
    public final static String PREDICTION_FIRST_YEAR_LONGITUDINAL                 = "prediction_first_year_longitudinal";
    public final static String PREDICTION_FIRST_YEAR_CROSS_SECTIONAL_R_SCRIPT_LOG = "prediction_first_year_cross_sectional_r_script_log";
    public final static String PREDICTION_FIRST_YEAR_LONGITUDINAL_R_SCRIPT_LOG    = "prediction_first_year_longitudinal_r_script_log";

    public final static String PREDICTION_FUTURE_CROSS_SECTIONAL_R_SCRIPT_LOG = "prediction_future_cross_sectional_r_script_log";
    public final static String PREDICTION_FUTURE_LONGITUDINAL_R_SCRIPT_LOG    = "prediction_future_longitudinal_r_script_log";
    
    public final static String VALIDATION_R_SCRIPT_LOG   = "validation_r_script_log";
    
    public List<String> getTypes() {
        List<String> types = new ArrayList<String>();
        types.add(VALIDATION_R_SCRIPT_LOG);
        return types;
    }
    
    /**
     * Converts a type to its sheet/file name. For CSV file types that are stored as sheets in a spreadsheet, the
     * type is converted to the sheet name to use. For files that are stand alone files (such as R script log files), the
     * type is converted to the file name to use.
     * 
     * @param type
     * @return
     */
    public String typeToName(CfeResultsType type) {
        String name = "";
        
        if (type.toString().endsWith("log")) {
            // text file
            name = type.toString();
            name += ".txt";
        }
        else {
            // sheet - best not to exceed 31 characters for the name
            name = type.toString();
            name = name.replace("first_year", "1st-year");
            name = name.replace("cross_sectional", "cross-sec.");
            name = name.replace("longitudinal", "long.");
            name = name.replace('_', ' ');
        }
        
        return name;
    }
}