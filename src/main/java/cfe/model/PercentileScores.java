package cfe.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for representing percentile ranges with their asscoiated scores.
 * 
 * @author Jim Mullen
 *
 */
public class PercentileScores {
    
    private List<Double> upperBounds;
    private List<Double> scores;
    
    public PercentileScores() {
        this.upperBounds = new ArrayList<Double>();
        this.scores      = new ArrayList<Double>();
        
        this.upperBounds.add(0.333333333333);
        this.upperBounds.add(0.5);
        this.upperBounds.add(0.8);
        this.upperBounds.add(1.0);
        
        this.scores.add(0.0);
        this.scores.add(1.0);
        this.scores.add(2.0);
        this.scores.add(4.0);
    }
    
    public PercentileScores(double[] upperBounds, double[] scores) throws Exception {
        if (upperBounds == null || upperBounds.length == 0) {
            throw new Exception("Empty upper bounds specified for percentile scores.");
        }
        else if (scores == null || scores.length == 0) {
            throw new Exception("Empty scores specified for percentile scores.");
        }
        else if (upperBounds.length != scores.length) {
            String message = "The number of values for upper bounds (" + upperBounds.length + ") and"
                    + " scores (" + scores.length + ") do not match for percentile scores.";
            throw new Exception(message);
        }
        
        this.upperBounds = new ArrayList<Double>();
        this.scores      = new ArrayList<Double>();
        
        for (int i = 0; i < upperBounds.length; i++) {
            if (i > 0 && upperBounds[i] <= upperBounds[i-1]) {
                String message = "Upper bound " + i + " (" + upperBounds[i] + ") is not greater than"
                        + " upper bound " + (i-1) + " (" + upperBounds[i-1] + ") for percentile scores.";
                throw new Exception(message);
            }
            this.upperBounds.add(upperBounds[i]);
            this.scores.add(scores[i]);
        }
    }
    
    public List<Double> getLowerBounds() {
        List<Double> lowerBounds = new ArrayList<Double>();
        
        lowerBounds.add(0.0);
        lowerBounds.addAll(this.upperBounds);
        lowerBounds.remove(upperBounds.size()); // remove 1.0 element
        
        return lowerBounds;
    }

    public List<Double> getUpperBounds() {
        return upperBounds;
    }

    public void setUpperBounds(List<Double> upperBounds) {
        this.upperBounds = upperBounds;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void setScores(List<Double> scores) {
        this.scores = scores;
    }
    
    /**
     * Gets the score for the specified percentile.
     * 
     * @param percentile
     * @return
     */
    public double getScore(double percentile) throws Exception {
        if (percentile < 0.0 || percentile > 1.0) {
            String message = "Score percentile " + percentile + " is out of range [0, 1)";
            throw new Exception(message);
        }
        
        double score = 0.0;
        
        for (int i = 0; i < this.upperBounds.size(); i++) {
            if (percentile < this.upperBounds.get(i) || i == this.upperBounds.size() - 1) {
                score = this.scores.get(i);
                break;
            }
        }
        return score;
    }
    
}
