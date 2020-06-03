package cfe.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

/**
 * Utility to extract metadata from Access db
 * @author mtavares
 *
 */
public class MetadataExtractor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
        String path = "./test-data/ms-access/";
        
		File folder = new File(path);	
		
		MetadataExtractor me = new MetadataExtractor();
		
		File[] files = folder.listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".accdb");
		    }
		});
		
				
		try {
			PrintWriter outputStream = 	new PrintWriter(new FileWriter(path +  "all.zig", true)); // false = no append
			
			outputStream.println("Database^Table^Column^Type");
			for (File fileEntry : files) {
				
		        if (!fileEntry.isDirectory()) {
		            
		            me.createCSV(path + fileEntry.getName(), fileEntry.getName(), outputStream);
		            
		            me.getMetadata(path + fileEntry.getName());
		        }
		    }
			outputStream.close();
			
			System.out.println("Done");
			
		} catch (IOException e)	{
			
			e.printStackTrace();
		}

	}
	
	
	public void getMetadata(String filename) throws IOException
	{
		
		if (!filename.endsWith(".accdb"))
			throw new IOException(filename + ": invalid extension");
		
		System.out.println("Processing " + filename);
		
		PrintWriter outputStream = 	new PrintWriter(new FileWriter(filename + ".txt", false)); // false = no append
		
		Database db = DatabaseBuilder.open(new File(filename));
		
		Set<String> tablenames = db.getTableNames();
		
		for (String tablename : tablenames)	{
			
			Table table = db.getTable(tablename);
			
			outputStream.println("-------------------TABLE: " + tablename + "-------------------");
			
			for(Column column : table.getColumns())	{
				
				String columnName = column.getName();
				
				outputStream.println(columnName);
			}
			outputStream.println("-----------------");
			
			for(Column column : table.getColumns())	{
				
				String columnName = column.getName();				
				outputStream.println(columnName + ": " + column.getType());
			}	
			
			for(Column column : table.getColumns())	{
				
				String columnName = column.getName();				
				outputStream.println("hm.put(\"" + columnName + "\",\"\");");
			}	
			outputStream.println("**************************************");
			
		}
		
		outputStream.close();
		db.close();
	}

	public void createCSV(String filename, String dbname, PrintWriter outputStream) throws IOException
	{
		
		if (!filename.endsWith(".accdb"))
			throw new IOException(filename + ": invalid extension");
		
		System.out.println("Processing " + filename);
		
		
		outputStream.println(dbname + "^^^");
		Database db = DatabaseBuilder.open(new File(filename));
		
		Set<String> tablenames = db.getTableNames();
		
		for (String tablename : tablenames)	{
			
			Table table = db.getTable(tablename);
			/*
			if (tablename.contains("LOGIN") || tablename.contains("Full linkage (NJ 8-29-2013)") || tablename.contains("Full Linkage database (HLN2-25-2013)")
					) 
				continue;
			*/
			outputStream.println("^" + tablename + "^^");
			/**
			for(Column column : table.getColumns())	{
				
				String columnName = column.getName();
				
				outputStream.println(columnName);
			}
			outputStream.println("-----------------");
			
			for(Column column : table.getColumns())	{
				
				String columnName = column.getName();				
				outputStream.println(columnName + ": " + column.getType());
			}	
			*/
			for(Column column : table.getColumns())	{
				
				String columnName = column.getName();				
				outputStream.println("^^" + columnName + "^"+ column.getType());
			}	
			
		}
		
		//outputStream.close();
		db.close();
	}

}
