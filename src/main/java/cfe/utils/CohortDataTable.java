package cfe.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.opencsv.CSVReader;

import cfe.action.DiscoveryAction;

/**
 * Class for storing the the merged data tables used to construct the Discovery cohort.
 * 
 * @author Jim Mullen
 *
 */
public class CohortDataTable extends DataTable {
	
	private static final Log log = LogFactory.getLog(CohortDataTable.class);
	
	public CohortDataTable() {
		super("PheneVisit");
	}
	
	public void initialize(Table table) throws IOException {

		// Reset key, because sometimes PheneVisit is misspelled.
		for (Column col: table.getColumns()) {
			String columnName = col.getName();
			if (columnName.trim().equalsIgnoreCase("PheneVisit")) {
				this.key = columnName;
				break;
			}
		}
		
		super.initialize(table);
		
		// Now, after table has been initialized, make sure key and column name are "PheneVisit"
		this.key = "PheneVisit";
		for (int i = 0; i < this.columns.size(); i++) {
			if (columns.get(i).trim().equalsIgnoreCase("PheneVisit")) {
				columns.set(i, "PheneVisit");
				break;
			}
		}
    }
	
	public CohortDataTable merge(CohortDataTable mergeTable) throws Exception {
		if (this.key == null || mergeTable.key == null) {
			throw new Exception("Merge of tables without a key defined.");
		}
		
		CohortDataTable merge = new CohortDataTable();
		
		merge.keyIndex = this.keyIndex;
		
		log.info("MERGE KEY INDEX: " + merge.keyIndex);
		
		ArrayList<String> columns1 = new ArrayList<String>();
		for (String columnName: this.columns) {
			if (columnName.equals(key)) {
				columnName = this.name + "." + columnName;
			}
			columns1.add(columnName);
		}
		
		ArrayList<String> columns2 = new ArrayList<String>();
		for (String columnName: mergeTable.columns) {
			if (columnName.equals(key)) {
				columnName = mergeTable.name + "." + columnName;
			}
			columns2.add(columnName);
		}	
		
		merge.columns.addAll(columns1);
		merge.columns.addAll(columns2);
		
		String columnsString = String.join(", ", merge.columns);
		log.info("Merged columns: " + columnsString);
		
		Set<String> keys1 = this.index.keySet();
		Set<String> keys2 = mergeTable.index.keySet();
		keys1.retainAll(keys2);
		
		for (String key: keys1) {
			ArrayList<String> mergedRow = new ArrayList<String>();
			mergedRow.addAll(this.index.get(key));
			mergedRow.addAll(mergeTable.index.get(key));
			merge.addRow(mergedRow);
		}
		
		return merge;
	}
	

	/**
	 * Create 4 columns: Subject, PheneVisit, DiscoveryCohort, Date
	 * Sort by Subject, and then PheneVisit
	 * 
	 * DiscoveryCohort is 1 if subject is in cohort, date is the date of the visit.
	 * 
	 * @param low
	 * @param high
	 * @param phene
	 */
	public CohortTable getCohort(String phene, int lowCutoff, int highCutoff) {
		// Get the subject column index
		int subjectIndex = -1;
		for (int i = 0; i < this.columns.size(); i++) {
			if (columns.get(i).trim().equalsIgnoreCase("Subject")) {
				subjectIndex = i;
				break;
			}
		}
		
		// Get the selected phene column index
		int pheneIndex = -1;
		for (int i = 0; i < this.columns.size(); i++) {
			if (columns.get(i).trim().equalsIgnoreCase(phene.trim())) {
				pheneIndex = i;
				break;
			}
		}
		
		// Get the "visit date" column index
		int visitDateIndex = -1;
		for (int i = 0; i < this.columns.size(); i++) {
			if (columns.get(i).trim().equalsIgnoreCase("Visit Date")) {
				visitDateIndex = i;
				break;
			}
		}
		
		// Find subjects with low score and subjects with high score
		TreeSet<String> lowScoreSubjects   = new TreeSet<String>();
		TreeSet<String> highScoreSubjects  = new TreeSet<String>();

	    for (ArrayList<String> row : this.data) {
			String pheneValueString = row.get(pheneIndex);
			String subject = row.get(subjectIndex);
			if (pheneValueString != null) {
				pheneValueString = pheneValueString.trim();
				if (pheneValueString.matches("\\d+")) {
					int pheneValue = Integer.parseInt(pheneValueString);
					
					if (pheneValue <= lowCutoff) {
						lowScoreSubjects.add(subject);
					}
					
					if (pheneValue >= highCutoff) {
						highScoreSubjects.add(subject);
					}
				}
			}
	    }
	    
	    // Set cohort subjects as the intersection of subject sets with low and high phene scores, i.e., 
	    // subjects with both low and high phene values, minus subjects that have intermediate scores
	    TreeSet<String> cohortSubjects = lowScoreSubjects;
	    cohortSubjects.retainAll(highScoreSubjects);    // intersection of sets of users with low and high scores
	    
	    //---------------------------------------------------------------------------
	    // Calculate the number of low and high visits for subjects in the cohort
	    //---------------------------------------------------------------------------
		int lowVisits          = 0;
		int highVisits         = 0;
		
	    for (ArrayList<String> row : this.data) {
			String pheneValueString = row.get(pheneIndex);
			String subject = row.get(subjectIndex);
			if (cohortSubjects.contains(subject) && pheneValueString != null) {
				pheneValueString = pheneValueString.trim();
				// If the phene value is an integer
				if (pheneValueString.matches("\\d+")) {
					int pheneValue = Integer.parseInt(pheneValueString);
					
					if (pheneValue <= lowCutoff) {
						lowVisits++;
					}
					
					if (pheneValue >= highCutoff) {
						highVisits++;
					}
				}
			}
	    }
	    
	    //-----------------------------------------
	    // Create the cohort table
	    //-----------------------------------------
	    CohortTable cohort = new CohortTable();
	    
	    cohort.setSubjects(cohortSubjects);
	    cohort.setLowVisits(lowVisits);
	    cohort.setHighVisits(highVisits);
	    
	    cohort.key = "PheneVisit";
	    cohort.columns.add("Subject");
	    cohort.columns.add("PheneVisit");
	    cohort.columns.add("DiscoveryCohort");
	    cohort.columns.add("date");
	    
	    log.info("Going to add data rows to cohort...");
	    
	    for (ArrayList<String> cohortDataRow: this.data) {
	        ArrayList<String> cohortRow = new ArrayList<String>();
	        
	        String subject = cohortDataRow.get(subjectIndex);
	        String keyValue = cohortDataRow.get(this.keyIndex);
	        
	        cohortRow.add(subject);
	        cohortRow.add(keyValue);
	        
	        if (cohortSubjects.contains(subject)) {
				String pheneValueString = cohortDataRow.get(pheneIndex);
				pheneValueString = pheneValueString.trim();
				
			  	if (pheneValueString.matches("-?\\d+")) {
		            int pheneValue = Integer.parseInt(pheneValueString);
		        
	        	    if (pheneValue <= lowCutoff) {
	        	        cohortRow.add("1");
	        	    }
	        	    else if (pheneValue >= highCutoff) {
	        	        cohortRow.add("1");
	        	    }
	        	    else {
	        		    // Intermediate phene value
	        	        cohortRow.add("0.5");
	        	    }
			  	}
	        	else {
	        	    // Blank or non-numeric phene value
	        	    cohortRow.add("0");
	        	}
	        }
	        else {
	        	cohortRow.add("0");
	        }
	        cohortRow.add(cohortDataRow.get(visitDateIndex));
	        cohort.addRow(cohortRow);
	    }
	    
	    return cohort;
	}
	
	public XSSFWorkbook toXlsx(String phene, int lowCutoff, int highCutoff) {
        XSSFWorkbook workbook = super.toXlsx();
        
        
        return workbook;
	}
		
}
