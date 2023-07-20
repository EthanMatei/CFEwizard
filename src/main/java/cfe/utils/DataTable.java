package cfe.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Table;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * Class for representing a table of data with named columns.
 * 
 * @author Jim Mullen
 *
 */
public class DataTable {
	
	private static final Logger log = Logger.getLogger(DataTable.class.getName());
    
	protected String name; // name of the table
	protected String key;  // primary key column name


	protected Map<String, ArrayList<String>> index; // primary key index
	protected List<String> columns;
	protected List<ArrayList<String>> data;
	
	public static final Pattern INT_PATTERN       = Pattern.compile("^-?\\d+$");
    public static final Pattern FLOAT_PATTERN     = Pattern.compile("^-?[0-9]+[.][0-9]*([eE][-]?[0-9]+)?$");
    public static final Pattern DATE_MDY_PATTERN  = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2}(\\d{2})?$");
    public static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$");
    public static final Pattern TIMESTAMP_WITH_SECONDS_PATTERN
        = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}$");    
	
	public enum JoinType {INNER, OUTER, LEFT_OUTER, RIGHT_OUTER};
	
    public DataTable() {
        this("", null);
    }
	
	public DataTable(String key) {
	    this("", key);
    }

    public DataTable(String name, String key) {
        this.name = name;
        
	    columns = new ArrayList<String>();
	    data = new ArrayList<ArrayList<String>>();
	    this.key = key;
	    this.index = new TreeMap<String, ArrayList<String>>();
	}
    
    /**
     * Indicates if the data table has a key defined.
     * @return
     */
    public boolean hasKey() {
        return (this.key != null && !this.key.trim().isEmpty());
    }

    /**
     * Get the key index size.
     * @return the size of the key index or zero if there is no key index
     */
    public int getIndexSize() {
        int size = 0;
        if (this.hasKey()) {
            size = this.index.size();
        }
        return size;
    }
    
    /**
     * Gets the index for the key column.
     * 
     * @return the index for the key, or -1 if there is no key. An exception is thrown
     *     if a key was specified, but it does not exist in the data table.
     */
    public int getKeyIndex() throws Exception {
        int keyIndex = -1;
        if (this.hasKey()) {
            // If there is a key, try to get the key index
            keyIndex = this.getColumnIndex(this.key);
            if (keyIndex < 0) {
                String message =  "The specified key column \"" + key + "\" does not exist in the data table \"" + this.name + "\"."
                        + " The columns in this table are: " + this.getColumnNamesAsString() + ".";
                log.severe(message);
                throw new Exception(message);
            }
        }
        return keyIndex;
    }

    
    public void initializeToCsv(String csvFile) throws Exception {
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
     * Initialize to CSV data represented as a string.
     * 
     * @param csvString
     * @throws IOException
     */
    public void initializeToCsvString(String csvString) throws Exception {
        Reader reader = new StringReader(csvString);
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
     * Initializes to the specified CSV string, but only adds the specified columns.
     * 
     * @param csvString
     * @param columns
     * @throws IOException
     * @throws CsvValidationException
     */
    public void initializeToCsvString(String csvString, String[] columns) throws Exception {
        Reader reader = new StringReader(csvString);
        CSVReader csvReader = new CSVReader(reader);

        String[] header = csvReader.readNext();


        //-----------------------------------------------------------------------
        // Create a map from header column name, to column number (zero-indexed)
        //-----------------------------------------------------------------------
        Map<String, Integer> headerMap = new HashMap<String, Integer>();
        int headerIndex = 0;
        for (String headerColumn: header) {
            headerMap.put(headerColumn, headerIndex);
            headerIndex++;
        }

        //-----------------------------------------------------------------------
        // Make sure all the specified columns actually exist in the CSV data
        //-----------------------------------------------------------------------
        for (String column: columns) {
            if (!headerMap.containsKey(column)) {
                throw new IOException("Specified CSV column \"" + column + "\" does not exist in the specified CSV data.");
            }
        }

        //-----------------------------------------------------------
        // Initialize this DataTable using only the specified columns
        // for each row in the CSV data.
        //-----------------------------------------------------------
        if (columns != null && columns.length > 0) {
            for (String columnName: columns) {
                this.columns.add(columnName);
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                String[] filteredLine = new String[columns.length];

                int columnIndex = 0;
                for (String columnName: columns) {
                    headerIndex = headerMap.get(columnName);
                    filteredLine[columnIndex] = line[headerIndex];
                    columnIndex++;
                }

                this.addRow(filteredLine);
            }
        }

        csvReader.close();
    }



    /**
     * Initialized using the specified Excel workbook file's first sheet.
     *  
     * @param fileName
     */
    public void initializeToWorkbookFile(String fileName) throws Exception {
        FileInputStream input = new FileInputStream(fileName);
        XSSFWorkbook workbook = new XSSFWorkbook(input);
        XSSFSheet sheet = workbook.getSheetAt(0);
        this.initializeToWorkbookSheet(sheet);
        workbook.close();
        input.close();
    }
    
    public void initializeToWorkbookSheet(XSSFSheet sheet) throws Exception {
        XSSFRow header = sheet.getRow(0);
        for (int cellIndex = 0; cellIndex < header.getLastCellNum(); cellIndex++) {
            Cell cell = header.getCell(cellIndex);
            String columnName = cell.getStringCellValue();
            this.addColumn(columnName, "");
        }
        
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            XSSFRow row = sheet.getRow(rowIndex);
            ArrayList<String> dataRow = new ArrayList<String>();
            
            for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                //CellType cellType = cell.getCellType();
                String value = "";

                if (cell != null) {
                    DataFormatter formatter = new DataFormatter();
                    String stringValue = formatter.formatCellValue(cell);
                    value = stringValue;
                }
                
                /*
                if (cellType == CellType.BOOLEAN) {
                    value = cell.getBooleanCellValue() + "";
                }
                else if (cellType == CellType.NUMERIC) {
                    DataFormatter fmt = new DataFormatter();
                    String formatValue = fmt.formatCellValue(cell);
                    value = cell.getNumericCellValue() + "";
                    log.info("    **** FORMAT VALUE for double cell (" + value + "): " + formatValue);
                }
                else if (cellType == CellType.STRING) {
                    value = cell.getStringCellValue();
                }
                */
                
                dataRow.add(value);
            }    
            this.addRow(dataRow);
        }
    }

	
    public void initializeToWorkbookStreamingSheet(Sheet sheet) throws Exception {
        Row header = sheet.getRow(0);
        for (int cellIndex = 0; cellIndex < header.getLastCellNum(); cellIndex++) {
            Cell cell = header.getCell(cellIndex);
            String columnName = cell.getStringCellValue();
            this.addColumn(columnName, "");
        }
        
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            ArrayList<String> dataRow = new ArrayList<String>();
            
            for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
                Cell cell = row.getCell(cellIndex);
                //CellType cellType = cell.getCellType();
                String value = "";

                if (cell != null) {
                    DataFormatter formatter = new DataFormatter();
                    String stringValue = formatter.formatCellValue(cell);
                    value = stringValue;
                }
                
                dataRow.add(value);
            }    
            this.addRow(dataRow);
        }
    }	
    
	/**
	 * Initializes with MS Access table.
	 * 
	 * @param table
	 * @throws IOException
	 */
	public void initializeToAccessTable(Table table) throws Exception {
		this.name = table.getName();

		int columnIndex = 0;
		for (Column col: table.getColumns()) {
			String columnName = col.getName();
		    columns.add(columnName);
		    columnIndex++;
	    }
		
		log.info("Initializing to Access table \"" + table.getName() + "\".");
		
		String keyValue = null;
		com.healthmarketscience.jackcess.Row row;
		while ((row = table.getNextRow()) != null) {
			
		    // log.info("**************** ROW SIZE: " + row.size() + "   (COLUMNS SIZE: " + columns.size() + ")");
		    
			ArrayList<String> dataRow = new ArrayList<String>();
			for (String column: columns) {
				keyValue = null;

				Object obj = row.get(column);
				
				String type = "";
				String value = "";

				if (obj != null) { 
				    type = obj.getClass().getName();
				    value = obj.toString();
				}
				else {
				    value = "";
				}
				
				if (StringUtil.isMdyDate(value)) {
				    value = StringUtil.mdyDateToTimestampDate(value);
				}
				if (key != null && column.equals(key)) {
					keyValue = value;
				}
				
				//dataRow.add(row.getString(column));
				dataRow.add(value);
			}
			
			this.addRow(dataRow);
		}
		
		log.info(this.getNumberOfRows() + " rows added for Access table \"" + table.getName() + "\".");
    }


    public boolean containsKey() {
        return this.index.containsKey(key);
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
	    for (int i = 0; i < this.getNumberOfRows(); i++) {
	        this.data.get(i).add(position, initialValue);
	    }
	}

	/**
	 * Adds column to end of table.
	 * 
	 * @param name
	 * @param initialValue
	 */
	public void addColumn(String name, String initialValue) {
	    this.insertColumn(name, this.columns.size(), initialValue);
	}
	
	public void addRow(String[] row) throws Exception {
	    ArrayList<String> rowAsList = new ArrayList<String>(Arrays.asList(row));
	    this.addRow(rowAsList);    
	}
	
	public void addRow(ArrayList<String> row) throws Exception {
		// Update index
		if (this.hasKey()) {
			String keyValue = row.get(this.getKeyIndex());
			if (keyValue == null) {
			    throw new Exception("Attempt to add row to data table with a null key value.");
			}
			else if (this.index.containsKey(keyValue)) {
			    // If the index already contains this key
			    String message = "Attempt to add row to data table (key: \"" + this.getKey() 
			            + "\") with key value \"" + keyValue + "\" that already exists.";
			    throw new Exception(message);
			}
			else {
		        this.index.put(keyValue, row);
		    }
		}
		
	    data.add(row);
	}
	
	/**
	 * Indicates if the data table has a row with the specified value in the specified column.
	 * 
	 * @param columnName
	 * @param columnValue
	 * @return
	 */
	public boolean hasRow(String columnName, String columnValue) throws Exception {
	    boolean hasRow = false;
	    
	    int columnIndex = this.getColumnIndex(columnName);
	    if (columnIndex < 0) {
	        String errorMessage = "Column \"" + columnName + "\" not found in data table \"" + this.name + "\".";
	        throw new Exception(errorMessage);
	    }
	        
	    for (int rowIndex = 0; rowIndex < this.getNumberOfRows(); rowIndex++) {
	        ArrayList<String> row = data.get(rowIndex);
	        if (row.get(columnIndex).contentEquals(columnValue)) {
	            hasRow = true;
	            break;
	        }
	    }
	    return hasRow;
	}
	
	/**
	 * Deletes row where the specified column name has the specified value.
	 * 
	 * @param columnName
	 * @param columnValue
	 */
	public void deleteRows(String columnName, String columnValue) throws Exception {
	    int columnIndex = this.getColumnIndex(columnName);
	    if (columnIndex < 0) {
	        String errorMessage = "Column \"" + columnName + "\" not found in data table \"" + this.name + "\".";
	        throw new Exception(errorMessage);
	    }
        
        // Delete entries from index
        if (this.hasKey()) {
            Set<String> deleteKeys = new HashSet<String>();
            for (String key: this.index.keySet()) {
                ArrayList<String> row = this.index.get(key);
                if (row.get(columnIndex).contentEquals(columnValue)) {
                    deleteKeys.add(key);
                }
            }
            
            for (String key: deleteKeys) {
                this.index.remove(key);
            }
        }
        
	    // Delete entries from data
	    for (int rowIndex = this.getNumberOfRows() - 1; rowIndex >= 0; rowIndex--) {
	        ArrayList<String> row = data.get(rowIndex);
	        if (row.get(columnIndex).contentEquals(columnValue)) {
	            this.data.remove(rowIndex);
	        }
	    }

	}
	
	public void deleteRow(String keyValue) throws Exception {
	    if (!this.hasKey()) {
	        throw new Exception("Attempt to use key value to delete row from data table without a key.");
	    }
	    
	    if (keyValue == null || keyValue.trim().isEmpty()) {
	        throw new Exception("Missing key value for deletion of data table row.");  
	    }
	    
        keyValue = keyValue.trim();
        
        // Remove index entry
        this.index.remove(keyValue);
        
        int keyIndex = this.getKeyIndex();
        
        // Delete entries from data
        for (int rowIndex = this.getNumberOfRows() - 1; rowIndex >= 0; rowIndex--) {
            ArrayList<String> row = data.get(rowIndex);
            if (row.get(keyIndex).contentEquals(keyValue)) {
                this.data.remove(rowIndex);
                break; // There should be only one value
            }
        }
	}
	
	public void replaceColumnValues(String columnName, String search, String replace) throws Exception {
	    int columnIndex = this.getColumnIndex(columnName);
	    if (columnIndex < 0) {
	        throw new Exception("In column value replace, column \"" + columnName + "\" not found.");    
	    }
	    
	    for (int i = 0; i < this.data.size(); i++) {
	        List<String> row = this.data.get(i);
	        if (row.get(columnIndex).equals(search)) {
	            row.set(columnIndex, replace);
	        }
	    }
	}
	
	
	/**
	 * Moves the specified column from its current position to the specified position.
	 * 
	 * @param name the name of the column to move
	 * @param moveToIndex the index of the position where the column should be moved
	 * @throws Exception
	 */
	public void moveColumn(String name, int moveToIndex) throws Exception {
	    
	    if (moveToIndex < 0 || moveToIndex > this.getNumberOfColumns()) {
	        String message = "The new index for column move (" + moveToIndex + ") is out"
	                + " of bounds.";
	        throw new Exception(message);
	    }
	    
	    // Insert the new column with a blank name
	    this.columns.add(moveToIndex, "");
	    
	    // Get the original column index after the insertion of the new column
        int moveFromIndex = this.getColumnIndex(name);
        if (moveFromIndex < 0) {
            String message = "Could not move column \"" + name + "\", because it could not be found.";
            throw new Exception(message);
        }
        
        // Copy the row elements from the old to new position
	    for (ArrayList<String> row: this.data) {
	        row.add(moveToIndex, "");
	        row.set(moveToIndex, row.get(moveFromIndex));
	    }
	    
	    // Delete the old column and set the name for the new column
	    this.deleteColumn(moveFromIndex);
	    this.columns.set(moveToIndex, name);    
	}
	
	public void deleteColumn(int columnIndex) throws Exception {
	    if (columnIndex < 0) {
	        throw new Exception("Column index less than zero for delete column; value is " + columnIndex + ".");
	    }
	    
	    if (columnIndex >= this.getNumberOfColumns()) {
	        throw new Exception("Column index is greater than max column index (" 
	            + (this.getNumberOfColumns() - 1) + "); value is " + columnIndex + ".");
	    }
	    
	    // If the key column is being deleted, remove the key and key index
	    if (this.hasKey() && columnIndex == this.getKeyIndex()) {
	        this.key = null;
	        this.index = new TreeMap<String, ArrayList<String>>();
	    }
	    
	    this.columns.remove(columnIndex);
        
        for (ArrayList<String> dataRow : this.data) {
            dataRow.remove(columnIndex);
        }
	}
	
	public void deleteColumn(String columnName) throws Exception {
	    int columnIndex = this.getColumnIndex(columnName);
	    this.deleteColumn(columnIndex);
	}
	
	public void deleteColumnIfExists(String columnName) throws Exception {
	    int columnIndex = this.getColumnIndex(columnName);
	    if (columnIndex > 0) {
	        this.deleteColumn(columnIndex);
	    }
	}
	
	public void deleteColumns(String pattern) throws Exception {
	    for (int columnIndex = this.getNumberOfColumns() - 1; columnIndex >= 0; columnIndex--) {
	        String columnName = this.getColumnName(columnIndex);
	        if (columnName.matches(pattern)) {
	            this.deleteColumn(columnIndex);
	        }
	    }
	}

    
    public void deleteColumns(List<String> columnNames) throws Exception {
        Map<String, Integer> map = this.getColumnNameMap();
        List<Integer> columnIndexes = new ArrayList<Integer>();
        
        for (String columnName: columnNames) {
            if (map.containsKey(columnName)) {
                columnIndexes.add(map.get(columnName));
            }
        }
        
        Collections.sort(columnIndexes);
        
        for (int columnIndex = columnIndexes.size() - 1; columnIndex >= 0; columnIndex--) {
            this.deleteColumn(columnIndex);
        }
    }
    
	public void renameColumn(String originalColumnName, String newColumnName) throws Exception {
	    int columnIndex = this.getColumnIndex(originalColumnName);
	    if (columnIndex < 0) {
	        throw new Exception("Column \"" + originalColumnName + "\" not found in data table.");
	    }
	    
	    this.columns.set(columnIndex, newColumnName);
	    
	    // If the column being renamed is the key column, rename the key 
	    if (this.key != null && this.key.contentEquals(originalColumnName)) {
	        this.key = newColumnName;
	    }
	}
	
	/**
	 * Deletes the last occurrence of the specified column in the data table.
	 *
	 * @param columnName
	 */
	public void deleteLastColumn(String columnName) {
	    int columnIndex = this.getLastColumnIndex(columnName);
	    this.columns.remove(columnIndex);
	    
        for (ArrayList<String> dataRow : this.data) {
            dataRow.remove(columnIndex);
        }
	}
	
	public void convertTimestampsToDates(int columnIndex) throws Exception {
        for (int rowIndex = 0; rowIndex < this.data.size(); rowIndex++) {
            String originalDate = this.data.get(rowIndex).get(columnIndex);
            String newDate = StringUtil.timestampToMdyDate(originalDate);
            this.setValue(rowIndex, columnIndex, newDate);
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
	                return StringUtil.compareTo(row1.get(index), row2.get(index));
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
	            	int compare = StringUtil.compareTo(row1.get(index1), row2.get(index1));
	            	if (compare == 0) {
	            		compare = StringUtil.compareTo(row1.get(index2), row2.get(index2));
	            	}
	                return compare;
	            }
	        });
	}
	
	/**
	 * Sorts by multiple columns.
	 * 
	 * @param sortColumns
	 */
	public void sort(String[] sortColumns) {
	    Map<String, Integer> indexMap = new HashMap<String,Integer>();
	    for (String sortColumn: sortColumns) {
	        int index = this.getColumnIndex(sortColumn);
	        indexMap.put(sortColumn, index);
	    }
	    
        Collections.sort(this.data, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> row1, ArrayList<String> row2) {
                int compare = 0;
                for (String sortColumn: sortColumns) {
                    int index = indexMap.get(sortColumn);
                    compare = StringUtil.compareTo(row1.get(index), row2.get(index));
                    if (compare != 0) break;
                }

                return compare;
            }
        });	    
	}

	public void sortWithBlanksLast(String[] sortColumns) throws Exception {
	    Map<String, Integer> indexMap = new HashMap<String,Integer>();
	    for (String sortColumn: sortColumns) {
	        int index = this.getColumnIndex(sortColumn);
	        if (index == -1) {
	            String message = "Attempt to sort data for table \"" + this.name + "\" with non-existant column \"" + sortColumn + "\"."
	                    + " The columns for this table are: " + this.getColumnNamesAsString() + ".";
	            log.severe(message);
	            throw new Exception(message);
	        }
	        indexMap.put(sortColumn, index);
	    }

	    Collections.sort(this.data, new Comparator<ArrayList<String>>() {
	        @Override
	        public int compare(ArrayList<String> row1, ArrayList<String> row2) {
	            int compare = 0;
	            for (String sortColumn: sortColumns) {
	                int index = indexMap.get(sortColumn);
	                String string1 = row1.get(index);
	                String string2 = row2.get(index);
	                if (string1.isEmpty() && !string2.isEmpty()) {
	                    compare = 1;
	                }
	                else if (!string1.isEmpty() && string2.isEmpty()) {
	                    compare = -1;
	                }
	                else {
	                    compare = StringUtil.compareTo(row1.get(index), row2.get(index));
	                }

	                if (compare != 0) break;
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

	public String toCsv(boolean convertDatesToTimestamps) {
        StringBuffer csv = new StringBuffer();
        //csv.append(this.key);
        boolean first = true;
        for (String column: this.columns) {
            column = "\"" + column + "\"";
            
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
                
                if (value == null) {
                    value = "";
                }
                else {
                    value = value.trim();
                }
                
                if (INT_PATTERN.matcher(value).matches()) {
                    csv.append(value);
                }
                else if (FLOAT_PATTERN.matcher(value).matches()) {
                    csv.append(value);
                }
                else if (value.matches("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}$")) {
                    csv.append(value);
                }
                else if (StringUtil.isMdyDate(value)) {
                    if (convertDatesToTimestamps) {
                        String timestamp = StringUtil.mdyDateToTimestamp(value);
                        csv.append(timestamp);
                    }
                    else {
                        csv.append(value);
                    }
                }
                else {
                    String modifiedValue = value;
                    modifiedValue = modifiedValue.replace("\"",  "\"\"");
                    csv.append("\"" + modifiedValue + "\"");
                }
            }
            csv.append("\n");
        }
        return csv.toString();	
	}
	
	/**
	 * Converts the data table to a CSV string.
	 * 
	 * @return string representation of the data table in CSV format.
	 */
	public String toCsv() {
        boolean convertDatesToTimestamps = true;
        String csv = this.toCsv(convertDatesToTimestamps);
        return csv;
	}
	
    public static Map<String, DataTable> createDataTables(XSSFWorkbook workbook) throws Exception {
        Map<String, DataTable> dataTables = new TreeMap<String, DataTable>();

        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            String sheetName = workbook.getSheetName(i);
            XSSFSheet sheet = workbook.getSheetAt(i);

            DataTable dataTable = new DataTable(null);
            dataTable.initializeToWorkbookSheet(sheet);

            dataTables.put(sheetName, dataTable);
        }

        return dataTables;
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
            log.info("Adding sheet \"" + sheetName + "\" to workbook...");
        	DataTable table = tables.get(sheetName);
            table.addToWorkbook(workbook, sheetName);
            log.info("Sheet \"" + sheetName + "\" added to workbook.");
        }
        log.info("All sheets added to workbook.");
        return workbook;
	}

	public static Workbook createStreamingWorkbook(Map<String, DataTable> tables, int rowAccessWindowSize) {
	    SXSSFWorkbook workbook = new SXSSFWorkbook(rowAccessWindowSize);

	    for (String sheetName: tables.keySet()) {
	        log.info("Adding sheet \"" + sheetName + "\" to workbook...");
	        DataTable table = tables.get(sheetName);
	        table.addToStreamingWorkbook(workbook, sheetName);
	        log.info("Sheet \"" + sheetName + "\" added to workbook.");
	    }
	    log.info("All sheets added to workbook.");
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

        //--------------------------------------------------------------
        // Create the data rows
        //--------------------------------------------------------------
        SimpleDateFormat mdyDateFormat = new SimpleDateFormat("M/d/yy");
        
        CellStyle dateMdyCellStyle = workbook.createCellStyle();  
        dateMdyCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy")); 
        
        CellStyle timeCellStyle = workbook.createCellStyle();  
        timeCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy HH:mm:ss")); 
        
	    for (ArrayList<String> dataRow: this.data) {
        	rowNumber++;
            xlsxRow = sheet.createRow(rowNumber);
            
            // log.info("Processing sheet \"" + sheetName + "\", data table row " + rowNumber + "/" + this.getNumberOfRows() + " (" + dataRow.size() + " columns).");
            
            for (int i = 0; i < dataRow.size(); i++){
            	String value = dataRow.get(i);
            	
            	if (value == null) value = "";
            	value = value.trim();
            	
            	if (INT_PATTERN.matcher(value).matches()) {
            	    try {
            	        int ivalue = Integer.parseInt(value);
                        xlsxRow.createCell(i).setCellValue(ivalue);
            	    } catch (NumberFormatException exception) {
            	        // Can get this if the number is too large, which can happen for PubMed IDs
                        xlsxRow.createCell(i).setCellValue(value);
            	    }
            	}
            	else if (FLOAT_PATTERN.matcher(value).matches()) {
            		double dvalue = Double.parseDouble(value);
            		xlsxRow.createCell(i).setCellValue(dvalue);
            	}
            	else if (TIMESTAMP_PATTERN.matcher(value).matches()) {
            		LocalDateTime dateTime = LocalDateTime.parse(value);
            		xlsxRow.createCell(i).setCellValue(dateTime);
 
            		xlsxRow.getCell(i).setCellStyle(dateMdyCellStyle);
            	}
            	else if (TIMESTAMP_WITH_SECONDS_PATTERN.matcher(value).matches()) {
            		LocalDateTime dateTime = LocalDateTime.parse(value);
            		xlsxRow.createCell(i).setCellValue(dateTime);
            		xlsxRow.getCell(i).setCellStyle(timeCellStyle);
            	}
                else if (DATE_MDY_PATTERN.matcher(value).matches()) {
                    // m/d/yy date format
            
                    try {
                        Date date = mdyDateFormat.parse( value );
                        xlsxRow.createCell(i).setCellValue(date);
                    }
                    catch (ParseException exception) {
                        xlsxRow.createCell(i).setCellValue(value);
                    }

                    xlsxRow.getCell(i).setCellStyle(dateMdyCellStyle);
                }            	
            	else {
                    xlsxRow.createCell(i).setCellValue(value);
            	}
            }
        }
	    
	    log.info("Sheet \"" + sheetName + "\" added to workbook, returning sheet...");
	    return sheet;
	}

	/**
	 * Adds the data from this object to a sheet with the specified name in the
	 * specified workbook.
	 * 
	 * @param workbook
	 * @param sheetName
	 * @return the sheet that was added to the workbook.
	 */
	public Sheet addToStreamingWorkbook(Workbook workbook, String sheetName) {
	    Sheet sheet = workbook.createSheet(sheetName);

	    CreationHelper createHelper = workbook.getCreationHelper();

	    //--------------------------------------
	    // Column header format
	    //--------------------------------------
	    CellStyle headerCellStyle = workbook.createCellStyle();

	    Font font = workbook.createFont();
	    font.setBold(true);
	    //headerCellStyle.setFont(font);
	    headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerCellStyle.setBorderBottom(BorderStyle.THIN);
	    headerCellStyle.setBorderLeft(BorderStyle.THIN);
	    headerCellStyle.setBorderRight(BorderStyle.THIN);

	    // Header row
	    int rowNumber = 0;
	    Row row = sheet.createRow(rowNumber);
	    for (int i = 0; i < columns.size(); i++) {
	        Cell cell;
	        cell = row.createCell(i);
	        cell.setCellStyle(headerCellStyle);
	        cell.setCellValue(columns.get(i));
	    }

	    //--------------------------------------------------------------
	    // Create the data rows
	    //--------------------------------------------------------------
	    SimpleDateFormat mdyDateFormat = new SimpleDateFormat("M/d/yy");

	    CellStyle dateMdyCellStyle = workbook.createCellStyle();  
	    dateMdyCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy")); 

	    CellStyle timeCellStyle = workbook.createCellStyle();  
	    timeCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy HH:mm:ss")); 

	    for (ArrayList<String> dataRow: this.data) {
	        rowNumber++;
	        row = sheet.createRow(rowNumber);

	        // log.info("Processing sheet \"" + sheetName + "\", data table row " + rowNumber + "/" + this.getNumberOfRows() + " (" + dataRow.size() + " columns).");

	        for (int i = 0; i < dataRow.size(); i++){
	            String value = dataRow.get(i);

	            if (value == null) value = "";
	            value = value.trim();

	            if (INT_PATTERN.matcher(value).matches()) {
	                int ivalue = Integer.parseInt(value);
	                row.createCell(i).setCellValue(ivalue);
	            }
	            else if (FLOAT_PATTERN.matcher(value).matches()) {
	                double dvalue = Double.parseDouble(value);
	                row.createCell(i).setCellValue(dvalue);
	            }
	            else if (TIMESTAMP_PATTERN.matcher(value).matches()) {
	                LocalDateTime dateTime = LocalDateTime.parse(value);
	                row.createCell(i).setCellValue(dateTime);

	                row.getCell(i).setCellStyle(dateMdyCellStyle);
	            }
	            else if (TIMESTAMP_WITH_SECONDS_PATTERN.matcher(value).matches()) {
	                LocalDateTime dateTime = LocalDateTime.parse(value);
	                row.createCell(i).setCellValue(dateTime);
	                row.getCell(i).setCellStyle(timeCellStyle);
	            }
	            else if (DATE_MDY_PATTERN.matcher(value).matches()) {
	                // m/d/yy date format

	                try {
	                    Date date = mdyDateFormat.parse( value );
	                    row.createCell(i).setCellValue(date);
	                }
	                catch (ParseException exception) {
	                    row.createCell(i).setCellValue(value);
	                }

	                row.getCell(i).setCellStyle(dateMdyCellStyle);
	            }               
	            else {
	                row.createCell(i).setCellValue(value);
	            }
	        }
	    }
	    
	    log.info("Sheet \"" + sheetName + "\" added to streaming workbook, returning sheet...");
	    return sheet;
	}
	

	public XSSFWorkbook toXlsx() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String sheetName = "data";
        this.addToWorkbook(workbook, sheetName);
 
		return workbook;
	}
	
	/**
	 * Gets the index of the first occurrence of the specified column name.
	 * @param columnName
	 * @return the index for the column or -1 if it does not exist
	 */
	public int getColumnIndex(String columnName) {
		int index = this.columns.indexOf(columnName);
		return index;
	}
	
	public boolean hasColumn(String columnName) {
	    boolean hasColumn = this.columns.contains(columnName);
	    return hasColumn;
	}
	
	public int getLastColumnIndex(String columnName) {
	    int index = this.columns.lastIndexOf(columnName);
	    return index;
	}
	
	   
    public int getColumnIndexTrimAndIgnoreCase(String columnName) {
        columnName = columnName.trim();
        int index = -1;
        for (int i = 0; i < this.columns.size(); i++) {
            if (columns.get(i).trim().equalsIgnoreCase(columnName)) {
                index = i;
                break;
            }
        }
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
		
		letters = DataTable.columnLetter(index);
	
		return letters;
	}
	
	public static String columnLetter(int index) {
        String letters = "";
        
        do {
            int current = (index % 26);
            letters = ((char) ('A' + current)) + letters;
            index = (index / 26) - 1;
        } while (index >= 0);
    
        return letters;	    
	}


	public static DataTable join(String keyColumn, String joinColumn, DataTable table1, DataTable table2) throws Exception {
	    DataTable joinedTable = DataTable.join(keyColumn, joinColumn, joinColumn, table1, table2);
	    return joinedTable;
	}

	public static DataTable join(String keyColumn, String joinColumn1, String joinColumn2, 
	        DataTable table1, DataTable table2) throws Exception {
	    DataTable joinedTable = DataTable.join(keyColumn, joinColumn1, joinColumn2, table1, table2, JoinType.INNER);
	    return joinedTable;
	}
	    
    /**
     * 
     * @param keyColumn
     * @param joinColumn1
     * @param joinColumn2
     * @param table1
     * @param table2
     * @param joinType
     * @return
     * @throws Exception
     */
	public static DataTable join(String keyColumn, String joinColumn1, String joinColumn2, 
	        DataTable table1, DataTable table2, DataTable.JoinType joinType) throws Exception {
	    
	    if (joinColumn1 == null || joinColumn1.isEmpty() || joinColumn2 == null || joinColumn2.isEmpty()) {
	        throw new Exception("No join column specified for data table join.");    
	    }
	    
	    if (table1 == null) {
	        throw new Exception("Table 1 for data table join is null.");
	    }
	    
	    if (table2 == null) {
	        throw new Exception("Table 2 for data tabel join is null.");
	    }
	    
	    if (!table1.hasColumn(joinColumn1)) {
	        String errorMessage = "Table 1 (\"" + table1.getName() + "\") of data table join does not"
	                + " contain the join column \"" + joinColumn1 + "\".";
	        throw new Exception(errorMessage);    
	    }
        
        if (!table2.hasColumn(joinColumn2)) {
            String errorMessage = "Table 2 (\"" + table2.getName() + "\") of data table join does not"
                    + " contain the join column \"" + joinColumn2 + "\".";
            throw new Exception(errorMessage);    
        }
        
	    DataTable joinTable = new DataTable(keyColumn);
	    
	    List<String> columns1 = table1.getColumnNames();
	    List<String> columns2 = table2.getColumnNames();
	    
	    //--------------------------------------------------
	    // Add the columns for the join table
	    //--------------------------------------------------
	    for (String column: columns1) {
	        if (table2.hasColumn(column)) {
	            // if this column name is not unique, prepend the table name to it
	            column = table1.getName() + "." + column;
	        }
	        joinTable.addColumn(column,  "");
	    }
	    
	    for (String column: columns2) {
	        if (table1.hasColumn(column)) {
                // if this column name is not unique, prepend the table name to it
                column = table2.getName() + "." + column;
	        }
	        joinTable.addColumn(column, "");
	    }
	    
	    //-----------------------------------------------
	    // Join the rows
	    //-----------------------------------------------
	    for (int index1 = 0; index1 < table1.getNumberOfRows(); index1++) {
	        ArrayList<String> row1 = table1.getRow(index1);
	        String subjectIndex1 = table1.getValue(index1, joinColumn1);
	        for (int index2 = 0; index2 < table2.getNumberOfRows(); index2++) {
	            String subjectIndex2 = table2.getValue(index2, joinColumn2);
	            
	            // If these rows match on the join column
	            if (subjectIndex2.equals(subjectIndex1)) {  // Both table contain row
	                
	                ArrayList<String> joinRow = new ArrayList<String>();
	                
	                // Add row values for table 1
	                for (int columnIndex = 0; columnIndex < row1.size(); columnIndex++) {
	                    joinRow.add(row1.get(columnIndex));
	                }
	                
	                // add row values for table 2
	                ArrayList<String> row2 = table2.getRow(index2);
	                for (int columnIndex = 0; columnIndex < row2.size(); columnIndex++) {
	                    joinRow.add(row2.get(columnIndex));
	                }
	                
	                joinTable.addRow(joinRow);
	            }
	        }
	    }
	    
	    if (joinType == JoinType.RIGHT_OUTER || joinType == JoinType.OUTER) {
	        for (int index2 = 0; index2 < table2.getNumberOfRows(); index2++) {
	            String value = table2.getValue(index2, joinColumn2);
	            if (!table1.hasRow(joinColumn1, value)) {
	                // If there is not matching row in table 1 for the row in table 2
	                ArrayList<String> joinRow = new ArrayList<String>();
	                
	                // Add blank row values for table 1
                    for (int columnIndex = 0; columnIndex < columns1.size(); columnIndex++) {
                        joinRow.add("");
                    }
                    
                    // add row values for table 2
                    ArrayList<String> row2 = table2.getRow(index2);
                    for (int columnIndex = 0; columnIndex < row2.size(); columnIndex++) {
                        joinRow.add(row2.get(columnIndex));
                    }
                    
                    joinTable.addRow(joinRow);
	            }
	        }
	    }
	    
	   if (joinType == JoinType.LEFT_OUTER || joinType == JoinType.OUTER) {
	        for (int index1 = 0; index1 < table1.getNumberOfRows(); index1++) {

	            String value = table1.getValue(index1, joinColumn1);
	            if (!table2.hasRow(joinColumn2, value)) {
	                // If there is not matching row in table 2 for the row in table 1
	                ArrayList<String> joinRow = new ArrayList<String>();
	                    
	                // Add row values for table 1
	                ArrayList<String> row1 = table1.getRow(index1);
                    for (int columnIndex = 0; columnIndex < row1.size(); columnIndex++) {
                        joinRow.add(row1.get(columnIndex));
                    }
	                    
	                // Add blank row values for table 2
	        	    for (int columnIndex = 0; columnIndex < columns2.size(); columnIndex++) {
	                    joinRow.add("");
	                }
	                
	                joinTable.addRow(joinRow);
	            }
	        }
	   }

	    return joinTable;
	}
	
	
	/**
	 * Gets a data table with the specified key that has only the specified columns.
	 * @param columns
	 * @return
	 */
	public DataTable filter(String key, List<String> columns) throws Exception {
	    DataTable dataTable = new DataTable(key);
	    
	    for (String column: columns) {
	        int columnIndex = this.getColumnIndex(column);
	        if (columnIndex == -1) {
	            throw new Exception("Column \"" + column + "\", used for filtering a data table, does not exits.");
	        }
	        dataTable.addColumn(column, "");
	    }
	    
	    ArrayList<String> row = new ArrayList<String>();
	    for (int i = 0; i < this.getNumberOfRows(); i++) {
	        row = this.filterRow(i, columns);
	        dataTable.addRow(row);
	    }
	    return dataTable;
	}
	
	public int getNumberOfColumns() {
	    return this.columns.size();    
	}
	
	public int getNumberOfRows() {
		return this.data.size();
	}
	
	/**
	 * Gets a map from the specified key column to the specified value column.
	 */
	public Map<String,String> getMap(String keyColumnName, String valueColumnName) throws Exception {
	    Map<String,String> map = new HashMap<String,String>();
	    
	    int keyColumnIndex = this.getColumnIndex(keyColumnName);
	    if (keyColumnIndex < 0) {
	        String message = "Key column \"" + keyColumnName + "\" for map could not be found in data table.";
	        log.severe(message);
	        throw new Exception(message);
	    }
	    
	    int valueColumnIndex = this.getColumnIndex(valueColumnName);
	    if (valueColumnIndex < 0) {
	        String message = "Value column \"" + valueColumnName + "\" for map could not be found in data table.";
	        log.severe(message);
	        throw new Exception(message);
	    }
	    
	    for (ArrayList<String> row: this.data) {
	        map.put(row.get(keyColumnIndex), row.get(valueColumnIndex));
	    }
	    
	    return map;
	}
	
	/**
	 * Gets the value for the specified key value (row) and column name. Returns null
	 * if no row with the specified key exists.
	 * 
	 * @param keyValue
	 * @param columnName
	 * @return
	 * @throws Exception If the data table does not have a key, or the specified column
	 *     name does not exist in the table.
	 */
	public String getValue(String keyValue, String columnName) throws Exception {
	    String value = null;
   
        ArrayList<String> row = this.getRow(keyValue);
        
	    int columnIndex = this.getColumnIndex(columnName);
	    
	    if (columnIndex < 0) {
	        throw new Exception("Attempt to retrieve value from data table for non-existent column \"" + columnName + "\".");
	    }

	    
	    if (row != null && !row.isEmpty()) {
	        if (columnIndex > (row.size() - 1)) {
	            value = "";
	        }
	        else {
	            value = row.get(columnIndex);
	        }
	    }
	    
	    return value;
	}
	
    public String getValue(int rowNum, String columnName) throws Exception {
        String value = null;
        
        if (rowNum < 0 || rowNum >= this.getNumberOfRows()) {
            throw new Exception("Attempt to retrieve value from data table for out of bound row \"" + rowNum + "\".");
        }
        
        ArrayList<String> row = this.data.get(rowNum);
        
        int columnIndex = this.getColumnIndex(columnName);
        
        if (columnIndex < 0) {
            throw new Exception("Attempt to retrieve value from data table for non-existent column \"" + columnName + "\".");
        }

        if (row != null && !row.isEmpty()) {
            value = row.get(columnIndex);
        }
        
        return value;
    }
    
	public void setValue(String keyValue, String columnName, String value) throws Exception {
	    ArrayList<String> row = this.getRow(keyValue);

	    int columnIndex = this.getColumnIndex(columnName);

	    if (columnIndex < 0) {
	        throw new Exception("Attempt to set value for data table for non-existent column \"" + columnName + "\".");
	    }

	    if (row == null && row.isEmpty()) {
	        throw new Exception("Attempt to set non-existent data table row with key \"" + keyValue + "\".");
	    }
	    else {
	        row.set(columnIndex, value);
	    }
	}
	
    public void setValue(int rowIndex, int columnIndex, String value) throws Exception {

        if (columnIndex < 0 || columnIndex >= this.getNumberOfColumns()) {
            throw new Exception("Attempt to set value with out of range column index " + columnIndex + ".");
        }
        
        if (rowIndex < 0 || rowIndex > this.data.size()) {
            throw new Exception("Attempt to set value in data table with invalid row index " + rowIndex + ".");
        }

        this.data.get(rowIndex).set(columnIndex, value);
    }
    
	public void setValue(int rowIndex, String columnName, String value) throws Exception {
	    int columnIndex = this.getColumnIndex(columnName);

        if (columnIndex < 0) {
            throw new Exception("Attempt to set value for data table for non-existent column \"" + columnName + "\".");
        }
        
        if (rowIndex < 0 || rowIndex > this.data.size()) {
            throw new Exception("Attempt to set value in data table with invalid row index " + rowIndex + ".");
        }

	    this.data.get(rowIndex).set(columnIndex, value);
	}

    public ArrayList<String> getRow(String keyValue) throws Exception {

        if (this.key == null || this.key.equals("")) {
            throw new Exception("Attempt to retrieve row from data table without a key.");
        }
        
        ArrayList<String> row = this.index.get(keyValue);
        
        return row;
    }

    public ArrayList<String> getRow(int rowNum) throws Exception {

        if (rowNum < 0 || rowNum >= this.getNumberOfRows()) {
            throw new Exception("Row index " + rowNum + " is out of bounds for data table with "
                    + this.getNumberOfRows() + " rows.");
        }
        
        ArrayList<String> row = this.data.get(rowNum);
        
        return row;
    }
    
    /**
     * Gets the row index for the row that has the specified value in the specified column.
     * 
     * @param columnName
     * @param value
     * @return the row index for the specified column and value, or -1 if the value is not
     *         found in the specified column.
     */
    public int getRowIndex(String columnName, String value) throws Exception {
       int rowIndex = -1;
       
       int columnIndex = this.getColumnIndex(columnName);
       
       if (columnIndex < 0) {
           throw new Exception("Column name \"" + columnName + "\" not found in call to getRowIndex");
       }
       
       for (int i = 0; i < this.data.size(); i++) {
           if (value.equals(this.getValue(i, columnIndex))) {
               rowIndex = i;
               break;
           }
       }
       return rowIndex;
    }
  
    /**
     * Returns a map for the specified column from the column value to row index.
     * 
     * @param columnName The column for which the map is to be created.
     * 
     * @return Map froom specified column's values to row indexes
     */
    public Map<String, Integer> getColumnMap(String columnName) throws Exception {
        Map<String, Integer> columnMap = new HashMap<String, Integer>();
        
        int columnIndex = this.getColumnIndex(columnName);
        if (columnIndex < 0) {
            throw new Exception("Column name \"" + columnName + "\" not found for creating column map.");
        }
        
        for (int rowNum = 0; rowNum < this.data.size(); rowNum++) {
            List<String> row = data.get(rowNum);
            String columnValue = row.get(columnIndex);
            columnMap.put(columnValue, rowNum);
        }
        
        return columnMap;
    }
    
    /**
     * Gets a map from the column name to column index.
     *
     * @return
     */
    public Map<String, Integer> getColumnNameMap() {
        Map<String, Integer> columnNameMap = new HashMap<String, Integer>();
        for (int colNum = 0; colNum < this.columns.size(); colNum++) {
            String columnName = this.columns.get(colNum);
            columnNameMap.put(columnName, colNum);
        }
        return columnNameMap;
    }
    
    /**
     * Gets the values from column "columnName" in the data table that have value "value" for column "matchColumnName".
     * 
     * @param column
     * @param value
     * @return
     */
    public List<String> getValues(String columnName, String value, String matchColumnName) throws Exception {
        List<String> values = new ArrayList<String>();
        
        int columnIndex      = this.getColumnIndex(columnName);
        int matchColumnIndex = this.getColumnIndex(matchColumnName);
        
        if (columnIndex < 0) {
            throw new Exception("Column name \"" + columnName + "\" not found in call to getValues");
        }
        
        if (matchColumnIndex < 0) {
            throw new Exception("Column name \"" + matchColumnName + "\" not found in call to getValues");
        }       
        
        for (ArrayList<String> row: this.data) {
            if (row.get(matchColumnIndex).contentEquals(value)) {
                String dataValue = row.get(columnIndex);
                values.add(dataValue);
            }
        }
        
        return values;
    }
    
    /**
     * Get the specified row with only the specified columns.
     * 
     * @param index
     * @param columns
     * @return
     * @throws Exception
     */
    public ArrayList<String> filterRow(int index, List<String> columns) throws Exception {

        if (index < 0 || index >= this.getNumberOfRows()) {
            throw new Exception("Row index value " + index + " for filtering row is out of bounds.");
        }
        
        ArrayList<String> row = this.data.get(index);
        ArrayList<String> filteredRow = new ArrayList<String>();
        
        // Transfer column values to filtered row in the order specified. 
        for (String column: columns) {
            int columnIndex = this.getColumnIndex(column);
            filteredRow.add(row.get(columnIndex));
        }
        
        return filteredRow;
    }
    
    /**
     * Gets a map from column name to column value for the specified row.
     * 
     * @param keyValue the key value for the row to retrieve.
     * @return
     * @throws Exception
     */
    public Map<String, String> getRowMap(String keyValue) throws Exception {

        if (this.key == null || this.key.equals("")) {
            throw new Exception("Attempt to retrieve row from data table without a key.");
        }
        
        Map<String,String> rowMap = new HashMap<String,String>();
        
        ArrayList<String> row = this.index.get(keyValue);
        
        for (int i = 0; i < this.columns.size(); i++) {
            rowMap.put(this.columns.get(i), row.get(i)); 
        }
        
        return rowMap;
    }

    public Map<String, String> getRowMap(int rowNum) throws Exception {

        if (rowNum < 0 || rowNum >= this.getNumberOfRows()) {
            throw new Exception("Row index " + rowNum + " is out of bounds for data table with "
                    + this.getNumberOfRows() + " rows.");
        }
        
        Map<String,String> rowMap = new HashMap<String,String>();
        
        ArrayList<String> row = this.getRow(rowNum);
        
        for (int i = 0; i < this.columns.size(); i++) {
            rowMap.put(this.columns.get(i), row.get(i)); 
        }
        
        return rowMap;
    }
    
    
    /**
     * Gets the set of keys for the data table, if a key has been defined, and returns null otherwise.
     * 
     * @return
     */
    public Set<String> getKeys() {
        Set<String> keys = null;
        if (this.index != null) {
            keys = this.index.keySet();
        }
        return keys;
    }
    
    public String getColumnName(int index) {
        return this.columns.get(index);    
    }
    
    public void setColumnName(int index, String columnName) throws Exception {
        if (index < 0 || index >= this.columns.size()) {
            throw new Exception("Column index " + index + " does not exist.");    
        }
        
        this.columns.set(index,  columnName);
    }
    
    public List<String> getColumnNames() {
        return this.columns;
    }
    
    public String getColumnNamesAsString() {
        String value = String.join(", ",  this.columns);
        return value;
    }
    
    
    public String getValue(int rowNumber, int columnNumber) {
        String value = null;
        ArrayList<String> row = this.data.get(rowNumber);
        value = row.get(columnNumber);
        return value;
    }
    
    public String getKey() {
        return this.key;
    }
    
    /**
     * Sets the key column. This can be used to set the key after data has been added, or to reset the key.
     * 
     * @param key
     */
    public void setKey(String key) throws Exception {
        if (key == null || key.trim().isEmpty()) {
            this.key = null;
            this.index = new TreeMap<String, ArrayList<String>>();
        }
        else {
            if (!this.columns.contains(key)) {
                throw new Exception("Failed to set key for data table to non-existant field \"" + key + "\"");
            }
            this.key = key;
            int keyIndex = this.getKeyIndex();
        
            this.index = new TreeMap<String, ArrayList<String>>();
            for (ArrayList<String> row: this.data) {
                String keyValue = row.get(keyIndex);
                this.index.put(keyValue, row);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the unique values of the specified column.
     * 
     * @param columnName
     * @return
     */
    public Set<String> getUniqueValues(String columnName) {
        Set<String> values = new TreeSet<String>();
        
        int columnNumber = this.getColumnIndex(columnName);
        for (int rowNumber = 0; rowNumber < this.getNumberOfRows(); rowNumber++) {
            String value = this.getValue(rowNumber, columnNumber);
            values.add(value);
        }
        return values;
    }
    
    public Set<String> getUniqueCombinedValues(String columnName1, String columnName2, String separator) {
        Set<String> values = new TreeSet<String>();
        
        int columnNumber1 = this.getColumnIndex(columnName1);
        int columnNumber2 = this.getColumnIndex(columnName2);
        for (int rowNumber = 0; rowNumber < this.getNumberOfRows(); rowNumber++) {
            String value = this.getValue(rowNumber, columnNumber1) + separator + this.getValue(rowNumber, columnNumber2);
            values.add(value);
        }
        return values;
    }
    
    /*
    public List<Integer> getMaxLengtColumnLength() {
        List<Integer> maxLengths = new ArrayList<Integer>();
        
        for (String columnName: this.columns) {
             maxLengths.add(columnName.length());   
        }
        
        for (List<String> row: this.data) {
            
        }
        return maxLengths;
    }
    */
    
    
    public String toString() {
        String value = "";
        System.out.println(String.join(" | ", this.columns));
        for (List<String> row: this.data) {
            System.out.println(String.join(" | ", row));
        }
        return value;
    }
}
