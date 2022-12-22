package cfe.utils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;


/**
 * Test scoring (work in progress).
 * 
 * @author Jim Mullen
 *
 */
public class DataTableTest {
	
	@Test
	public void testCreation() {
	
	    // Test that constructor returns non-null object
        DataTable dataTable = new DataTable(null);
        Assert.assertNotNull("Data table contstructor returned null.", dataTable);
        
        // Test that an initial column is added
        dataTable.addColumn("id", "");
        String column0 = dataTable.getColumnName(0);
        Assert.assertEquals("The first column is not \"id\".", "id", column0);
        
        // Test that an additional column is added
        dataTable.addColumn("name",  "");
        String column1 = dataTable.getColumnName(1);
        Assert.assertEquals("The second column is not \"name\".", "name", column1);
        
        // Test get (all) column names method
        List<String> expectedColumnNames = new ArrayList<String>();
        expectedColumnNames.add("id");
        expectedColumnNames.add("name");
        List<String> columnNames = dataTable.getColumnNames();
        Assert.assertEquals("The column names are not what is expected.", expectedColumnNames, columnNames);
	}
	
	@Test
	public void testIndex() throws Exception {
	    DataTable dataTable = new DataTable("id");
	    Assert.assertNotNull("Data table constructor test", dataTable);
	    
	    dataTable.addColumn("id", "");
	    dataTable.addColumn("value", "");
	    
	    String[] row1 = {"1", "abc"};
	    dataTable.addRow(row1);
	    
	    String[] row2 = {"2", "def"};
	    dataTable.addRow(row2);
	    
	    String[] row3 = {"3", "ghi"};
	    dataTable.addRow(row3);
	    
	    Assert.assertEquals(2,  dataTable.getNumberOfColumns());
	    Assert.assertEquals(3, dataTable.getNumberOfRows());
	    
	    //dataTable.deleteRow("id", "2");
	    //dataTable.deleteRow("2");
	    //Assert.assertEquals(2, dataTable.getNumberOfRows());
	    
	    dataTable.setValue(0, "value", "xyz");

	    for (int i = 0; i < dataTable.getNumberOfRows(); i++) {
	        System.out.println(dataTable.getRow(i));
	    } 
	    
	    for (String key: dataTable.getKeys()) {
	        System.out.println("Key: " + key + " Value: " + "    Row: " + dataTable.getRow(key));
	    }
	}
}
