package cfe.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.TESTING)
public class Testing extends Model implements ModelInterface, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The convention is the first letter to be lower case
	 * We are writing like this so the the dynamic invocation creates setAccesionNumber instead of
	 * setaccessNumber (for example).
	 */
	public Testing()	{
		super();
		
		fieldMap.put("SMSLowMood Score", "smsLowMoodScore");
		fieldMap.put("HAMD Score", "hamdScore");
		fieldMap.put("First Year Depression Score", "firstYearDepressionScore");
		fieldMap.put("All Future Depression", "allFutureDepression");
	}

	double smsLowMoodScore;
	double hamdScore;
	double firstYearDepressionScore;
	double allFutureDepression;

	
	// Getters and Setters:
	
	public double getSmsLowMoodScore() {
		return smsLowMoodScore;
	}
	
	public void setSmsLowMoodScore(double smsLowMoodScore) {
		this.smsLowMoodScore = smsLowMoodScore;
	}
	
	public double getHamdScore() {
		return hamdScore;
	}
	
	public void setHamdScore(double hamdScore) {
		this.hamdScore = hamdScore;
	}
	
	public double getFirstYearDepressionScore() {
		return firstYearDepressionScore;
	}
	
	public void setFirstYearDepressionScore(double firstYearDepressionScore) {
		this.firstYearDepressionScore = firstYearDepressionScore;
	}
	
	public double getAllFutureDepression() {
		return allFutureDepression;
	}
	
	public void setAllFutureDepression(double allFutureDepression) {
		this.allFutureDepression = allFutureDepression;
	}
	
}