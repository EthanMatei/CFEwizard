package cfe.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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

/**
 * Discovery (PheneVisits) database parser. This class should be modified to extend the
 * AccessDatabaseParser class.
 * 
 * @author Jim Mullen
 *
 */
public class DiscoveryDatabaseParser {

    // "Core" tables (required for every discovery phase)
	public static final String DIAGNOSIS_TABLE = "Diagnosis";
	public static final String DEMOGRAPHICS_TABLE = "Demographics";
	public static final String SUBJECT_IDENTIFIERS_TABLE = "Subject Identifiers";
	
	private String msAccessFile;
	private Database database;
	
	public DiscoveryDatabaseParser(String msAccessFile) throws IOException {
	    this.msAccessFile = msAccessFile;
	    this.database = DatabaseBuilder.open(new File(msAccessFile));
	}
	
	/**
	 * Checks for missing and/or incorrect data in the core tables that are used in all Discovery phases
	 * (irrespective of the phene or microarray table selected), and throws an exception if there is a problem.
	 * 
	 * @param msAccessFile the MS Access database file to be checked.
	 */
	public void checkCoreTables() throws Exception {
        
	    // Get the names of tables in the database
        Set<String> pheneTables = new TreeSet<String>();	        
	    pheneTables = this.database.getTableNames();
	    
	    Map<String, LinkedHashSet<String>> tableInfo = new LinkedHashMap<String, LinkedHashSet<String>>();

	    
	    // Diagnosis Table Info
	    String[] diagnosisColumnArray = {
	            "PheneVisit", "Primary DIGS DX", "Specifiers", "DxCode", "Per Chip", "Confidence Rating", "DIGS Rater", "Other Dx"
	    };
	    LinkedHashSet<String> diagnosisColumns = new LinkedHashSet<String>(Arrays.asList(diagnosisColumnArray));
	    tableInfo.put(DIAGNOSIS_TABLE, diagnosisColumns);
	    
        // Demographics Table Info
        String[] demographicsColumnArray = {
                "PheneVisit", "Gender(M/F)", "Age at testing (Years)", "Age at Onset of Illness", "Race/Ethnicity"
        };
        LinkedHashSet<String> demographicsColumns = new LinkedHashSet<String>(Arrays.asList(demographicsColumnArray));
        tableInfo.put(DEMOGRAPHICS_TABLE, demographicsColumns);	    

        // Subject Identifiers Table Info
        String[] subjectIdentifiersColumnArray = {
                "Subject", "Vet/Non-Vet?"
        };
        LinkedHashSet<String> subjectIdentifiersColumns = new LinkedHashSet<String>(Arrays.asList(subjectIdentifiersColumnArray));
        tableInfo.put(SUBJECT_IDENTIFIERS_TABLE, subjectIdentifiersColumns);     
        
	    for (Map.Entry<String, LinkedHashSet<String>> entry : tableInfo.entrySet()) {
	        String tableName = entry.getKey();
	        
	        // Check that the table exists
	        if (!pheneTables.contains(tableName)) {
	            throw new Exception("The following required table is missing from the database: \"" + tableName + "\"");           
	        }
	        
	        Table table = this.database.getTable(tableName);
	        
	        LinkedHashSet<String> requiredColumns = entry.getValue();
	        
	        Set<String> columnNames = new LinkedHashSet<String>();
	        for (Column column: table.getColumns()) {
	            String columnName = column.getName();
	            columnNames.add(columnName);
	        }
	        
	        //String columnsString = String.join(",", columnNames);
	        
	        for (String requiredColumn: requiredColumns) {
	            if (!columnNames.contains(requiredColumn)) {
	                throw new Exception("Table \"" + tableName + "\" is missing required column \"" + requiredColumn + "\".");
	            }
	        }
	    }
	    
	}
	
	public void checkPheneTable(String pheneTableName) throws Exception {
	    Table pheneTable = this.database.getTable(pheneTableName);
	    
        Set<String> columnNames = new LinkedHashSet<String>();
        for (Column column: pheneTable.getColumns()) {
            String columnName = column.getName();
            columnNames.add(columnName);
        }
        
        if (!columnNames.contains("PheneVisit")) {
            throw new Exception("The phene table \"" + pheneTableName + "\" is missing column \"PheneVisit\"");
        }
	}
	
	public Set<String> getPheneTables() throws Exception {
		Set<String> pheneTables = new TreeSet<String>();

		pheneTables = this.database.getTableNames();
		
		return pheneTables;
	}
	
	public Set<String> getMicroarrayTables() throws Exception {
		Set<String> microarrayTables = new TreeSet<String>();
		
		Set<String> tables = this.database.getTableNames();
		
		for (String table: tables) {
			if (table.toLowerCase().contains("microarray") || table.toLowerCase().contains("chip")) {
			    microarrayTables.add(table);	
			}
		}

		return microarrayTables;
	}
	
	public Map<String,ArrayList<String>> getPhenes() throws Exception {
		Set<String> pheneTables = new TreeSet<String>();
		Map<String,ArrayList<String>> phenes = new TreeMap<String,ArrayList<String>>();
		
		pheneTables = this.database.getTableNames();
		for (String pheneTable: pheneTables) {
			Table table = this.database.getTable(pheneTable);
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
	
	public Map<String,ArrayList<ColumnInfo>> getTableColumnMap() throws Exception {
	    Set<String> excludedTables = new HashSet<String>();
	    excludedTables.add("Demographics");
	    excludedTables.add("Diagnosis");
	    excludedTables.add("Subject Identifiers");
	    
		Set<String> tableNames = new TreeSet<String>();
		Map<String,ArrayList<ColumnInfo>> map = new TreeMap<String,ArrayList<ColumnInfo>>();
		
		tableNames = this.database.getTableNames();
		for (String tableName: tableNames) {
			if (!excludedTables.contains(tableName)) {
			    Table table = this.database.getTable(tableName);
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

	
	public Map<String,String> getDiagnosisCodes() throws Exception {
		Map<String,String> diagnosisCodes = new TreeMap<String,String>();
		Map<String,HashSet<String>> codes = new TreeMap<String,HashSet<String>>();

	    Table table = this.database.getTable(DIAGNOSIS_TABLE);
		
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
	
	public void deleteTableRows(String tableName) throws IOException {

	    Table table = this.database.getTable(tableName);
	    for (Row row: table) {
	    	table.deleteRow(row);
	    }
	}
	
	public void addRows(String tableName, List<String[]> rowValues) throws IOException {
	    Table table = this.database.getTable(tableName);

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
	public void replaceRows(String tableName, List<String[]> rowValues) throws IOException {
		this.deleteTableRows(tableName);
		this.addRows(tableName, rowValues);
	}
	
	public Database getDatabase() {
	    return this.database;
	}
	
}
