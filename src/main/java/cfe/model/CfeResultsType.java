package cfe.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import cfe.calc.DiscoveryCohortCalc;

public class CfeResultsType {
    private static Logger log = Logger.getLogger(CfeResultsType.class.getName());
    
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

    private String value;
    
    public CfeResultsType(String value) throws Exception {
        Set<String> typesSet = this.getTypesSet();
        if (!typesSet.contains(value)) {
            throw new Exception("CFE Results type \"" + value + "\" is not a valid type.");
        }
        this.value = value;    
    }
    
    /**
     * Gets types that can be specified as the end of the calculation.
     * 
     * @return
     */
    public static List<String> getEndTypes() {
        List<String> types = new ArrayList<String>();
        types.add(DISCOVERY_COHORT);
        types.add(DISCOVERY_SCORES);
        
        types.add(PRIORITIZATION_SCORES);
        
        types.add(VALIDATION_COHORT);
        types.add(VALIDATION_SCORES);
        
        types.add(TESTING_COHORTS);
        types.add(TESTING_SCORES);

        return types;
    }
 
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
    
    public static List<String> getCompleteTypes() {
        List<String> types = new ArrayList<String>();
        types.add(DISCOVERY_COHORT);
        types.add(DISCOVERY_SCORES);
        
        types.add(PRIORITIZATION_SCORES);

        types.add(VALIDATION_COHORT);
        types.add(VALIDATION_SCORES);
        
        types.add(TESTING_COHORTS);
        types.add(TESTING_SCORES);

        return types;
    }   
    
    public int getOrder() throws Exception {
        int order = 0;
        
        String type = this.value;
        
        if (!this.isValid(type)) {
            throw new Exception("CFE results type \"" + type + "\" is not valid.");
        }
        
        if (type.equals(DISCOVERY_COHORT)) {
            order = 1;
        }
        else if (type.equals(DISCOVERY_SCORES)) {
            order = 2;
        }
        else if (type.equals(PRIORITIZATION_SCORES) || type.equals(PRIORITIZATION_SCORES_ONLY)) {
            order = 3;
        }
        else if (type.equals(VALIDATION_COHORT) || type.equals(VALIDATION_COHORT_ONLY)) {
            order = 4;
        }
        else if (type.equals(VALIDATION_SCORES)) {
            order = 5;
        }
        else if (type.equals(TESTING_COHORTS) || type.equals(TESTING_COHORTS_ONLY)) {
            order = 6;
        }
        else if (type.equals(TESTING_SCORES)) {
            order = 7;
        }
        
        return order;
    }
    
    public Set<String> getTypesSet() {
        Set<String> typesSet = new HashSet<String>(this.getTypes());
        
        return typesSet;
    }
    
    public boolean isValid(String type) {
        boolean isValid = false;
        Set<String> types = this.getTypesSet();
        if (types.contains(type)) {
            isValid = true;
        }
        return isValid;
    }
    
    /**
     * Indicates if this type come before the specified type in the pipeline workflow order.
     * 
     * @param type the type to check.
     * @return
     */
    public boolean isBefore(String type) throws Exception {
        boolean isBefore = false;
        
        CfeResultsType compareType = new CfeResultsType(type);
        
        if (this.getOrder() < compareType.getOrder()) {
            isBefore = true;
        }

        return isBefore;
    }
    
    public boolean isEqualTo(String type) throws Exception {
        boolean isEqual = false;
        
        CfeResultsType compareType = new CfeResultsType(type);
        
        if (this.getOrder() == compareType.getOrder()) {
            isEqual = true;
        }

        return isEqual;
    }
    
    public boolean isAfter(String type) throws Exception {
        boolean isAfter = false;
        
        CfeResultsType compareType = new CfeResultsType(type);
        
        if (this.getOrder() > compareType.getOrder()) {
            isAfter = true;
        }

        return isAfter;        
    }
    
    /**
     * Indicates if lowerBound < type <= upperBoundInclusive.
     * 
     * @param type
     * @param lowerBound
     * @param upperBoundInclusive
     * @return
     */
    public static boolean typeIsInRange(String type, String lowerBound, String upperBoundInclusive) throws Exception {
        log.info("type = \"" + type + "\", lower = \"" + lowerBound + "\", upperBound = \"" + upperBoundInclusive + "\".");
        boolean inRange = false;
        CfeResultsType cfeResultsType = new CfeResultsType(type);
        if (lowerBound == null || lowerBound.trim().equals("") || cfeResultsType.isAfter(lowerBound)) {
            log.info("FIRST CHECK!!!!!!!!!!");
            if (!cfeResultsType.isAfter(upperBoundInclusive)) {
                log.info("SECOND CHECK!!!!!!!!!!!!!");
                inRange = true;
            }
        }
        return inRange;
    }
    
    public String getValue() {
        return this.value;
    }
}