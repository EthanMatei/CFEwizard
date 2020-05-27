package cfg.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ParseResult {

	private String fileName;
	private Map<String, TableParseResult> tableParseResults;  // Map from table name table parse result

	public ParseResult(String fileName) {
		this.fileName = fileName;
		this.tableParseResults = new TreeMap<String, TableParseResult>();
	}
	
	public void addTableParseResult(String tableName) {
		TableParseResult tableParseResult = new TableParseResult(tableName);
	    this.tableParseResults.put(tableName, tableParseResult);
	}
	
	public void addTableIssue(String table, String issue) {
		this.tableParseResults.get(table).addIssue(issue);
	}
	
	public void setTableStatus(String table, TableParseResult.Status status) {
	    this.tableParseResults.get(table).setStatus(status);
	}
	
	public List<String> getTableIssues(String table) {
		List<String> issues = this.tableParseResults.get(table).getIssues();
	    return issues;
	}
	
	public String getFileName() {
	    return this.fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public List<TableParseResult> getTableParseResults() {
		Collection values = this.tableParseResults.values();
		
		List<TableParseResult> results = new ArrayList<TableParseResult>( values );
		return results;
	}
}
