package cfe.action;

import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.ServletActionContext;

import com.opencsv.CSVReader;

import cfe.action.ActionErrorException;
import cfe.utils.Authorization;
import cfe.utils.Filter;

/**
 * Struts2 action for displaying CSV files.
 * 
 * @author Jim Mullen
 *
 */
public class CsvDisplayAction extends BaseAction implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	private Log log = LogFactory.getLog(CsvDisplayAction.class);
	

    @SuppressWarnings("unchecked")
    private Map session;

    private String reportName;
    private String csvFilePath;
    private String csvFileName;
    private String reportType;
    
	private String errorMessage;
	private String fileName;
	private String fileContentType;
    private InputStream fileStream;
    
    private String published;
    
    private int month;
    private int year;
    
    private String reportFormat;
	
    public CsvDisplayAction() {
        errorMessage    = "";
        fileName        = "";
        fileContentType = "";
        fileStream      = null;
        
        month = 0;
        year  = 0;
    }
    
    @SuppressWarnings("unchecked")
    public void setSession(Map session) {
    	this.session = session;
    }    
	
    /**
     * For viewing reports.
     * 
     * @return
     */
    public String view() throws Exception {
    	String result = SUCCESS;

    	if (!Authorization.isLoggedIn(session)) {
    		result = LOGIN;
    	}
    	else {
    		//try {
    		if (csvFilePath == null || csvFilePath.trim().equals("")) {
    			throw new ActionErrorException("No CSV file was specified.");
    		}

    	    Path path = Paths.get(csvFilePath); 
    	    csvFileName = path.getFileName().toString();
    	    String csvFileBaseName = FilenameUtils.getBaseName(csvFileName);


    		/*********
    		if (session == null) {
    			String exceptionMessage = "You user information could not be retrieved."
    				+ " Please make sure that you are logged in.";
    			throw new ActionErrorException( exceptionMessage );	
    		}
    		 ***************/

    		reportFormat = Filter.filterNonAlphaNumeric( reportFormat );


    		//try {
    		    log.info("Trying to generate report with name " + reportName + " and format " + reportFormat + ".");

    		    XSSFWorkbook workbook = csvToXlsx(csvFilePath);
    		    
    		    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
    	        workbook.write(byteOut);
    		    byteOut.close();
    		    
                fileStream = new ByteArrayInputStream( byteOut.toByteArray() );
    		    if (fileStream == null) {
    			    throw new Exception("The file \"" + csvFilePath + "\" could not be retrieved.");
    		    }
    		//}
    		//catch (Exception exception) {
    		//	throw new ActionErrorException( exception.getMessage() );
    		//}

   			String fileSuffix = ".xlsx";
    		fileName = csvFileBaseName + fileSuffix;
    		fileContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    		log.info("Generating spreasheet " + fileName + " for csv file \"" + csvFilePath + "\".");
    	}

    	return result;
    }
    

    public static XSSFWorkbook csvToXlsx(String csvFilePath) throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("data");
        
        System.out.println("*************** CSV FILE PATH: |" + csvFilePath + "|");
        BufferedReader reader = new BufferedReader(new FileReader(csvFilePath));
        CSVReader csvReader = new CSVReader(reader);
            
        String[] csvRow;
            
        int rowNumber = 1;
        while ((csvRow = csvReader.readNext()) != null) {
            XSSFRow xlsxRow = sheet.createRow(rowNumber);
            for (int i = 0; i < csvRow.length; i++){
                xlsxRow.createCell(i).setCellValue(csvRow[i]);
            }
            rowNumber++;
        }
        csvReader.close();
        
        return workbook;
    }



	//-----------------------------------------
	// Getters and Setters
	//-----------------------------------------
	public InputStream getFileStream() {
		return fileStream;
	}

	public void setFileStream(InputStream fileStream) {
		this.fileStream = fileStream;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getCsvFilePath() {
		return csvFilePath;
	}

	public void setCsvFilePath(String csvFilePath) {
		this.csvFilePath = csvFilePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getCsvFileName() {
		return csvFileName;
	}

	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getPublished() {
		return published;
	}

	public void setPublished(String published) {
		this.published = published;
	}
	
	public String getReportFormat() {
		return reportFormat;
	}

	public void setReportFormat(String reportFormat) {
		this.reportFormat = reportFormat;
	}
}
