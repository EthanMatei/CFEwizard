package cfe.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * Workbook utilities
 */
public class WorkbookUtil {
    
    public static void setCellForLongText(XSSFWorkbook workbook, String sheetName, int rowNum, int columnNum) {
        Sheet sheet = workbook.getSheet(sheetName);
        sheet.setColumnWidth(0, 18000);
        Row row = sheet.getRow(rowNum);
        Cell cell = row.getCell(columnNum);
        
        sheet.setColumnWidth(columnNum, 25600); // About 100 characters (25600/256 characters)
        CellStyle style = workbook.createCellStyle(); //Create new style
        
        row.setHeight((short) (20 * 12 * 40));
        style.setWrapText(true);
        cell.setCellStyle(style);      
    }
    
    public static List<String> getSheetNames(XSSFWorkbook workbook) {
        List<String> sheetNames = new ArrayList<String>();
        int numberOfSheets = workbook.getNumberOfSheets();
        
        for (int i = 0; i < numberOfSheets; i++) {
            String sheetName = workbook.getSheetName(i);
            sheetNames.add(sheetName);
        }
        
        return sheetNames;
    }

}
