package log;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Log {
    public static Logger initLog(Class classObject, String fileName) {
        String today = new SimpleDateFormat("yyyyMMDD").format(Calendar.getInstance().getTime());
        System.setProperty("logFileName", today + "_" + fileName);
        final Logger logger = LoggerFactory.getLogger(classObject);
        PropertyConfigurator.configure("log4j.properties");

        return logger;
    }

    public static Logger initLog(Class classObject) {
        return initLog(classObject, "main");
    }
}
