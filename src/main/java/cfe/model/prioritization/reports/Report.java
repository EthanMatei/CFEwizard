package cfe.model.prioritization.reports;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Workbook;


/**
 * Class for creating reports, from which Excel spreadsheets can be generated. The report format
 * is basically restricted to multiple "sheets", where each sheet contains a single table.
 * 
 * @author Jim Mullen
 *
 */
public class Report {
	private List<ReportSheet> reportSheets;
	
	private Log log = LogFactory.getLog(Report.class);
	
	Report() {
		reportSheets = new ArrayList<ReportSheet>();
	}

	InputStream getExcel(boolean isLandscape, boolean xlsxFormat) {
		InputStream fileStream = null;
		Workbook workbook = null;
		
		if (xlsxFormat) {
	        workbook = new XSSFWorkbook();
    	}
		else {
			workbook = new HSSFWorkbook();			
		}
		
		for (ReportSheet reportSheet: reportSheets) {
			reportSheet.setSheet(workbook, isLandscape);
		}
		
	    //---------------------------------------------------
	    // Try to convert the workbook into a filestream
	    //---------------------------------------------------
	    try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	        workbook.write(byteOut);
		    byteOut.close();
		    
            fileStream = new ByteArrayInputStream( byteOut.toByteArray() );
	    }
	    catch (Exception exception) {
	    	exception.printStackTrace();
	    }
	    
		return fileStream;
	}
	
    public XSSFWorkbook getWorkbook(boolean isLandscape) {
        XSSFWorkbook workbook = null;

        workbook = new XSSFWorkbook();
        
        for (ReportSheet reportSheet: reportSheets) {
            reportSheet.setSheet(workbook, isLandscape);
        }
        
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            workbook.write(byteOut);
            byteOut.close();
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
        
        return workbook;
    }
    
    
	public List<ReportSheet> getReportSheets() {
		return reportSheets;
	}

	public void setReportSheets(List<ReportSheet> reportSheets) {
		this.reportSheets = reportSheets;
	}



	
	
	
	
}