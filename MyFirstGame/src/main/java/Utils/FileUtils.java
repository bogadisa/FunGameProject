package Utils;

import java.io.File;

public class FileUtils {
    public static String[] getFiles(String dirPath) {
        File directory = new File(dirPath);
        System.out.println(dirPath + directory.exists());
        
        File[] files = directory.listFiles();

        int nFiles = files.length;

        String[] pathStrings = new String[nFiles];
        for (int i = 0; i < nFiles; i++) {
            pathStrings[i] = files[i].getName();
        }
        return pathStrings;
    }
}
