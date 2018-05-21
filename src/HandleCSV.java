package reservations;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HandleCSV {

    public static ArrayList readCSV(String path) {
        LOGGER.addHandler(Log.fileHandler);
        LOGGER.setLevel(Level.ALL);

        String csvFile = path;
        ArrayList<String[]> list = new ArrayList<>();

        BufferedReader br;
        int counter = 0;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            // br = new BufferedReader(new InputStreamReader(new FileInputStream(new
            // File(path)), "UTF8"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(splitter, -1);
                list.add(fields);

            }

        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.WARNING, "File not found");

        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, "IO Exception while reading");

        }
        return list;
    }

    // for feedback
    public static void writeCSV(String val, String user, String path) {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            String data = "\n" + user + splitter + val + splitter + dtf.format(now);
            File file = new File(path);

            if (!file.exists()) {
                file.createNewFile();
            }

            fileWriter = new FileWriter(file.getAbsoluteFile(), true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);

        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Can't write into csv file");
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }

            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Can't close bufferedreader ");
            }
        }

    }

    private static final String splitter = ";";
    public final static Logger LOGGER = Logger.getLogger(Menu.class.getName());

}