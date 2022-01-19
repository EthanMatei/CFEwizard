package cfe.utils;

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
                    + " " + pheneCondition.value + " | \"" + valueString);
            
            try {
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
}
