package cfe.model.prioritization.reports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Calendar;
import java.util.Set;

import javax.swing.GroupLayout.Alignment;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;



public class ReportSheet {
	private String title;
	private String subTitle;
	private String[] columnNames;
	private String[] columnTypes; // "string", "float", "int"
	private int[] columnWidths;  // Column widths in characters, <= 0 implies set automatically
	private String[] columnAlignment;
	private List<List<String>> data;
	private String sheetTitle;
	private boolean[] columnTotal;
	private Set<Integer> rowsWithTopBorder;
	private List<Region> mergedRegions;
	
	private Log log = LogFactory.getLog(ReportSheet.class);
	
	ReportSheet() {
	    title           = "";
	    subTitle        = "";
	    sheetTitle      = null;
	    data            = new ArrayList<List<String>>();
	    columnNames     = new String[0];
	    columnTypes     = new String[0];
	    columnWidths    = null;
	    columnAlignment = new String[0];
	    columnTotal     = new boolean[0];
	    rowsWithTopBorder    = new HashSet<Integer>();
	}

	private int getNumberOfColumns() {
	    int cols = 0;
	    
	    cols = Math.max(cols, columnNames.length);
	    
	    for (List<String> row: data) {
	    	cols = Math.max(cols, row.size());
	    }
	    
	    return cols;
	}
	
	private int getMaxColumnLength(int columnNumber) {
		int maxLength = 1;
		
		if (columnNumber < columnNames.length) {
			maxLength = Math.max(maxLength, columnNames[columnNumber].length());
		}
		
		for (List<String> row: data) {
			if (columnNumber < row.size()) {
				String cell = row.get(columnNumber);
				// System.out.println("COLUMN NUMBER: " + columnNumber);
				// System.out.println("CELLS: " + row.size());
				// System.out.println("CELL: " + cell);
				String[] lines = cell.split("\\n");
				for (String line: lines) {
				    maxLength = Math.max(maxLength, line.length());
				}
			}
		}
		
		return maxLength;
	}
	
	public void setSheet(Workbook workbook, boolean isLandscape) {

		CreationHelper createHelper = workbook.getCreationHelper();
		
		//--------------------------------------------------
		// Note 31 character name limit for sheets ???!!!
		//--------------------------------------------------
		String sheetTitle = this.sheetTitle;
		if (sheetTitle == null) sheetTitle = title;
		Sheet sheet = workbook.createSheet( sheetTitle );
		
		sheet.getPrintSetup().setLandscape( isLandscape );

		DataFormat format = workbook.createDataFormat();
	    
	    sheet.setRepeatingRows(CellRangeAddress.valueOf("1:1"));
	    
	    Font boldFont = workbook.createFont();
	    
        //boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        boldFont.setBold(true);
       
        //--------------------------------
        // Title format
        //--------------------------------
		CellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFont( boldFont );
        //titleCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
        titleCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
        
        //--------------------------------------
        // Column header format
        //--------------------------------------
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont( boldFont );
		//headerCellStyle.setBorderBottom( CellStyle.BORDER_THIN );
		//headerCellStyle.setVerticalAlignment( CellStyle.VERTICAL_BOTTOM );
		//headerCellStyle.setAlignment( CellStyle.ALIGN_CENTER );
		headerCellStyle.setBorderBottom( BorderStyle.THIN );
		headerCellStyle.setVerticalAlignment( VerticalAlignment.BOTTOM );
		headerCellStyle.setAlignment( HorizontalAlignment.CENTER );
		headerCellStyle.setWrapText( true );
		headerCellStyle.setFillForegroundColor( IndexedColors.PALE_BLUE.getIndex() );
		//headerCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		headerCellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
		
        //--------------------------------------
        // Column total format
        //--------------------------------------
		CellStyle totalCellStyle = workbook.createCellStyle();
		//totalCellStyle.setBorderTop( CellStyle.BORDER_THIN );
		totalCellStyle.setBorderTop( BorderStyle.THIN );
		
		//-----------------------------
		// Data style
		//-----------------------------
		CellStyle dataCellStyle = workbook.createCellStyle();
        //dataCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
        dataCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataCellStyle.setWrapText( true );
		
		//---------------------------
		// Data right style
		//---------------------------
		CellStyle dataRightCellStyle = workbook.createCellStyle();
		//dataRightCellStyle.setAlignment( CellStyle.ALIGN_RIGHT );
        //dataRightCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
		dataRightCellStyle.setAlignment( HorizontalAlignment.RIGHT );
        dataRightCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataRightCellStyle.setWrapText( true );		
		
		//---------------------------
		// Data int style
		//---------------------------
		CellStyle dataIntCellStyle = workbook.createCellStyle();
        //dataIntCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
        dataIntCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataIntCellStyle.setWrapText( true );
		dataIntCellStyle.setDataFormat( format.getFormat("0") );

		//---------------------------
		// Data float style
		//---------------------------
		CellStyle dataFloatCellStyle = workbook.createCellStyle();
        //dataFloatCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
        dataFloatCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataFloatCellStyle.setWrapText( true );
		dataFloatCellStyle.setDataFormat( format.getFormat("0.00") );
		
		//-----------------------------
		// Data with top border style
		//-----------------------------
		CellStyle dataWithTopBorderCellStyle = workbook.createCellStyle();
        //dataWithTopBorderCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
        dataWithTopBorderCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataWithTopBorderCellStyle.setWrapText( true );	
		//dataWithTopBorderCellStyle.setBorderTop( CellStyle.BORDER_THIN );
		dataWithTopBorderCellStyle.setBorderTop( BorderStyle.THIN );

		//---------------------------
		// Data right style
		//---------------------------
		CellStyle dataRightWithTopBorderCellStyle = workbook.createCellStyle();
		//dataRightWithTopBorderCellStyle.setAlignment( CellStyle.ALIGN_RIGHT );
        //dataRightWithTopBorderCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
		dataRightWithTopBorderCellStyle.setAlignment( HorizontalAlignment.RIGHT );
        dataRightWithTopBorderCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataRightWithTopBorderCellStyle.setWrapText( true );
		//dataRightWithTopBorderCellStyle.setBorderTop( CellStyle.BORDER_THIN );
		dataRightWithTopBorderCellStyle.setBorderTop( BorderStyle.THIN );

		//---------------------------
		// Data int style
		//---------------------------
		CellStyle dataIntWithTopBorderCellStyle = workbook.createCellStyle();
        //dataIntWithTopBorderCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
        dataIntWithTopBorderCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataIntWithTopBorderCellStyle.setWrapText( true );
		dataIntWithTopBorderCellStyle.setDataFormat( format.getFormat("0") );
		//dataIntWithTopBorderCellStyle.setBorderTop( CellStyle.BORDER_THIN );
		dataIntWithTopBorderCellStyle.setBorderTop( BorderStyle.THIN );
		
		//---------------------------
		// Data float style
		//---------------------------
		CellStyle dataFloatWithTopBorderCellStyle = workbook.createCellStyle();
        //dataFloatWithTopBorderCellStyle.setVerticalAlignment( CellStyle.VERTICAL_TOP );
        dataFloatWithTopBorderCellStyle.setVerticalAlignment( VerticalAlignment.TOP );
		dataFloatWithTopBorderCellStyle.setWrapText( true );
		dataFloatWithTopBorderCellStyle.setDataFormat( format.getFormat("0.00") );
		//dataFloatWithTopBorderCellStyle.setBorderTop( CellStyle.BORDER_THIN );
		dataFloatWithTopBorderCellStyle.setBorderTop( BorderStyle.THIN );
		
		//------------------------------------------------
		// Set the column widths
		//------------------------------------------------
	    for (int col = 0; col < this.getNumberOfColumns(); col++) {
	    	int width = 1;
	    	// If column width for this column was specified and is greater than zero
	    	// i.e., don't set automatically
	    	if (columnWidths != null && columnWidths.length > col  && columnWidths[col] > 0) {
	    		width = columnWidths[col];
	    	}
	    	else {
	    		width = getMaxColumnLength(col) + 4;  // constant chosen somewhat arbitrarily; sometimes the lengths are too short
	    		                                      // without adding a bit (it may be only when bold characters are used?)
	    	}
	    	
	        sheet.setColumnWidth(col, 256 * width);  // width set in 1/256th's of a character increments
	    }
	    
	    int currentRow    = 0;
	    int currentColumn = 0;

	    Row  row;
	    Cell dataCell;
	    RichTextString cellValue;
	          
        //-----------------------------
        // Set column headers
        //-----------------------------
        Cell colCell;
	    row = sheet.createRow(currentRow++);
        currentColumn = 0;
	    for (String columnName: columnNames) {
	        colCell = row.createCell( currentColumn++ );
		    colCell.setCellStyle( headerCellStyle );
	        cellValue = createHelper.createRichTextString( columnName );
	        //cellValue = new HSSFRichTextString( columnName );
	        colCell.setCellValue(cellValue);
	    }
	    
	    //--------------------------------
	    // Set data
	    //--------------------------------
	    int dataRowCount = 0;
	    for (List<String> dataRow: this.data) {
		    row = sheet.createRow(currentRow);
	        currentColumn = 0;
		    for (String datum: dataRow) {
		        dataCell = row.createCell( currentColumn );

		        boolean isInt   = false;
		        boolean isFloat = false;
		        boolean isRight = false;
		        
		        if (columnTypes != null && columnTypes.length > currentColumn
		        		&& columnTypes[currentColumn].equalsIgnoreCase("int")) {
		        	isInt = true;
		        }
		        else if (columnTypes != null && columnTypes.length > currentColumn
		        		&& columnTypes[currentColumn].equalsIgnoreCase("float")) {
		        	isFloat = true;
		        }
		        
		        if (columnAlignment != null && columnAlignment.length > currentColumn
		        	    && columnAlignment[currentColumn].toLowerCase().startsWith("r")) {
                    isRight = true;
		        }

		        
		        if (rowsWithTopBorder.contains( dataRowCount )) {
		        	if (isInt) {
		        		dataCell.setCellStyle( dataIntWithTopBorderCellStyle );
		        	}
		        	else if (isFloat) {
		        		dataCell.setCellStyle( dataFloatWithTopBorderCellStyle );
		        	}
		        	else if (isRight) {
		        		dataCell.setCellStyle( dataRightWithTopBorderCellStyle );
		        	}
		        	else {
		        		dataCell.setCellStyle( dataWithTopBorderCellStyle );
		        	}
		        }
		        else {
		        	if (isInt) {
		        		dataCell.setCellStyle( dataIntCellStyle );
		        	}
		        	else if (isFloat) {
		        		dataCell.setCellStyle( dataFloatCellStyle );
		        	}
		        	else if (isRight) {
		        		dataCell.setCellStyle( dataRightCellStyle );
		        	}
		        	else {
		        		dataCell.setCellStyle( dataCellStyle );
		        	}
		        }
		        
		        if (isInt) {
		        	int intValue = Integer.parseInt(datum);
		        	dataCell.setCellValue( intValue );
		        }
		        else if (isFloat) {
		        	double doubleValue = Double.parseDouble(datum);
		        	dataCell.setCellValue( doubleValue );
		        }

		        else {
		            cellValue = createHelper.createRichTextString( datum );
		            //cellValue = new HSSFRichTextString( datum );
		            dataCell.setCellValue(cellValue);
		        }
		        
		        currentColumn++;
		    }	    	
		    
		    currentRow++;
		    dataRowCount++;
	    }

	    
	    //------------------------------------------------
	    // Set column totals (if any)
	    //------------------------------------------------
	    if (columnTotal != null && columnTotal.length > 0 && this.data.size() > 0) {
		    row = sheet.createRow(currentRow++);
	        for (currentColumn = 0; currentColumn < columnTotal.length; currentColumn++) {
		        dataCell = row.createCell( currentColumn );
		        
	        	if (columnTotal[currentColumn]) {
	        		String colLetter = this.convertColNumToLetter(currentColumn);
	        		String formula = "SUM(" + colLetter + "2:" + colLetter + (currentRow-1) + ")";
	        		//System.out.println( "************ FORMULA: " + formula );
	    		    dataCell.setCellStyle( totalCellStyle );
		            dataCell.setCellFormula( formula );
	        	}
	        }
	    }
	    
	    //------------------------------------------
	    // Set merged regions (if any)
	    //------------------------------------------
	    if (mergedRegions != null) {
	    	for (Region mergedRegion: mergedRegions) {
	    		sheet.addMergedRegion(new CellRangeAddress( 
	    				mergedRegion.getFromRow(), mergedRegion.getToRow(), mergedRegion.getFromColumn(), mergedRegion.getToColumns()
	            ));
	    	}
	    }
	    
	    //---------------------------------
	    // Set Sheet Header and Footer
	    //---------------------------------
	    Header header = sheet.getHeader();
	    
        Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
	    String dateTime = sdf.format(cal.getTime());

	    header.setLeft( "&B" + this.title);

	    header.setRight( "&B" + dateTime );

	    Footer footer = sheet.getFooter();
	    footer.setCenter( "&BPage &P of &N");

	    sheet.createFreezePane(0,1); // freeze the header row
	    sheet.protectSheet("cfg"); // protect the sheet so it cannot be modified
	}

	/**
	 * Converts the report sheet to CSV format.
	 * 
	 * @return
	 */
	public String toCsv() {
	    String csv = "";
	    StringBuffer csvBuffer = new StringBuffer("");
	    
	    boolean isFirst = true;
	    for (String columnName: this.columnNames) {
	        if (isFirst) {
	            isFirst = false;
	        }
	        else {
	            csvBuffer.append(",");
	        }
	        csvBuffer.append("\"" + columnName + "\"");
	    }
	    csvBuffer.append("\n");
	    
	    for (List<String> row: data) {
	        for (int i = 0; i < columnNames.length; i++) {
	            if (i > 0) {
	                csvBuffer.append(",");
	            }
	            
	            if (columnTypes[i].contentEquals("string")) {
	                csvBuffer.append("\"" + row.get(i) + "\"");
	            }
	            else {
	                csvBuffer.append(row.get(i));
	            }
	        }
	        csvBuffer.append("\n");
	    }
	    
	    csv = csvBuffer.toString();
	    return csv;
	}
	
	public void addData(List<String> row) {
	    this.data.add( row );	
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	public String getSheetTitle() {
		return sheetTitle;
	}

	public void setSheetTitle(String sheetTitle) {
		this.sheetTitle = sheetTitle;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}

	public int[] getColumnWidths() {
		return columnWidths;
	}

	public void setColumnWidths(int[] columnWidths) {
		this.columnWidths = columnWidths;
	}

	public String[] getColumnAlignment() {
		return columnAlignment;
	}

	public void setColumnAlignment(String[] columnAlignment) {
		this.columnAlignment = columnAlignment;
	}

	public String[] getColumnTypes() {
		return columnTypes;
	}

	public void setColumnTypes(String[] columnTypes) {
		this.columnTypes = columnTypes;
	}
	public Set<Integer> getRowsWithTopBorder() {
		return rowsWithTopBorder;
	}

	public void setRowsWithTopBorder(Set<Integer> rowsWithTopBorder) {
		this.rowsWithTopBorder = rowsWithTopBorder;
	}

	public List<Region> getMergedRegions() {
		return mergedRegions;
	}

	public void setMergedRegions(List<Region> mergedRegions) {
		this.mergedRegions = mergedRegions;
	}

	public String convertColNumToLetter(int col) {
		String letters = "";
		
		if (col <= 25) {
		    letters = ( (char) ('A' + col)) + "";	
		}
		
		return letters;
	}
	
	
}
