package cfe.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public class CsvUtil {


    public static XSSFWorkbook csvToXlsx(String csvFilePath) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("data");
        
        CreationHelper createHelper = workbook.getCreationHelper();  
        
        BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
        CSVReader csvReader = new CSVReader(reader);
            
        String[] csvRow;
            
        int rowNumber = 0;
        while ((csvRow = csvReader.readNext()) != null) {
            XSSFRow xlsxRow = sheet.createRow(rowNumber);
            for (int i = 0; i < csvRow.length; i++){
            	String value = csvRow[i];
            	if (value == null) value = "";
            	
            	if (value.matches("-?\\d+")) {
            	    int ivalue = Integer.parseInt(value);
                    xlsxRow.createCell(i).setCellValue(ivalue);
            	}
            	else if (value.matches("^-?\\d+\\.\\d*$")) {
            		double dvalue = Double.parseDouble(value);
	        		xlsxRow.createCell(i).setCellValue(dvalue);
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
            rowNumber++;
        }
        csvReader.close();
        
        return workbook;
    }
    
    public static String[] getHeader(String csvString) throws IOException, CsvValidationException {
        Reader reader = new StringReader(csvString);
        CSVReader csvReader = new CSVReader(reader);  
        
        String[] header = csvReader.readNext();
        
        csvReader.close();
        
        return header;
    }
}
