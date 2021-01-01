package it;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

public class itDepartment {

    final static String
        DEBUG_LOG_PATH = "/Users/shadman//debuglog.txt";

    private FileOutputStream fileOutputStream;

    public itDepartment()  {
        try {
            File debugLogFile = new File(DEBUG_LOG_PATH);
            if (!debugLogFile.exists()) {
                Files.createFile(Paths.get(DEBUG_LOG_PATH));
            }
            this.fileOutputStream = new FileOutputStream(debugLogFile, true);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to create debug log file");
        }

    }

    public void logIssue(String issue) {
        try {
            byte[] message = String.format("[%s] ERROR %s\n", new Date(), issue).getBytes();
            fileOutputStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to write to debug log. Message: " + e.getMessage());
        }
    }
}
