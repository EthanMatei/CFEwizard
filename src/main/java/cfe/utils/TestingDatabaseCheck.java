package cfe.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.healthmarketscience.jackcess.Table;

import cfe.parser.DiscoveryDatabaseParser;

public class TestingDatabaseCheck {
    public static final String TESTING_DB_DIR = "/home/lab/shared/";
    public static final String TESTING_DB_FILE = "Testing Database MDH 12-12-2022.accdb";
    
    public static final String PHENE_VISIT_PATTERN = "^phchp\\d+v\\d+$|^CTBIN\\d+v\\d+$";
    
    public static void main(String[] args) {
        
        String dbFile = TESTING_DB_DIR + TESTING_DB_FILE;
        
        try {
            FileOutputStream file = new FileOutputStream("testing-db-check.txt");
            PrintWriter out = new PrintWriter(file);
            
            out.println("DATABASE: " + TESTING_DB_FILE);
            out.println();
            
            DiscoveryDatabaseParser dbParser = new DiscoveryDatabaseParser(dbFile);
            Set<String> pheneTables = dbParser.getPheneTables();
            String pheneTablesList = String.join(", ",  pheneTables);
            
            for (String pheneTable: pheneTables) {
                out.println("TABLE: \"" + pheneTable + "\"");
                Set<String> columns = dbParser.getTableColumnNames(pheneTable);
                System.out.println("    COLUMNS: " + String.join(", ", columns));
                
                Table dbTable = dbParser.getTable(pheneTable);
                DataTable dataTable = new DataTable();
                dataTable.initializeToAccessTable(dbTable);
                
                if (!dataTable.hasColumn("PheneVisit")) {
                    System.out.println(    "WARNING: Table \"" + pheneTable +"\" has no \"PheneVisit\" column.");
                }
                else {                    
                    Set<String> pheneVisits = new HashSet<String>();
                    for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
                        String pheneVisit = dataTable.getValue(i, "PheneVisit");
                        if (pheneVisits.contains(pheneVisit)) {
                            out.println("    ERROR: duplicate phene visit \"" + pheneVisit + "\".");
                        }
                        //else if (pheneVisit.matches("^phchp\\d+v\\d+$|^CTBIN\\d+v\\d+$")) {
                        else if (pheneVisit.matches(PHENE_VISIT_PATTERN)) {
                            pheneVisits.add(pheneVisit);
                        }
                        else {
                            out.println("    ERROR: phene visit \"" + pheneVisit + "\" has an incorrect format.");
                        }
                           
                    }
                }
                out.println();
            }
            out.close();
            System.out.println("DONE.");
        }
        catch (IOException ioException) {
            System.out.println("I/O error: " + ioException.getLocalizedMessage());
        }
        catch (Exception exception) {
            System.out.println("ERROR: " + exception.getLocalizedMessage());
        }
    }
}