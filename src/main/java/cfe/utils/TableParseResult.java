package cfe.utils;

import java.util.ArrayList;
import java.util.List;

public class TableParseResult {

	public enum Status {PROCESSED, IGNORED, ERROR};
	
	private String name;
	private Status status; 
	private List<String> issues;

	public TableParseResult(String name) {
		this.status = Status.IGNORED;
		this.name = name;
		this.issues = new ArrayList<String>();
	}

	public void addIssue(String issue) {
	    this.issues.add(issue);
	}
	
	public List<String> getIssues() {
	    return this.issues;
	}

	public String getName() {
		return this.name;
	}
	
	public String getStatus() {
		String status = "";
		switch (this.status) {
		    case PROCESSED:
			    status = "processed";
			    break;
		    case IGNORED:
		    	status = "ignored";
		    	break;
		    case ERROR:
		    	status = "error";
		    	break;
		}
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
}
