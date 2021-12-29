package cfe.utils;

import java.io.File;
import java.util.UUID;

public class FileUtil {
    
    public static String getTempDir() {
        String tempDir = WebAppProperties.getTempDir();
        if (!tempDir.endsWith("/")) {
            tempDir += "/";
        }
        return tempDir;
    }
    
    public static String createTempFileName(String prefix, String suffix) {
        String tempDir = FileUtil.getTempDir();
        String fileName = tempDir + prefix + UUID.randomUUID().toString() + suffix;
        return fileName;
    }
    
    public static File createTempFile(String prefix, String suffix) {
        File file = null;
        String fileName = FileUtil.createTempFileName(prefix, suffix);
        file = new File(fileName);
        return file;
    }
}