package cfe.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableMetaData;

import cfe.utils.CohortDataTable;
import cfe.utils.ColumnInfo;

public class DiscoveryDatabaseParser {
	
	public static final String COHORTS_TABLE = "Cohorts"; 
	public static final String DIAGNOSIS_TABLE = "Diagnosis";
	
	public Set<String> getPheneTables(String msAccessFile) throws Exception {
		Set<String> pheneTables = new TreeSet<String>();
		
		Database db = DatabaseBuilder.open(new File(msAccessFile));
		
		pheneTables = db.getTableNames();
		
		return pheneTables;
	}
	
	public Set<String> getMicroarrayTables(String msAccessFile) throws Exception {
		Set<String> microarrayTables = new TreeSet<String>();
		
		Database db = DatabaseBuilder.open(new File(msAccessFile));
		
		Set<String> tables = db.getTableNames();
		
		for (String table: tables) {
			if (table.toLowerCase().contains("microarray") || table.toLowerCase().contains("chip")) {
			    microarrayTables.add(table);	
			}
		}
		
		return microarrayTables;
	}
	
	public Map<String,ArrayList<String>> getPhenes(String msAccessFile) throws Exception {
		Set<String> pheneTables = new TreeSet<String>();
		Map<String,ArrayList<String>> phenes = new TreeMap<String,ArrayList<String>>();
		
		Database db = DatabaseBuilder.open(new File(msAccessFile));
		
		pheneTables = db.getTableNames();
		for (String pheneTable: pheneTables) {
			Table table = db.getTable(pheneTable);
			ArrayList<String> pheneNames = new ArrayList<String>();
			for (Column col: table.getColumns()) {
				String colName = col.getName().trim();
				DataType colDataType = col.getType();
				if (!colName.equalsIgnoreCase("PheneVisit")) {
				    pheneNames.add(colName + " (" + colDataType.toString() + ")");
				}
			};
			phenes.put(pheneTable, pheneNames);
		}
		
		return phenes;
	}
	
	public Map<String,ArrayList<ColumnInfo>> getTableColumnMap(String msAccessFile) throws Exception {
	    Set<String> excludedTables = new HashSet<String>();
	    excludedTables.add("Demographics");
	    excludedTables.add("Diagnosis");
	    excludedTables.add("Subject Identifiers");
	    
		Set<String> tableNames = new TreeSet<String>();
		Map<String,ArrayList<ColumnInfo>> map = new TreeMap<String,ArrayList<ColumnInfo>>();
		
		Database db = DatabaseBuilder.open(new File(msAccessFile));
		
		tableNames = db.getTableNames();
		for (String tableName: tableNames) {
			if (!excludedTables.contains(tableName)) {
			    Table table = db.getTable(tableName);
			    ArrayList<ColumnInfo> columnInfos = new ArrayList<ColumnInfo>();
			    for (Column col: table.getColumns()) {
				    String columnName = col.getName().trim();
				    DataType colDataType = col.getType();
				    if (!columnName.equalsIgnoreCase("PheneVisit")) {
					    ColumnInfo colInfo = new ColumnInfo();
					    colInfo.setTableName(tableName);
					    colInfo.setColumnName(columnName);
					    colInfo.setColumnType(colDataType.toString());
				        columnInfos.add(colInfo);
				    }
			    };
			    map.put(tableName, columnInfos);
			}
		}
		
		return map;
	}
	
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
	
	public void deleteTableRows(String msAccessFile, String tableName) throws IOException {
		Database db = DatabaseBuilder.open(new File(msAccessFile));
	    Table table = db.getTable(tableName);
	    for (Row row: table) {
	    	table.deleteRow(row);
	    }
	}
	
	public void addRows(String msAccessFile, String tableName, List<String[]> rowValues) throws IOException {
		Database db = DatabaseBuilder.open(new File(msAccessFile));
	    Table table = db.getTable(tableName);

	    table.addRows(rowValues);
	}


	/**
	 * Replaces the current rows in the specified table with the specified row values.
	 * 
	 * @param msAccessFile
	 * @param tableName
	 * @param rowValues
	 * @throws IOException
	 */
	public void replaceRows(String msAccessFile, String tableName, List<String[]> rowValues) throws IOException {
		this.deleteTableRows(msAccessFile, tableName);
		this.addRows(msAccessFile, tableName, rowValues);
	}
	
}
