package cfe.model;

import java.util.ArrayList;
import java.util.List;

public class CfeResultsType {
    public final static String DISCOVERY_COHORT                   = "discovery cohort";
    public final static String DISCOVERY_SCORES                   = "discovery scores";
    public final static String ALL_COHORTS                        = "all cohorts";
    public final static String ALL_COHORTS_PLUS_DISCOVERY_SCORES  = "all cohorts + discovery scores";
    public final static String ALL_COHORTS_PLUS_VALIDATION_SCORES = "all cohorts + validation scores";
    public final static String PRIORITIZATION_SCORES              = "prioritization scores";
    
    public List<String> getTypes() {
        List<String> types = new ArrayList<String>();
        types.add(DISCOVERY_COHORT);
        types.add(DISCOVERY_SCORES);
        types.add(ALL_COHORTS);
        types.add(ALL_COHORTS_PLUS_DISCOVERY_SCORES);
        types.add(PRIORITIZATION_SCORES);
        return types;
    }
}