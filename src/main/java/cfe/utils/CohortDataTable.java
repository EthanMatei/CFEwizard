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

		for (Column col: table.getColumns()) {
			String columnName = col.getName();
			if (columnName.trim().equalsIgnoreCase("PheneVisit")) {
				key = columnName;
				columnName = tableName + "." + "PheneVisit";
			}
		    columns.add(columnName);
	    }
		
		Row row;
		while ((row = table.getNextRow()) != null) {
			String pheneVisit = row.getString(key);
			
			ArrayList<String> dataRow = new ArrayList<String>();
			for (String column: columns) {
				
				if (column.endsWith(".PheneVisit")) {
					column = key; // Reset compound PheneVisit columns names for data retrieval
				}
				
				Object obj = row.get(column);
				
				String type = "";
				String value = "";
				if (obj != null) { 
				    type = obj.getClass().getName();
				    value = obj.toString();
			    }
				
				//dataRow.add(row.getString(column));
				dataRow.add(value);
			}
			data.put(pheneVisit, dataRow);
		}
    }
	
	public List<String[]> getValuesAsListOfArrays() {
		List<String[]> values = new ArrayList<String[]>();
		
	    for (Map.Entry<String, ArrayList<String>> entry : this.data.entrySet()) {
	        ArrayList<String> dataRow = entry.getValue();
	        String[] row = dataRow.toArray(new String[dataRow.size()]);
	        values.add(row);
	    }
	    
		return values;
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
	public CohortDataTable getCohort(String phene, int lowCutoff, int highCutoff) {
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
		
	    for (Map.Entry<String, ArrayList<String>> entry : this.data.entrySet()) {
	    	ArrayList<String> row = entry.getValue();
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
	    
	    CohortDataTable cohort = new CohortDataTable();
	    
	    cohort.key = "PheneVisit";
	    cohort.columns.add("Subject");
	    cohort.columns.add("PheneVisit");
	    cohort.columns.add("DiscoveryCohort");
	    cohort.columns.add("date");
	    
	    for (Map.Entry<String, ArrayList<String>> entry : this.data.entrySet()) {
	        ArrayList<String> cohortRow = new ArrayList<String>();
	        ArrayList<String> cohortDataRow = entry.getValue();
	        
	        String subject = cohortDataRow.get(subjectIndex);
	        
	        cohortRow.add(subject);
	        cohortRow.add(entry.getKey());
	        
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
	        cohort.data.put(entry.getKey(), cohortRow);
	    }
	    
	    return cohort;
	}
	
	
	public String toCsv() {
		StringBuffer csv = new StringBuffer();
		//csv.append(this.key);
		boolean first = true;
		for (String column: this.columns) {
			if (first) {
			    csv.append(column);
			    first = false;
			}
			else {
			    csv.append("," + column);
			}
		}
		csv.append("\n");
		
	    for (Map.Entry<String, ArrayList<String>> entry : this.data.entrySet()) {
	        //csv.append(entry.getKey());
	        first = true;
	        for (String value: entry.getValue()) {
	        	if (first) {
	        		first = false;
	        	}
	        	else {
	        		csv.append(",");
	        	}
	        	
	        	value = value.trim();
	        	
	        	if (value.matches("^-?\\d+$")) {
	        		csv.append(value);
	        	}
            	else if (value.matches("^-?\\d+\\.\\d*$")) {
	        		csv.append(value);
            	}
	          	else if (value.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
	        		csv.append(value);
	        	}
	        	else {
	        	    csv.append("\"" + value + "\"");
	        	}
	        }
	        csv.append("\n");
	    }
	    return csv.toString();
	}
	
	public XSSFWorkbook toXlsx() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("data");
        
        CreationHelper createHelper = workbook.getCreationHelper();
        
        // Header row
        int rowNumber = 0;
        XSSFRow xlsxRow = sheet.createRow(rowNumber);
        for (int i = 0; i < columns.size(); i++) {
            xlsxRow.createCell(i).setCellValue(columns.get(i));
        }

        for (Map.Entry<String, ArrayList<String>> entry : this.data.entrySet()) {
        	rowNumber++;
            xlsxRow = sheet.createRow(rowNumber);
            
            ArrayList<String> dataRow = entry.getValue();
            
            for (int i = 0; i < dataRow.size(); i++){
            	String value = dataRow.get(i);
            	
            	if (value == null) value = "";
            	value = value.trim();
            	
            	if (value.matches("^-?\\d+$")) {
            		// Integer
            	    int ivalue = Integer.parseInt(value);
                    xlsxRow.createCell(i).setCellValue(ivalue);
            	}
            	else if (value.matches("^-?\\d+\\.\\d*$")) {
            		double dvalue = Double.parseDouble(value);
            		xlsxRow.createCell(i).setCellValue(dvalue);
            	}
            	else if (value.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
            		// Timestamp
            		LocalDateTime dateTime = LocalDateTime.parse(value);
            		xlsxRow.createCell(i).setCellValue(dateTime);
                    CellStyle cellStyle = workbook.createCellStyle();  
                    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yy")); 
                    		//.getFormat("m/d/yy h:mm"));  
            		xlsxRow.getCell(i).setCellStyle(cellStyle);
            	}
            	else {
                    xlsxRow.createCell(i).setCellValue(value);
            	}
            }
        }

		return workbook;
	}
	
}
