package cfe.model;

import java.util.ArrayList;
import java.util.List;

public class PercentileScore {
    private double lowerBound;
    private double upperBound;
    private double score;
    
    public static List<PercentileScore> getDefaultPercentileScores() {
        List<PercentileScore> percentileScores = new ArrayList<PercentileScore>();
        
        PercentileScore percentileScore = new PercentileScore();
        
        percentileScore.lowerBound = 0.0;
        percentileScore.upperBound = 0.333333;
        percentileScore.score      = 1.0;
        percentileScores.add(percentileScore);
        
        percentileScore = new PercentileScore();
        percentileScore.lowerBound = 0.333333;
        percentileScore.upperBound = 0.50;
        percentileScore.score      = 2.0;
        percentileScores.add(percentileScore);  
        
        percentileScore = new PercentileScore();
        percentileScore.lowerBound = 0.50;
        percentileScore.upperBound = 0.80;
        percentileScore.score      = 3.0;
        percentileScores.add(percentileScore);  
        
        percentileScore = new PercentileScore();
        percentileScore.lowerBound = 0.80;
        percentileScore.upperBound = 1.00;
        percentileScore.score      = 4.0;
        percentileScores.add(percentileScore); 
        
        return percentileScores;
    }
    
    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

}
