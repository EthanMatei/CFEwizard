package cfe.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;

import cfe.enums.Tables.TblNames;

@Entity
@Table(name=TblNames.VALIDATION)
public class Validation extends Model implements ModelInterface, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * The convention is the first letter to be lower case
	 * We are writing like this so the the dynamic invocation creates setAccesionNumber instead of
	 * setaccessNumber (for example).
	 */
	public Validation()	{
		super();
		
		fieldMap.put("Sig", "sig");
		fieldMap.put("Validation",  "validation"); 
	}

    private double sig;
	private String validation;

	
	// Getters and Setters:
	
	public double getSig() {
		return sig;
	}
	
	public void setSig(double sig) {
		this.sig = sig;
	}
	
	public String getValidation() {
		return validation;
	}
	
	public void setValidation(String validation) {
		this.validation = validation;
	} 

	
	
	

}