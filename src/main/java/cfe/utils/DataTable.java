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

/**
 * Class for representing a table of data with named columns.
 * 
 * @author Jim Mullen
 *
 */
public class DataTable {
	
	private static final Log log = LogFactory.getLog(DataTable.class);
    
	protected String name; // name of the table
	protected String key;  // primary key column name
	protected Map<String, ArrayList<String>> index; // primary key index
	protected List<String> columns;
	protected List<ArrayList<String>> data;
	
	public DataTable(String key) {
		columns = new ArrayList<String>();
		data = new ArrayList<ArrayList<String>>();
		this.key = key;
		this.index = new TreeMap<String, ArrayList<String>>();
	}
	
	/**
	 * Initializes with MS Access table.
	 * 
	 * @param table
	 * @throws IOException
	 */
	public void initialize(Table table) throws IOException {
		this.name = table.getName();

		for (Column col: table.getColumns()) {
			String columnName = col.getName();
		    columns.add(columnName);
	    }
		
		String keyValue = null;
		Row row;
		while ((row = table.getNextRow()) != null) {
			
			ArrayList<String> dataRow = new ArrayList<String>();
			for (String column: columns) {
				keyValue = null;

				Object obj = row.get(column);
				
				String type = "";
				String value = "";
				if (obj != null) { 
				    type = obj.getClass().getName();
				    value = obj.toString();
					if (key != null && column.contentEquals(key)) {
						keyValue = value;
					}
			    }
				
				//dataRow.add(row.getString(column));
				dataRow.add(value);
			}
			
			if (keyValue != null) {
			    this.index.put(keyValue, dataRow);
			}
			data.add(dataRow);
		}
    }
	
	public List<String[]> getValuesAsListOfArrays() {
		List<String[]> values = new ArrayList<String[]>();
		
	    for (ArrayList<String> dataRow : this.data) {
	        String[] row = dataRow.toArray(new String[dataRow.size()]);
	        values.add(row);
	    }
	    
		return values;
	}
	
	/**
	 * Merges data tables based on key. Key columns are renamed to be prefixed with the table name.
	 * @param mergeTable
	 * @return
	 */
	public DataTable merge(DataTable mergeTable) throws Exception {
		if (this.key == null || mergeTable.key == null) {
			throw new Exception("Merge of tables without a key defined.");
		}
		
		DataTable merge = new DataTable(this.key);
		
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
				columnName = this.name + "." + columnName;
			}
			columns2.add(columnName);
		}
		
		merge.columns.addAll(columns1);
		merge.columns.addAll(columns2);
		
		Set<String> keys1 = this.index.keySet();
		Set<String> keys2 = mergeTable.index.keySet();
		keys1.retainAll(keys2);
		
		for (String key: keys1) {
			ArrayList<String> mergedRow = new ArrayList<String>();
			mergedRow.addAll(this.index.get(key));
			mergedRow.addAll(mergeTable.index.get(key));
			merge.data.add(mergedRow);
			merge.index.put(key, mergedRow);
		}
		
		return merge;
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
		
	    for (ArrayList<String> dataRow: this.data) {
	        //csv.append(entry.getKey());
	        first = true;
	        for (String value: dataRow) {
	        	if (first) {
	        		first = false;
	        	}
	        	else {
	        		csv.append(",");
	        	}
	        	
	        	if (value.matches("-?\\d+")) {
	        		csv.append(value);
	        	}
            	else if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {
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

	    for (ArrayList<String> dataRow: this.data) {
        	rowNumber++;
            xlsxRow = sheet.createRow(rowNumber);
            
            for (int i = 0; i < dataRow.size(); i++){
            	String value = dataRow.get(i);
            	
            	if (value == null) value = "";
            	
            	if (value.matches("-?\\d+")) {
            	    int ivalue = Integer.parseInt(value);
                    xlsxRow.createCell(i).setCellValue(ivalue);
            	}
            	else if (value.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {
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
