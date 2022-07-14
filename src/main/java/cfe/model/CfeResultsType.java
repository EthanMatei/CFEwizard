package cfe.model;

import java.util.ArrayList;
import java.util.List;

public class CfeResultsType {
    public final static String DISCOVERY_COHORT        = "discovery cohort";
    public final static String DISCOVERY_SCORES        = "discovery scores";
       
    public final static String PRIORITIZATION_SCORES              = "prioritization scores";
    public final static String PRIORITIZATION_SCORES_ONLY         = "prioritization scores only";
    
    public final static String VALIDATION_COHORT       = "validation cohort";
    public final static String VALIDATION_COHORT_ONLY  = "validation cohort only";    
    public final static String VALIDATION_SCORES       = "validation scores";
    
    //public final static String VALIDATION_COHORT_PLUS_DISCOVERY_SCORES       = "validation cohort + discovery scores";
    //public final static String VALIDATION_COHORT_PLUS_PRIORITIZATION_SCORES  = "validation cohort + prioritization scores";
    //public final static String VALIDATION_COHORT_PLUS_VALIDATION_SCORES      = "validation cohort + validation scores";

    public final static String TESTING_COHORTS                        = "testing cohorts";
    public final static String TESTING_COHORTS_ONLY                   = "testing cohorts only";
    public final static String TESTING_SCORES                         = "testing scores";
    
    //public final static String ALL_COHORTS                        = "all cohorts";
    //public final static String ALL_COHORTS_ONLY                   = "all cohorts only";
    //public final static String ALL_COHORTS_PLUS_DISCOVERY_SCORES  = "all cohorts + discovery scores";
    //public final static String ALL_COHORTS_PLUS_VALIDATION_SCORES = "all cohorts + validation scores";
    //public final static String ALL_COHORTS_PLUS_ALL_SCORES         = "all cohort + all scores";

    
    public List<String> getTypes() {
        List<String> types = new ArrayList<String>();
        types.add(DISCOVERY_COHORT);
        types.add(DISCOVERY_SCORES);
        
        types.add(PRIORITIZATION_SCORES);
        types.add(PRIORITIZATION_SCORES_ONLY);
        
        types.add(VALIDATION_COHORT);
        types.add(VALIDATION_COHORT_ONLY);
        types.add(VALIDATION_SCORES);
        
        types.add(TESTING_COHORTS);
        types.add(TESTING_COHORTS_ONLY);
        types.add(TESTING_SCORES);
        
        //types.add(VALIDATION_COHORT_PLUS_PRIORITIZATION_SCORES);
        //types.add(VALIDATION_COHORT_PLUS_VALIDATION_SCORES);
        
        //types.add(ALL_COHORTS);
        //types.add(ALL_COHORTS_PLUS_DISCOVERY_SCORES);
        //types.add(ALL_COHORTS_PLUS_VALIDATION_SCORES);
        //types.add(ALL_COHORTS_PLUS_ALL_SCORES);

        return types;
    }
}