package cfe.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

public class PheneVisitParser {
	public static final String COHORTS_TABLE = "Cohorts"; 
	public static final String DIAGNOSIS_TABLE = "Diagnosis"; 
	
	public List<String> getCohorts(String msAccessFile) throws Exception {
		List<String> cohorts = new ArrayList<String>();
		
	    Table table = DatabaseBuilder.open(new File(msAccessFile)).getTable(COHORTS_TABLE);
	    
	    for (Column column: table.getColumns()) {
	    	String columnName = column.getName();
	    	if (columnName.equalsIgnoreCase("PheneVisit") || columnName.equalsIgnoreCase("date")) {
	    		; // ignore these columns
	    	} else {
                cohorts.add(columnName);
	    	}
	    }

	    return cohorts;
	}
	
	public Map<String,String> getDiagnosisCodes(String msAccessFile) throws Exception {
		Map<String,String> diagnosisCodes = new TreeMap<String,String>();
		Map<String,HashSet<String>> codes = new TreeMap<String,HashSet<String>>();
		
		Database db = DatabaseBuilder.open(new File(msAccessFile));
	    Table table = db.getTable(DIAGNOSIS_TABLE);
	    for (Row row: table) {
	    	String value = row.getString("Primary DIGS DX");
	    	String key = row.getString("DxCode");
	    	if (key != null && !key.trim().contentEquals("")) {
	    		if (!codes.containsKey(key)) {
	    			codes.put(key, new HashSet<String>());
	    		}
	    		codes.get(key).add(value.trim());
	    	}
	    }
	    
	    for (Map.Entry<String, HashSet<String>> entry: codes.entrySet()) {
	    	String diagnosisCode = entry.getKey();
	    	Set<String> diagnoses = entry.getValue();
	    	String diagnosesString = String.join("; ", diagnoses);
	    	diagnosisCodes.put(diagnosisCode, diagnosesString);
	    }
	    
        return diagnosisCodes;
	}
}
