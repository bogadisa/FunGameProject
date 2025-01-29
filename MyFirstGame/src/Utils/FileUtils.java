package Utils;

import java.io.File;

public class FileUtils {
    public static String[] getFiles(String dirPath) {
        File directory = new File(dirPath);
        File[] files = directory.listFiles();

        int nFiles = files.length;

        String[] pathStrings = new String[nFiles];
        for (int i = 0; i < nFiles; i++) {
            pathStrings[i] = dirPath + files[i].getName();
        }
        return pathStrings;
    }
}
