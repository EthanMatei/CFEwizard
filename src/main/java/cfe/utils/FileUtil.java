package cfe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
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
    
    /**
     * Creates a temporary file with the specified prefix, suffix and content, and returns a path to the file.
     * A random string is added to the file name between the prefix and suffix to avoid naming conflicts.
     * 
     * @param prefix The file name prefix, which is typically ended with "-" as a separator between the
     *     prefix and the random string that is generated.
     * @param suffix The file name suffix, which should include ".", e.g., ".txt", ".csv".
     * @param content The content that should be written to the temporary file.
     * @return The path of the temporary file that was created.
     * 
     * @throws IOException
     */
    public static String createTempFile(String prefix, String suffix, String content) throws IOException {
        File file = null;
        String fileName = FileUtil.createTempFileName(prefix, suffix);
        file = new File(fileName);
        
        if (content == null) {
            content = "";
        }

        FileWriter writer = new FileWriter(fileName);
        writer.write(content);
        writer.close();
        
        return file.getAbsolutePath();
    }
}