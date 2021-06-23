package cfe.model.prioritization;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.prioritization.Tables.TblNames;

/**
 * This table contains the necessary data to calculate the score
 * It will be regenerated everytime there is a new database(s)
 * @author mtavares
 *
 */

@Entity
@Table(name=TblNames.SCORING_DATA)
public class ScoringData extends Model implements Serializable {

	private static final long serialVersionUID = 3453578434965917277L;
	
	private String tableName;
	private String srcTableName;
	
	// Scores
	// Human
	private double huBrainScore;
	private double huPerScore;
	private double huGeneCnvScore;
	private double huGeneAssocScore;
	private double huGeneLinkageScore;
	
	
	// Animal
	private double nhBrainScore;
	private double nhPerScore;
	private double nhGeneCnvScore;
	private double nhGeneAssocScore;
	private double nhGeneLinkageScore;
	

	
	public ScoringData() {hm = null;}
	
	@Column(length=30)
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@Column(length=30)
	public String getSrcTableName() {
		return srcTableName;
	}

	public void setSrcTableName(String srcTableName) {
		this.srcTableName = srcTableName;
	}

	public double getHuBrainScore() {
		return huBrainScore;
	}

	public void setHuBrainScore(double huBrainScore) {
		this.huBrainScore = huBrainScore;
	}

	public double getHuPerScore() {
		return huPerScore;
	}

	public void setHuPerScore(double huPerScore) {
		this.huPerScore = huPerScore;
	}

	public double getNhBrainScore() {
		return nhBrainScore;
	}

	public void setNhBrainScore(double nhBrainScore) {
		this.nhBrainScore = nhBrainScore;
	}

	public double getNhPerScore() {
		return nhPerScore;
	}

	public void setNhPerScore(double nhPerScore) {
		this.nhPerScore = nhPerScore;
	}

	public double getHuGeneAssocScore() {
		return huGeneAssocScore;
	}

	public void setHuGeneAssocScore(double huGeneAssocScore) {
		this.huGeneAssocScore = huGeneAssocScore;
	}

	public double getHuGeneCnvScore() {
		return huGeneCnvScore;
	}

	public void setHuGeneCnvScore(double huGeneCnvScore) {
		this.huGeneCnvScore = huGeneCnvScore;
	}

	public double getNhGeneCnvScore() {
		return nhGeneCnvScore;
	}

	public void setNhGeneCnvScore(double nhCnvScore) {
		this.nhGeneCnvScore = nhCnvScore;
	}

	public double getNhGeneAssocScore() {
		return nhGeneAssocScore;
	}

	public void setNhGeneAssocScore(double nhGeneAssocScore) {
		this.nhGeneAssocScore = nhGeneAssocScore;
	}

	public double getNhGeneLinkageScore() {
		return nhGeneLinkageScore;
	}

	public void setNhGeneLinkageScore(double nhGeneLinkageScore) {
		this.nhGeneLinkageScore = nhGeneLinkageScore;
	}

	public double getHuGeneLinkageScore() {
		return huGeneLinkageScore;
	}

	public void setHuGeneLinkageScore(double huGeneLinkageScore) {
		this.huGeneLinkageScore = huGeneLinkageScore;
	}

	
}
