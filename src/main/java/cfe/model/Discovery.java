package cfe.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.DISCOVERY)
public class Discovery extends Model implements ModelInterface, Serializable {
	
	private static final long serialVersionUID = 3063001087206669093L;
	
	/**
	 * The convention is the first letter to be lower case
	 * We are writing like this so the the dynamic invocation creates setAccesionNumber instead of
	 * setaccessNumber (for example).
	 */
	public Discovery()	{
		super();
		
		fieldMap.put("APscores", "apScores");
		fieldMap.put("AP Percentile", "apPercentile");
		fieldMap.put("AP Score", "apScore");
		fieldMap.put("AP Change", "apChange");
		fieldMap.put("DEscores", "deScores");
		fieldMap.put("DE Percentile", "dePercentile");
		fieldMap.put("DE Score", "deScore");
		fieldMap.put("DE Change", "deChange");
	}

	private double apScores;
	private double apPercentile;
	private double apScore;
	private String apChange;
	private double deScores;
	private double dePercentile;
	private double deScore;
	private String deChange;

	
	// Getters and Setters:
	
	public double getApScores() {
		return apScores;
	}
	
	public void setApScores(double apScores) {
		this.apScores = apScores;
	}
	
	public double getApPercentile() {
		return apPercentile;
	}
	
	public void setApPercentile(double apPercentile) {
		this.apPercentile = apPercentile;
	}
	
	public double getApScore() {
		return apScore;
	}
	
	public void setApScore(double apScore) {
		this.apScore = apScore;
	}
	
	public String getApChange() {
		return apChange;
	}
	
	public void setApChange(String apChange) {
		this.apChange = apChange;
	}
	
	public double getDeScores() {
		return deScores;
	}
	
	public void setDeScores(double deScores) {
		this.deScores = deScores;
	}
	
	public double getDePercentile() {
		return dePercentile;
	}
	
	public void setDePercentile(double dePercentile) {
		this.dePercentile = dePercentile;
	}
	
	public double getDeScore() {
		return deScore;
	}
	
	public void setDeScore(double deScore) {
		this.deScore = deScore;
	}
	
	public String getDeChange() {
		return deChange;
	}
	
	public void setDeChange(String deChange) {
		this.deChange = deChange;
	}
	
}