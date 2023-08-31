package cfe.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.healthmarketscience.jackcess.Table;

import cfe.parser.DiscoveryDatabaseParser;

public class TableCheckInfo {
    
    public static final String PHENE_VISIT_PATTERN = "^phchp\\d+v\\d+$|^CTBIN\\d+v\\d+$";
    
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

    public static int getErrorCount(List<TableCheckInfo> tableCheckInfos) {
        int count = 0;
        
        for (TableCheckInfo tableCheckInfo: tableCheckInfos) {
            if (tableCheckInfo.errors != null) {
                count += tableCheckInfo.errors.size();
            }
        }
        
        return count;
    }
    
    public static int getWarningCount(List<TableCheckInfo> tableCheckInfos) {
        int count = 0;
        
        for (TableCheckInfo tableCheckInfo: tableCheckInfos) {
            if (tableCheckInfo.warnings != null) {
                count += tableCheckInfo.warnings.size();
            }
        }
        
        return count;
    }
    
    public static List<TableCheckInfo> checkTestingDatabase(String testingDbFilePath) throws Exception {
        List<TableCheckInfo> tableCheckInfos = new ArrayList<TableCheckInfo>();
        
        DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(testingDbFilePath);
        
        Set<String> tableNames = new TreeSet<String>();         
        tableNames = dbParser.getTableNames();
        
        Map<String, List<String>> coreTableMap = new LinkedHashMap<String, List<String>>();
        coreTableMap.put(
                DiscoveryDatabaseParser.DEMOGRAPHICS_TABLE,
                Arrays.asList(DiscoveryDatabaseParser.DEMOGRAPHICS_REQUIRED_COLUMNS)
        );
        coreTableMap.put(
                DiscoveryDatabaseParser.DIAGNOSIS_TABLE,
                Arrays.asList(DiscoveryDatabaseParser.DIAGNOSIS_REQUIRED_COLUMNS)
        );              
        coreTableMap.put(
                DiscoveryDatabaseParser.SUBJECT_IDENTIFIERS_TABLE,
                Arrays.asList(DiscoveryDatabaseParser.SUBJECT_IDENTIFIERS_REQUIRED_COLUMNS)
        );  
        
        //----------------------------------------------------------------------
        // Check for required tables
        //----------------------------------------------------------------------
        for (Map.Entry<String, List<String>> entry : coreTableMap.entrySet()) {
            TableCheckInfo tableCheckInfo = new TableCheckInfo();
            
            String tableName = entry.getKey();
            // out.println("TABLE: \"" + tableName + "\"");
            tableCheckInfo.setName(tableName);

            if (!tableNames.contains(tableName)) {
                // out.println("    ERROR: this required table does not exist in the database.");
                tableCheckInfo.addError("This required table does not exist in the database.");
            }
            else {
                 Set<String> columns = dbParser.getTableColumnNames(tableName);
                 // out.println("    COLUMNS: " + String.join(", ", columns));
                 tableCheckInfo.setColumns(columns);
                 
                 List<String> requiredColumns = entry.getValue();
                 for (String requiredColumn: requiredColumns) {
                     if (!columns.contains(requiredColumn)) {
                         // out.println("    ERROR: required column \"" + requiredColumn + "\" was not found in the table");
                         tableCheckInfo.addError("Required column \"" + requiredColumn + "\" was not found in the table");
                     }
                 }
            }
            
            tableCheckInfos.add(tableCheckInfo);
        }
          
        Set<String> pheneTables = dbParser.getPheneTables();
        
        Map<String,ArrayList<ColumnInfo>> phenes = new TreeMap<String,ArrayList<ColumnInfo>>();
        
        phenes = dbParser.getTableColumnMap();

        //--------------------------------------------------------------
        // Check phene tables
        //--------------------------------------------------------------
        for (String pheneTable: pheneTables) {
            TableCheckInfo tableCheckInfo = new TableCheckInfo();
            
            tableCheckInfo.setName(pheneTable);
            
            Set<String> columns = dbParser.getTableColumnNames(pheneTable);
            // out.println("    COLUMNS: " + String.join(", ", columns));
            tableCheckInfo.setColumns(columns);
            
            Table dbTable = dbParser.getTable(pheneTable);
            DataTable dataTable = new DataTable();
            dataTable.initializeToAccessTable(dbTable);
            
            if (!dataTable.hasColumn("PheneVisit")) {
                // out.println(    "WARNING: Table \"" + pheneTable +"\" has no \"PheneVisit\" column.");
                tableCheckInfo.addWarning("Table \"" + pheneTable +"\" has no \"PheneVisit\" column.");
            }
            else {                    
                Set<String> pheneVisits = new HashSet<String>();
                for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
                    String pheneVisit = dataTable.getValue(i, "PheneVisit");
                    if (pheneVisits.contains(pheneVisit)) {
                        tableCheckInfo.addError("Duplicate phene visit \"" + pheneVisit + "\" on line " + (i+1) + ".");
                    }
                    else if (pheneVisit.matches(PHENE_VISIT_PATTERN)) {
                        pheneVisits.add(pheneVisit);
                    }
                    else {
                        tableCheckInfo.addError("Phene visit \"" + pheneVisit + "\" on line " + (i+1) + " has an incorrect format.");
                    }
                       
                }
            }
            tableCheckInfos.add(tableCheckInfo);
        }

        return tableCheckInfos;
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
