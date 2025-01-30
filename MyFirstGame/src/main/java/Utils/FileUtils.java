package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public String[] getMetadata(String metadataLocation) throws IOException {
        InputStream is = getClass().getResourceAsStream(metadataLocation);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        int linesToRead = Integer.parseInt(br.readLine());
        String metadata[] = new String[linesToRead];

        String line;
        for (int i = 0; i < linesToRead; i++) {
            line = br.readLine();
            metadata[i] = line.strip();
        }

        return metadata;

    }
}
