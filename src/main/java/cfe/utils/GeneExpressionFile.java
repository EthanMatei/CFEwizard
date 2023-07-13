package cfe.utils;

import java.io.File;
import java.io.FileReader;

import com.opencsv.CSVReader;

import cfe.action.TestingDbCheckAction;

public class GeneExpressionFile {
    
    public static void checkFile(File geneExpressionFile) throws Exception {
        if (geneExpressionFile == null) {
            throw new Exception("Invalid null gene expression CSV file.");
        }
        else if (!geneExpressionFile.exists()) {
            throw new Exception("Non-existant gene expression CSV file.");
        }
        else if (!geneExpressionFile.canRead()) {
            throw new Exception("Gene expression CSV file cannot be read.");
        }
        
        FileReader filereader = new FileReader(geneExpressionFile);
        CSVReader csvReader = new CSVReader(filereader);
        
        String[] header = csvReader.readNext();
        
        if (header == null || header.length == 0) {
            csvReader.close();
            throw new Exception("No data found in gene expression CSV File.");
        }
        
        if (!header[0].equalsIgnoreCase("ID")) {
            csvReader.close();
            throw new Exception("The first header value \"" + header[0] + "\" is not \"ID\" for the gene expression file.");
        }
        
        for (int i = 1; i < header.length; i++) {
            if (!(header[i]).matches(TestingDbCheckAction.PHENE_VISIT_PATTERN)) {
                csvReader.close();
                throw new Exception("Header number " + (i+1) + " with value \"" + header[i] + "\" is not a valid phene visit"
                        + " for a validation gene expression CSV file. The header row of a gene expression file"
                        + " should have \"ID\" as its first column and valid phene visits for the remaining columns.");
            }
        }
        
        csvReader.close();
    }
}
