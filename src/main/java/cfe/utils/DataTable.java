package cfe.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
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
	protected int keyIndex; // primary key column number
	protected Map<String, ArrayList<String>> index; // primary key index
	protected List<String> columns;
	protected List<ArrayList<String>> data;
	
	public DataTable(String key) {
		columns = new ArrayList<String>();
		data = new ArrayList<ArrayList<String>>();
		this.key = key;
		this.index = new TreeMap<String, ArrayList<String>>();
	}
	
    public void initializeToCsv(String csvFile) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(csvFile));
		CSVReader csvReader = new CSVReader(reader);  
		
		String[] header = csvReader.readNext();
		
		if (header != null && header.length > 0) {
		    for (String columnName: header) {
		        this.columns.add(columnName);    
		    }
		    
		    String[] line;
	        while ((line = csvReader.readNext()) != null) {
	            this.addRow(line);
	        }
		}
		
		csvReader.close();
	}
	
	
	/**
	 * Initializes with MS Access table.
	 * 
	 * @param table
	 * @throws IOException
	 */
	public void initialize(Table table) throws IOException {
		this.name = table.getName();

		int columnIndex = 0;
		for (Column col: table.getColumns()) {
			String columnName = col.getName();
		    columns.add(columnName);
		    if (columnName.equals(this.key)) {
		    	this.keyIndex = columnIndex;
		    }
		    columnIndex++;
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
					if (key != null && column.equals(key)) {
						keyValue = value;
					}
			    }
				
				//dataRow.add(row.getString(column));
				dataRow.add(value);
			}
			
			this.addRow(dataRow);
		}
    }
	
	/**
	 * Inserts the column at the specified position.
	 * 
	 * @param name the name of the column to add.
	 * @param position the position to place the column.
	 * @param initialValue
	 */
	public void insertColumn(String name, int position, String initialValue) {
	    this.columns.add(position, name);
	    for (ArrayList<String> row: this.data) {
	    	row.add(position, initialValue);
	    }
	}
	
	public void addRow(String[] row) {
	    ArrayList<String> rowAsList = new ArrayList<String>(Arrays.asList(row));
	    this.addRow(rowAsList);    
	}
	
	public void addRow(ArrayList<String> row) {
		data.add(row);
		
		// Update index
		if (this.key != null) {
			String keyValue = row.get(this.keyIndex);
		    if (keyValue != null) {
		        this.index.put(keyValue, row);
		    }
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
	
	public void sort(String columnName)
	{
		int index = this.columns.indexOf(columnName);
		
        Collections.sort(this.data, new Comparator<ArrayList<String>>() {
	            @Override
	            public int compare(ArrayList<String> row1, ArrayList<String> row2) {
	                return row1.get(index).compareTo(row2.get(index));
	            }
	        });
	}

	public void sort(String column1, String column2)
	{
		int index1 = this.columns.indexOf(column1);
		int index2 = this.columns.indexOf(column2);
		
        Collections.sort(this.data, new Comparator<ArrayList<String>>() {
	            @Override
	            public int compare(ArrayList<String> row1, ArrayList<String> row2) {
	            	int compare = row1.get(index1).compareTo(row2.get(index1));
	            	if (compare == 0) {
	            		compare = row1.get(index2).compareTo(row2.get(index2));
	            	}
	                return compare;
	            }
	        });
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
		merge.keyIndex = this.keyIndex;
		
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
	
	/**
	 * Creates a workbook from multiple DataTable objects, where each DataTable
	 * is on a separate sheet in the workbook.
	 *
	 * @param tables
	 * @return
	 */
	public static XSSFWorkbook createWorkbook(Map<String, DataTable> tables) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        
        for (String sheetName: tables.keySet()) {
        	DataTable table = tables.get(sheetName);
            table.addToWorkbook(workbook, sheetName);
        }
        return workbook;
	}
	
	/**
	 * Adds a sheet representing the data table to the specified workbook.
	 * 
	 * @param workbook
	 * @param sheetName
	 */
	public XSSFSheet addToWorkbook(XSSFWorkbook workbook, String sheetName) {
        XSSFSheet sheet = workbook.createSheet(sheetName);
        
        CreationHelper createHelper = workbook.getCreationHelper();

        //--------------------------------------
        // Column header format
        //--------------------------------------
        CellStyle headerCellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        //headerCellStyle.setFont(font);
        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);

        // Header row
        int rowNumber = 0;
        XSSFRow xlsxRow = sheet.createRow(rowNumber);
        for (int i = 0; i < columns.size(); i++) {
        	XSSFCell cell;
            cell = xlsxRow.createCell(i);
            cell.setCellStyle(headerCellStyle);
            cell.setCellValue(columns.get(i));
        }

	    for (ArrayList<String> dataRow: this.data) {
        	rowNumber++;
            xlsxRow = sheet.createRow(rowNumber);
            
            for (int i = 0; i < dataRow.size(); i++){
            	String value = dataRow.get(i);
            	
            	if (value == null) value = "";
            	value = value.trim();
            	
            	if (value.matches("^-?\\d+$")) {
            	    int ivalue = Integer.parseInt(value);
                    xlsxRow.createCell(i).setCellValue(ivalue);
            	}
            	else if (value.matches("^-?\\d+\\.\\d*$")) {
            		double dvalue = Double.parseDouble(value);
            		xlsxRow.createCell(i).setCellValue(dvalue);
            	}
            	else if (value.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
            		LocalDateTime dateTime = LocalDateTime.parse(value);
            		xlsxRow.createCell(i).setCellValue(dateTime);
                    CellStyle cellStyle = workbook.createCellStyle();  
                    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yy")); 
                    		//.getFormat("m/d/yy h:mm"));  
            		xlsxRow.getCell(i).setCellStyle(cellStyle);
            	}
            	else if (value.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}$")) {
            		LocalDateTime dateTime = LocalDateTime.parse(value);
            		xlsxRow.createCell(i).setCellValue(dateTime);
                    CellStyle cellStyle = workbook.createCellStyle();  
                    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yy HH:mm:ss")); 
                    		//.getFormat("m/d/yy h:mm"));  
            		xlsxRow.getCell(i).setCellStyle(cellStyle);
            	}
            	else {
                    xlsxRow.createCell(i).setCellValue(value);
            	}
            }
        }
	    return sheet;
	}
	
	public XSSFWorkbook toXlsx() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String sheetName = "data";
        this.addToWorkbook(workbook, sheetName);
 
		return workbook;
	}
	
	int getColumnIndex(String columnName) {
		int index = this.columns.indexOf(columnName);
		return index;
	}
	
	/**
	 * Gets the spreadsheet letter(s) index for the column, e.g., "A" for column 0, "AA" for column 26.
	 * 
	 * @param columnName
	 * @return
	 */
	String getColumnLetters(String columnName) {
        String letters = "";
		int index = this.getColumnIndex(columnName);
		
	    do {
	        int current = (index % 26);
	        letters = ((char) ('A' + current)) + letters;
	        index = (index / 26) - 1;
	    } while (index >= 0);
	
		return letters;
	}
	
	public int getNumberOfRows() {
		return this.data.size();
	}
	
}
