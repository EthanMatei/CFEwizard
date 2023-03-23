package cfe.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class MyTest {
    
    public static final Pattern FLOAT_PATTERN = Pattern.compile("^-?[0-9]+[.][0-9]*([eE][-]?[0-9]+)?$");
    
    public static void main(String[] args) {
        
        String rootDir = "/var/lib/tomcat9/webapps/cfe-wizard/";
        
        WebAppProperties.initialize(rootDir);
        String tempDir = WebAppProperties.getTempDir();
        System.out.println("temp dir: " + tempDir);
        
        FileFilter cfeFileFilter = new FileFilter()
        {
            public boolean accept(File file) {
                if (
                    file.getName().endsWith(".accdb")
                    || file.getName().endsWith(".csv")
                    || file.getName().endsWith(".txt")
                ) {
                    return true;
                }
                return false;
                
            }
        };
        
        File dir = new File(tempDir);
        File[] files = dir.listFiles(cfeFileFilter);
        
        long currentTime = System.currentTimeMillis();
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        for (File file: files) {
            long lastModified = file.lastModified();
            double ageInDays = (currentTime - file.lastModified()) / (1000.0 * 60.0 * 60.0 * 24.0);
            System.out.println(file.getName() + ": " + formatter.format(ageInDays) + "; size: " + formatter.format(file.length() / (1024.0 * 124.0)) + " MB");
            if (ageInDays > 10.0) {
                file.delete();
            }
        }
        
        
        
        
        
        
        /*
        System.out.println("Test");
        String[] data = {"1.2", "0.", "0.3", "0.4E-10", "abc", "1.0e2", "-1.0e-2", "1a2"};
        
        for (String value: data) {
            System.out.print("String \"" + value + "\" ");
            if (FLOAT_PATTERN.matcher(value).matches()) {
                System.out.print("is a floating point number.");
            }
            else {
                System.out.print("is NOT a floating point number.");
            }
            System.out.println();
        }
        */
    }
}
