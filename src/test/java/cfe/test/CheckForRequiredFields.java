package cfe.test;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import org.junit.Test;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

public class CheckForRequiredFields {

	@Test
	public void checker()
	{
        String path = "./test-data/ms-access/";
		
		File folder = new File(path);	

		File[] files = folder.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".accdb");
		    }
		});	
		
		try {
			PrintWriter outputStream = 	new PrintWriter(new FileWriter(path +  "ReqFields.txt", false)); // false = no append
		
			for (File fileEntry : files) {
			
				if (!fileEntry.isDirectory()) {
	            
					validateFields(path + fileEntry.getName(), outputStream);
				}
			}
			outputStream.close();
		
			System.out.println("Done");
			
		} catch (IOException e)	{
			
			e.printStackTrace();
		}

	}
	
	private void validateFields(String filename, PrintWriter outputStream) throws IOException
	{
		/**
		 *	Psychiatric domain
			Sub domain
			Relevant disorder
			Genecard symbol
			Pub Med ID
		 */
		
		String[] reqFields = {"Psychiatric domain","Sub domain","Relevant disorder", "Genecard symbol","Pub Med ID"};
		
		boolean res = false;
		
		if (!filename.endsWith(".accdb"))
			throw new IOException(filename + ": invalid extension");
		
		System.out.println("Processing " + filename);
				
		Database db = DatabaseBuilder.open(new File(filename));
		
		Set<String> tablenames = db.getTableNames();
		
		for (String tablename : tablenames)	{
			
			if (tablename.contains("LOGIN") || tablename.contains("Sheet1$_ImportErrors") || tablename.contains("inkage")) continue;
			
			Table table = db.getTable(tablename);
			
			for (String f: reqFields)	{
				
				res = false;
				
				for(Column column : table.getColumns())	{
					
					String columnName = column.getName();
					
					if (columnName.contentEquals(f)) 
						res = true;
				}
				
				if (res == false)
					outputStream.println("Field " + f + " not found in table " + table.getName());
			}	
				
		}
		
		db.close();
	}
		

}
