package reservations;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;

public class Log {

    public static Handler fileHandler;

    static {
        try {
            fileHandler = new FileHandler("./Log.log");
            fileHandler.setLevel(Level.ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
