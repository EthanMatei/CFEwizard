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

public class AccessDatabaseParser {

	protected String msAccessFileName;
	protected Database database;
	
	public AccessDatabaseParser(String msAccessFileName) throws IOException {
	    this.msAccessFileName = msAccessFileName;
	    this.database = DatabaseBuilder.open(new File(msAccessFileName));
	}
	
	public AccessDatabaseParser(File msAccessFile) throws IOException {
	    this.database = DatabaseBuilder.open(msAccessFile);
	}
	
	public Set<String> getTableNames() throws Exception {
		Set<String> tableNames = new TreeSet<String>();

		tableNames = this.database.getTableNames();
		
		return tableNames;
	}
	
	public Set<String> getTableColumnNames(String tableName) throws Exception {
	    Set<String> columnNames = new LinkedHashSet<String>();
	    
	    Table table = this.database.getTable(tableName);
	    
	    if (table == null) {
	        String errorMessage = "Table \"" + tableName + "\" could not be found in database \""
	                + this.msAccessFileName + "\".";
	        throw new Exception(errorMessage);
	    }
	    
        for (Column column: table.getColumns()) {
            String columnName = column.getName().trim();
            columnNames.add(columnName);
        };

	    return columnNames;
	}
	
	/**
	 * Indicates if the specified table name contains the specified column name.
	 * 
	 * @param tableName
	 * @param columnName
	 * @return
	 * @throws Exception
	 */
	public boolean tableHasColumn(String tableName, String columnName) throws Exception {
	    boolean hasColumn = false;

	    Set<String> columnNames = this.getTableColumnNames(tableName);
	    if (columnNames.contains(columnName)) {
	        hasColumn = true;
	    }

	    return hasColumn;
	}

	
	public Map<String,ArrayList<ColumnInfo>> getTableColumnMap(Set<String> excludedTables) throws Exception {
	    
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
				    //if (!columnName.equalsIgnoreCase("PheneVisit")) {
					    ColumnInfo colInfo = new ColumnInfo();
					    colInfo.setTableName(tableName);
					    colInfo.setColumnName(columnName);
					    colInfo.setColumnType(colDataType.toString());
				        columnInfos.add(colInfo);
				    //}
			    };
			    map.put(tableName, columnInfos);
			}
		}
		
		return map;
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
	
	public Table getTable(String tableName) throws IOException {
	    Table table = this.database.getTable(tableName);
	    return table;
	}

    public String getMsAccessFileName() {
        return msAccessFileName;
    }

}
