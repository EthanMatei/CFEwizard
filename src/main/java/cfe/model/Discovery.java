package cfe.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.DISCOVERY)
public class Discovery extends CfeData implements Serializable {
	
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
		fieldMap.put("Ap Score", "apScore");
		fieldMap.put("AP Change", "apChange");
		fieldMap.put("DEscores", "deScores");
		fieldMap.put("DE Percentile", "dePercentile");
		fieldMap.put("DE Score", "deScore");
		fieldMap.put("DE change", "deChange");
	}

	private Double apScores;
	private Double apPercentile;
	private Integer apScore;
	private String apChange;
	private Double deScores;
	private Double dePercentile;
	private Integer	deScore;
	private String deChange;
	
	public String getFieldName(String name)	{
		return fieldMap.get(name.trim());		
	}

	public Double getApScores() {
		return apScores;
	}

	public void setApScores(Double apScores) {
		this.apScores = apScores;
	}

	public Double getApPercentile() {
		return apPercentile;
	}

	public void setApPercentile(Double apPercentile) {
		this.apPercentile = apPercentile;
	}

	public Integer getApScore() {
		return apScore;
	}

	public void setApScore(Integer apScore) {
		this.apScore = apScore;
	}

	public String getApChange() {
		return apChange;
	}

	public void setApChange(String apChange) {
		this.apChange = apChange;
	}

	public Double getDeScores() {
		return deScores;
	}

	public void setDeScores(Double deScores) {
		this.deScores = deScores;
	}

	public Double getDePercentile() {
		return dePercentile;
	}

	public void setDePercentile(Double dePercentile) {
		this.dePercentile = dePercentile;
	}

	public Integer getDeScore() {
		return deScore;
	}

	public void setDeScore(Integer deScore) {
		this.deScore = deScore;
	}

	public String getDeChange() {
		return deChange;
	}

	public void setDeChange(String deChange) {
		this.deChange = deChange;
	}
	
}