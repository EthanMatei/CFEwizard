package cfg.test;

import java.io.File;
import java.io.FilenameFilter;

import cfg.parser.MSAccessParser;


public class TestUtil {

	public final static String DB_FILE_DIR = "./test-data/ms-access/";

	public static boolean loadDatabaseFiles() {
		boolean loaded = true;
		File folder = new File(DB_FILE_DIR);	

		File[] files = folder.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".accdb");
			}
		});

		try {
			for (File fileEntry : files) {	
				if (!fileEntry.isDirectory()) {
					MSAccessParser msparser = new MSAccessParser();
					String filename = DB_FILE_DIR + fileEntry.getName();
					msparser.parse(filename);
					System.out.println("FILE \"" + filename + "\" PARSED.");
				}
			}
		}
		catch (Exception exception) {
			loaded = false;
		}

		return loaded;
	}
	
	public static void main(String[] args) {
		loadDatabaseFiles();
	}

}
