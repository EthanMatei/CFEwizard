package cfe.utils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class TableCheckInfo {
	private String name;
	private Set<String> columns;
	private List<String> errors;
	private List<String> warnings;

	public TableCheckInfo() {
		this.name      = "";
		this.columns   = new LinkedHashSet<String>();
		this.errors    = new ArrayList<String>();
		this.warnings  = new ArrayList<String>();
	}

	public void addError(String error) {
	    this.errors.add(error);
	}

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }	
	
    public String getColumnsString() {
        String columnsString = "\"" + String.join("\", \"", this.columns) + "\"";
        return columnsString;
    }
    
	//----------------------------------------------
	// Getters and Setters
	//----------------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getColumns() {
        return columns;
    }

    public void setColumns(Set<String> columns) {
        this.columns = columns;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

}
