package cfe.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class PheneCondition {
    private static final Logger log = Logger.getLogger(PheneCondition.class.getName());
    
    private String phene;
    private String operator;
    private double value;
    
    public PheneCondition(String phene, String operator, double value) {
        this.phene = phene;
        this.operator = operator;
        this.value = value;
    }
    
    public static List<PheneCondition> createList(
            String phene1, String operator1, String value1,
            String phene2, String operator2, String value2,
            String phene3, String operator3, String value3
    ) {
        double value;
        PheneCondition pheneCondition;
        List<PheneCondition> pheneConditions = new ArrayList<PheneCondition>();


        if (phene1 != null && !phene1.isEmpty() && value1 != null && !value1.isEmpty()) {
            value = Double.parseDouble(value1);
            pheneCondition = new PheneCondition(phene1, operator1, value);
            pheneConditions.add(pheneCondition);
        }

        if (phene2 != null && !phene2.isEmpty() && value2 != null && !value2.isEmpty()) {
            value = Double.parseDouble(value2);
            pheneCondition = new PheneCondition(phene2, operator2, value);
            pheneConditions.add(pheneCondition);
        }

        if (phene3 != null && !phene3.isEmpty() && value3 != null && !value3.isEmpty()) {
            value = Double.parseDouble(value3);
            pheneCondition = new PheneCondition(phene3, operator3, value);
            pheneConditions.add(pheneCondition);
        }
        
        return pheneConditions;
    }
    
    
    public boolean isTrue(double pheneValue) {
        boolean isTrue = false;
        
        if (operator.equals("<")) {
            isTrue = pheneValue < value;
        }
        else if (operator.equals("<=")) {
            isTrue = pheneValue <= value;
        }
        else if (operator.equals("=")) {
            isTrue = pheneValue == value;
        }
        else if (operator.equals(">")) {
            isTrue = pheneValue > value;
        }
        else if (operator.equals(">=")) {
            isTrue = pheneValue >= value;
        }
        
        return isTrue;
    }
    
    /**
     * Returns true if all the phene conditions are true for the specified phene values map.
     * Note: if there are no conditions, then true is returned.
     * 
     * @param pheneConditions
     * @param values map from phene name to phene value
     * @return
     */
    public static boolean isTrue(List<PheneCondition> pheneConditions, Map<String,String> values) {
        boolean isTrue = true;
        
        for (PheneCondition pheneCondition: pheneConditions) {
            String phene = pheneCondition.getPhene();
            String valueString = values.get(phene);
            double value;
            
            log.info("Phene condition - \"" + phene + "\" " + pheneCondition.operator
                    + " " + pheneCondition.value + " | \"" + valueString + "\"");
            
            try {
                if (valueString == null || valueString.isEmpty()) {
                    // No value for this phene
                    isTrue = false;
                    break;
                }
                
                value = Double.parseDouble(valueString);
                if (!pheneCondition.isTrue(value)) {
                    isTrue = false;
                    break;
                }
            }
            catch (NumberFormatException exception) {
                // there is no numeric value for the phene
                isTrue = false;
                break;
            }

        }
        
        log.info("IS TRUE: " + isTrue);
        return isTrue;
    }
    
    public String getPhene() {
        return this.phene;
    }
    
    public String getOperator() {
        return this.operator;
    }
    
    public double getValue() {
        return this.value;
    }
}
