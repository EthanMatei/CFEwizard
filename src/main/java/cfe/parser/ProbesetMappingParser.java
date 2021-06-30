package cfe.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.DataType;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableMetaData;


public class ProbesetMappingParser {
	
    public static final String PROBE_SET_ID_COLUMN = "Probe Set ID";
    public static final String GENECARDS_SYMBOL_COLUMN = "Genecards Symbol";
    
	private String msAccessFile;
	private Database database;
	
	public ProbesetMappingParser(String msAccessFile) throws IOException {
	    this.msAccessFile = msAccessFile;
	    this.database = DatabaseBuilder.open(new File(msAccessFile));
	}
	
	public Table getMappingTable() throws Exception {
	    Table map = null;
	    ArrayList<String> genecardsTables = new ArrayList<String>();
	    
	    try {
	        Set<String> tableNames = this.database.getTableNames();
	        for (String tableName: tableNames) {
	            if (tableName.toLowerCase().contains("genecards")) {
	                genecardsTables.add(tableName);    
	            }
	        }
	    } catch (Exception exception) {
	        throw new Exception("Could not read probeset to gene mapping database: " + exception.getLocalizedMessage());
	    }
	    
	    if (genecardsTables.size() == 0) {
	        String message = "No mapping table found in probset to gene mapping database."
	                + " The database must contain one table that has the word \"genecards\" in its name"
	                + " that provides the mapping information.";
	        throw new Exception(message);
	    }
	    
	    if (genecardsTables.size() > 1) {
	        String message = "More than one mapping table found in probset to gene mapping database."
	                + " The database must contain only one table that has the word \"genecards\" in its name,"
	                + " which is used to get the mapping information.";
	        throw new Exception(message);
	    }
	    
	    map = this.database.getTable( genecardsTables.get(0) );
	
	    boolean hasProbesetIdColumn = false;
	    boolean hasGenecardsSymbolColumn = false;
	    
	    for (Column column: map.getColumns()) {
	        String columnName = column.getName();
	        if (columnName.equals(PROBE_SET_ID_COLUMN)) {
	            hasProbesetIdColumn = true;
	        }
	        else if (columnName.equals(GENECARDS_SYMBOL_COLUMN)) {
	            hasGenecardsSymbolColumn = true;
	        }
	    }
	    
	    if (!hasProbesetIdColumn) {
	        String message = "The probeset mapping database does not contain required column \""
	            + PROBE_SET_ID_COLUMN + "\".";
	        throw new Exception(message);
	    }
	    
	       
        if (!hasGenecardsSymbolColumn) {
            String message = "The probeset mapping database does not contain required column \""
                + GENECARDS_SYMBOL_COLUMN + "\".";
            throw new Exception(message);
        }
	    
	    return map;
	}

}
