package cfe.web;

import java.util.List;

import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlFileInput;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlLabel;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlTable;
import org.htmlunit.html.HtmlTableCell;
import org.htmlunit.html.HtmlTableRow;

import io.cucumber.datatable.DataTable;

public class WebUtil {
    
    /**
     * Gets the dom element on the specified page that has the specified label.
     * 
     * @param page
     * @param label
     * @return
     */
    public static DomElement getDomElementByLabel(HtmlPage page, String label) {
        DomElement domElement = null;
        DomNodeList<DomElement> labelList = page.getElementsByTagName("label");

        for (int i = 0; i < labelList.getLength(); i++) {
            DomElement domLabelElement = labelList.get(i);
            if (domLabelElement instanceof HtmlLabel) {
                HtmlLabel labelElement = (HtmlLabel) domLabelElement;

                if (labelElement.getTextContent().trim().equals(label)) {
                    String id = labelElement.getAttribute("for");
                    if (id != null) {
                        domElement = page.getElementById(id);
                    }
                }
            }
        }
        return domElement;
    }
    
    public static HtmlFileInput getFileInputByLabel(HtmlPage page, String label) {
        HtmlFileInput fileInput = null;
        
        DomElement element = WebUtil.getDomElementByLabel(page, label);
        if (element != null && element instanceof HtmlFileInput) {
            fileInput = (HtmlFileInput) element;
        }
        return fileInput;
    }
    
    public static HtmlInput getFirstSubmitByValue(HtmlPage page, String value) {
        HtmlInput submit = null;
        //List<Object> objects = page.getByXPath("/html/body//form//input[@type='submit' and @value='" + value + "']");
        List<Object> objects = page.getByXPath("/html/body//form//input[@type='submit']");
        //if (objects.size() > 0) {
        for (Object object: objects) {
            //Object object = objects.get(0);
            if (object instanceof HtmlInput) {
                submit = (HtmlInput) object;

                if (submit.getValue().trim().equals(value)) {
                    break;  // found
                } else {
                    submit = null; // this was not it
                }
            }
        }
        return submit;
    }
    
    public static HtmlInput getFirstCheckboxByValue(HtmlPage page, String value) {
        HtmlInput checkbox = null;
        List<Object> objects = page.getByXPath("/html/body//form//input[@type='checkbox' and @value='" + value + "']");
        //List<Object> objects = page.getByXPath("/html/body//form//input[@type='checkbox']");
        //System.out.println("FOUND " + objects.size() + " possible check boxes");
        if (objects.size() > 0) {
            Object object = objects.get(0);
            //System.out.println("    object class: " + object.getClass());
            if (object instanceof HtmlInput) {
                checkbox = (HtmlInput) object;
            }
        }
        return checkbox;
    }
    
    public static boolean tablesMatch(HtmlTable webTable, DataTable dataTable) throws Exception {
        boolean match = false;
        
        if (webTable == null) {
            throw new Exception("Not web table provided.");    
        }
        
        List<HtmlTableRow> webRows = webTable.getRows();
        List<List<String>> dataRows = dataTable.asLists();
        
        if (webRows.size() != dataRows.size()) {
            throw new Exception("The table on the web page has " + webRows.size() + " rows, but it"
                    + " is expected to have " + dataRows.size() + " rows.");
        }
        
        int i = 0;
        for (HtmlTableRow webRow: webRows) {
            List<String> dataRow = dataRows.get(i);
            List<HtmlTableCell> webCells = webRow.getCells();
            if (dataRow.size() != webCells.size()) {
                throw new Exception("Row " + i + " of the web page table has " + webCells.size() + " columns, but it"
                    + " should have " + dataRow.size() + " columns.");
            } else {
                for (int col = 0; col < webCells.size(); col++) {
                    String webCellValue = webCells.get(col).getVisibleText();
                    if (!webCellValue.equalsIgnoreCase(dataRow.get(col))) {
                        throw new Exception("Row " + i + " column " + col + " has web value of \"" + webCellValue +"\""
                                + " but value \"" + dataRow.get(col) + "\" is expected.");
                    }
                }
            }
            i++;
        }
        match = true;
        
        return match;
    }
    
    public static HtmlTable getTableWithHeader(HtmlPage page, String[] header) {
        HtmlTable table = null;
        List<Object> tableObjects = page.getByXPath("/html/body//table");

        for (Object object: tableObjects) {
            if (object instanceof HtmlTable) {
                table = (HtmlTable) object;
                HtmlTableRow row = table.getRow(0);
                List<HtmlTableCell> cells = row.getCells();

                if (cells.size() == header.length) {
                    int i = 0;
                    for (HtmlTableCell cell: cells) {
                        if (!cell.getVisibleText().equalsIgnoreCase(header[i])) {
                            table = null;
                            continue;
                        }
                        i++;
                    }
                    
                    if (table != null) {
                        // Table found!
                        break;
                    }
                }
            }
        }
        return table;
    }
}
