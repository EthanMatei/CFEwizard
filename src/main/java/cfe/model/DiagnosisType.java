package cfe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that lists the possible file types for CFE Results.
 * 
 * @author Jim Mullen
 *
 */
public class DiagnosisType {
    
    public final static String GENDER           = "gender";
    public final static String GENDER_DIAGNOSIS = "gender/diagnosis";

    
    public static List<String> getTypes() {
        List<String> types = new ArrayList<String>();
        types.add(GENDER);
        types.add(GENDER_DIAGNOSIS);
        return types;
    }

}