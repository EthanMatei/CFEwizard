package cfe.model;

import java.util.ArrayList;
import java.util.List;

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
    
}
