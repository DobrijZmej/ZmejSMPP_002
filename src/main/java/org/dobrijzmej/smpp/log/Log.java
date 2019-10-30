package org.dobrijzmej.smpp.log;

//import org.apache.log4j.PropertyConfigurator;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

//import org.apache.log4j.LogManager;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.util.Loader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 */
public class Log {
    private static final String FILE_NAME = "log4j2.xml";
    private static final String CONFIG_TEXT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<Configuration status=\"error\">\n" +
            "  <Appenders>\n" +
            "    <Console name=\"Console\" target=\"SYSTEM_OUT\">\n" +
            "      <PatternLayout>\n" +
            "        <MarkerPatternSelector defaultPattern=\"%sn. %msg: Logger=%logger%n\">\n" +
            "          <PatternMatch key=\"CLASS\" pattern=\"%sn. %msg: Class=%class%n\"/>\n" +
            "        </MarkerPatternSelector>\n" +
            "      </PatternLayout>\n" +
            "    </Console>\n" +
            "  </Appenders>\n" +
            "  <Loggers>\n" +
            "    <Root level=\"TRACE\">\n" +
            "      <AppenderRef ref=\"Console\" />\n" +
            "    </Root>\n" +
            "  </Loggers>\n" +
            "</Configuration>";

    public static Logger initLog(Class classObject, String fileName) {
        checkAndCreateConfig();
        String today = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        System.setProperty("logFileName", today + "_" + fileName);
//        ConfigurationSource source = null;
//        try {
//            source = new ConfigurationSource(new FileInputStream(FILE_NAME));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Configurator.initialize(null, FILE_NAME);
//        PropertyConfigurator.configure(FILE_NAME);

        final Logger logger = LogManager.getLogger("SMPPZmejlog");
        return logger;
    }

    public static Logger initLog(Class classObject) {
        return initLog(classObject, "main");
    }

    private static void checkAndCreateConfig() {
        if (Files.notExists(Paths.get(FILE_NAME))) {
            ByteBuffer buffer = ByteBuffer.wrap(CONFIG_TEXT.getBytes());
            try (FileChannel file = FileChannel.open(Paths.get(FILE_NAME), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                file.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
