package cfe.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

import cfe.action.DiscoveryAction;

public class CohortDataTable {
	
	private static final Log log = LogFactory.getLog(CohortDataTable.class);
    
	String key;
	List<String> columns;
	TreeMap<String, ArrayList<String>> data;
	
	public CohortDataTable() {
		columns = new ArrayList<String>();
		data = new TreeMap<String, ArrayList<String>>();
	}
	
	public void initialize(Table table) throws IOException {
		String tableName = table.getName();

		log.info("");
		log.info("INITIALIZE");
		for (Column col: table.getColumns()) {
			String columnName = col.getName();
			log.info("COLUMN NAME = \"" + columnName + "\"");
			if (columnName.trim().equalsIgnoreCase("PheneVisit")) {
				log.info("SETTING KEY...");
				key = columnName;
				columnName = tableName + "." + "PheneVisit";
			}
		    columns.add(columnName);
	    }
		
		log.info("KEY = " + key);
		
		Row row;
		while ((row = table.getNextRow()) != null) {
			String pheneVisit = row.getString(key);
			
			log.info("PHENE VISIT = " + pheneVisit);
			
			ArrayList<String> dataRow = new ArrayList<String>();
			for (String column: columns) {
				
				if (column.endsWith(".PheneVisit")) {
					column = key; // Reset compound PheneVisit columns names for data retrieval
				}
				
				log.info("COLUMN = " + column);
				
				Object obj = row.get(column);
				
				log.info("OBJ = " + obj);
				
				String type = "";
				String value = "";
				if (obj != null) { 
				    type = obj.getClass().getName();
				    value = obj.toString();
			    }
				log.info("VALUE = " + value);
				//dataRow.add(row.getString(column));
				dataRow.add(value);
			}
			data.put(pheneVisit, dataRow);
		}
    }
	
	public CohortDataTable merge(CohortDataTable mergeTable) {
		// Want to be able to merge - add columns and delete any rows that don't have PheneVisit in BOTH tables ???
		CohortDataTable merge = new CohortDataTable();
		merge.key = "PheneVisit";
		
		merge.columns.addAll(this.columns);
		merge.columns.addAll(mergeTable.columns);
		
		Set<String> keys1 = this.data.keySet();
		Set<String> keys2 = mergeTable.data.keySet();
		keys1.retainAll(keys2);
		for (String key: keys1) {
			ArrayList<String> mergedRow = new ArrayList<String>();
			mergedRow.addAll(this.data.get(key));
			mergedRow.addAll(mergeTable.data.get(key));
			merge.data.put(key, mergedRow);
		}
		
		return merge;
	}
	
	public String toCsv() {
		StringBuffer csv = new StringBuffer();
		csv.append(this.key);
		for (String column: this.columns) {
			csv.append("," + column);
		}
		csv.append("\n");
		
	    for (Map.Entry<String, ArrayList<String>> entry : this.data.entrySet()) {
	        csv.append(entry.getKey());
	        for (String value: entry.getValue()) {
	        	if (value.matches("-?\\d+")) {
	        		csv.append("," + value);
	        	}
	        	else if (value.matches("dddd-dd-ddTdd:dd")) {
	        		csv.append("," + value);
	        	}
	        	else {
	        	    csv.append("," + "\"" + value + "\"");
	        	}
	        }
	        csv.append("\n");
	    }
	    return csv.toString();
	}
}
