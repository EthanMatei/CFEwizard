package cfe.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.DISORDER)
public class Disorder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id @GeneratedValue(strategy=IDENTITY)
    private Integer disorderId;
	private String domain;
	private String subdomain;
	private String relevantDisorder;
	
	public Disorder() {
		this.disorderId = null;
	    this.domain = "";
	    this.subdomain = "";
	    this.relevantDisorder = "";
	}
	
	public Disorder(String domain, String subdomain, String relevantDisorder) {
		this.disorderId = null;
		this.domain = domain;
		this.subdomain = subdomain;
		this.relevantDisorder = relevantDisorder;
	}
	
	//-----------------------------------------------------------------
	// Getters and Setters
	//-----------------------------------------------------------------
	public Integer getDisorderId() {
		return disorderId;
	}
	
	public void setDisorderId(Integer disorderId) {
		this.disorderId = disorderId;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getSubdomain() {
		return subdomain;
	}
	
	public void setSubdomain(String subdomain) {
		this.subdomain = subdomain;
	}
	
	public String getRelevantDisorder() {
		return relevantDisorder;
	}
	
	public void setRelevantDisorder(String relevantDisorder) {
		this.relevantDisorder = relevantDisorder;
	}
	
}