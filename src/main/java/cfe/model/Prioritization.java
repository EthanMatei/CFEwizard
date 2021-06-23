package cfe.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.CfeTables.TblNames;

@Entity
@Table(name=TblNames.PRIORITIZATION)
public class Prioritization extends Model implements ModelInterface, Serializable {
	
	private static final long serialVersionUID = 3063001087206669093L;
	
	/**
	 * The convention is the first letter to be lower case
	 * We are writing like this so the the dynamic invocation creates setAccesionNumber instead of
	 * setaccessNumber (for example).
	 */
	public Prioritization()	{
		super();
		
		fieldMap.put("HUBRAIN Score", "huBrainScore");
		fieldMap.put("HUBRAIN Info",  "huBrainInfo"); 
		fieldMap.put("HUPER Score", "huPerScore");
		fieldMap.put("HUPER Info",  "huPerInfo"); 
		fieldMap.put("HUGENEASSOC Score", "huGeneAssocScore");
		fieldMap.put("HUGENEASSOC Info", "huGeneAssocInfo"); 
		fieldMap.put("HUGCNV Score", "huGCnvScore");
		fieldMap.put("HUGCNV Info", "huGCnvInfo"); 
		fieldMap.put("NHBRAIN Score", "nhBrainScore");
		fieldMap.put("NHBRAIN Info", "nhBrainInfo");
		fieldMap.put("NHPER Score", "nhPerScore");
		fieldMap.put("NHPER Info", "nhPerInfo");
		fieldMap.put("NHGENEASSOC Score", "nhGeneAssocScore");
		fieldMap.put("NHGENEASSOC Info", "nhGeneAssocInfo"); 
		fieldMap.put("NHGCNV Score", "nhGCnvScore");
		fieldMap.put("NHGCNV Info", "nhGCnvInfo"); 
	}

    private double huBrainScore;
	private String huBrainInfo;
	private double huPerScore;
	private String huPerInfo; 
	private double huGeneAssocScore;
	private String huGeneAssocInfo; 
	private double huGCnvScore;
	private String huGCnvInfo; 
	private double nhBrainScore;
	private String nhBrainInfo;
	private double nhPerScore;
	private String nhPerInfo;
	private double nhGeneAssocScore;
	private String nhGeneAssocInfo; 
	private double nhGCnvScore;
	private String nhGCnvInfo;
	
	// Getters and Setters:
	
	public double getHuBrainScore() {
		return huBrainScore;
	}

	public void setHuBrainScore(double huBrainScore) {
		this.huBrainScore = huBrainScore;
	}
	
	public String getHuBrainInfo() {
		return huBrainInfo;
	}
	
	public void setHuBrainInfo(String huBrainInfo) {
		this.huBrainInfo = huBrainInfo;
	}
	
	public double getHuPerScore() {
		return huPerScore;
	}
	
	public void setHuPerScore(double huPerScore) {
		this.huPerScore = huPerScore;
	}
	
	public String getHuPerInfo() {
		return huPerInfo;
	}
	
	public void setHuPerInfo(String huPerInfo) {
		this.huPerInfo = huPerInfo;
	}
	
	public double getHuGeneAssocScore() {
		return huGeneAssocScore;
	}
	
	public void setHuGeneAssocScore(double huGeneAssocScore) {
		this.huGeneAssocScore = huGeneAssocScore;
	}
	
	public String getHuGeneAssocInfo() {
		return huGeneAssocInfo;
	}
	
	public void setHuGeneAssocInfo(String huGeneAssocInfo) {
		this.huGeneAssocInfo = huGeneAssocInfo;
	}
	
	public double getHuGCnvScore() {
		return huGCnvScore;
	}
	
	public void setHuGCnvScore(double huGCnvScore) {
		this.huGCnvScore = huGCnvScore;
	}
	
	public String getHuGCnvInfo() {
		return huGCnvInfo;
	}
	
	public void setHuGCnvInfo(String huGCnvInfo) {
		this.huGCnvInfo = huGCnvInfo;
	}
	
	public double getNhBrainScore() {
		return nhBrainScore;
	}
	
	public void setNhBrainScore(double nhBrainScore) {
		this.nhBrainScore = nhBrainScore;
	}
	
	public String getNhBrainInfo() {
		return nhBrainInfo;
	}
	
	public void setNhBrainInfo(String nhBrainInfo) {
		this.nhBrainInfo = nhBrainInfo;
	}
	
	public double getNhPerScore() {
		return nhPerScore;
	}
	
	public void setNhPerScore(double nhPerScore) {
		this.nhPerScore = nhPerScore;
	}
	
	public String getNhPerInfo() {
		return nhPerInfo;
	}
	
	public void setNhPerInfo(String nhPerInfo) {
		this.nhPerInfo = nhPerInfo;
	}
	
	public double getNhGeneAssocScore() {
		return nhGeneAssocScore;
	}
	
	public void setNhGeneAssocScore(double nhGeneAssocScore) {
		this.nhGeneAssocScore = nhGeneAssocScore;
	}
	
	public String getNhGeneAssocInfo() {
		return nhGeneAssocInfo;
	}
	
	public void setNhGeneAssocInfo(String nhGeneAssocInfo) {
		this.nhGeneAssocInfo = nhGeneAssocInfo;
	}
	
	public double getNhGCnvScore() {
		return nhGCnvScore;
	}
	
	public void setNhGCnvScore(double nhGCnvScore) {
		this.nhGCnvScore = nhGCnvScore;
	}
	
	public String getNhGCnvInfo() {
		return nhGCnvInfo;
	}
	
	public void setNhGCnvInfo(String nhGCnvInfo) {
		this.nhGCnvInfo = nhGCnvInfo;
	}

}