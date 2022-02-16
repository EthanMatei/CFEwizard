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

}
