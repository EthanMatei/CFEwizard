package cfe.utils;

import java.io.File;
import java.io.FileFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class TemporaryFileInfo {
    public static final String[] fileExtensions = {".accdb", ".csv", ".txt"};
    public static String fileExtensionsString;
    private String name;
    private long sizeInBytes;
    private long lastModified;
    
    private static DecimalFormat formatter = new DecimalFormat("#,##0.00");
    
    static {
        fileExtensionsString = String.join(", ", TemporaryFileInfo.fileExtensions);
    }
    
    public static List<TemporaryFileInfo> getTemporaryFileInfos(String tempDir) {
        List<TemporaryFileInfo> infos = new ArrayList<TemporaryFileInfo>();
        
        FileFilter cfeFileFilter = new FileFilter() {
            public boolean accept(File file) {
                boolean accept = false;
                for (String extension: TemporaryFileInfo.fileExtensions) {
                    if (file.getName().endsWith( extension )) {
                        accept = true;
                        break;
                    }
                }
                return accept;
                
            }
        };
        
        File dir = new File(tempDir);
        File[] files = dir.listFiles(cfeFileFilter);

        if (files != null) {
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            for (File file: files) {
                TemporaryFileInfo tempFile = new TemporaryFileInfo();

                tempFile.setName(file.getName());
                tempFile.setLastModified(file.lastModified());
                tempFile.setSizeInBytes(file.length());

                infos.add(tempFile);
            }
        }
        
        return infos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }
    
    public double getSizeInMegabytes() {
        return this.sizeInBytes / (1024.0 * 1024.0);
    }
    
    public String getSizeInMegabytesFormatted() {
        String formatted = TemporaryFileInfo.formatter.format(this.getSizeInMegabytes());
        return formatted;
    }
    
    public double getAgeInDays() {
        long currentTime = System.currentTimeMillis();
        double ageInDays = (currentTime - this.lastModified) / (1000.0 * 60.0 * 60.0 * 24.0);
        return ageInDays;
    }
    
    public String getAgeInDaysFormatted() {
        String formatted = TemporaryFileInfo.formatter.format(this.getAgeInDays());
        return formatted;
    }

}
