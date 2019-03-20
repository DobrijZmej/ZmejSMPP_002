package log;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final String FILE_NAME  = "log4j.properties";
    private static final String CONFIG_TEXT = "# Уровень логирования - полный, в файл и консоль\n" +
            "log4j.rootLogger=ALL, file, stdout\n" +
            "\n" +
            "# Апендер для работы с файлами\n" +
            "log4j.appender.file=org.apache.log4j.RollingFileAppender\n" +
            "# Путь где будет создаваться лог файл\n" +
            "log4j.appender.file.File=logs/${logFileName}.log\n" +
            "# Указываем максимальный размер файла с логами\n" +
            "log4j.appender.file.MaxFileSize=10MB\n" +
            "# Конфигурируем шаблон вывода логов в файл\n" +
            "log4j.appender.file.layout=org.apache.log4j.PatternLayout\n" +
            "log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p %c{1}:%L - %m%n\n" +
            "\n" +
            "# Апендер для вывода в консоль\n" +
            "log4j.appender.stdout=org.apache.log4j.ConsoleAppender\n" +
            "log4j.appender.stdout.layout=org.apache.log4j.PatternLayout\n" +
            "log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p %c{1}:%L - %m%n";

    public static Logger initLog(Class classObject, String fileName) {
        checkAndCreateConfig();
        String today = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
        System.setProperty("logFileName", today + "_" + fileName);
        final Logger logger = LoggerFactory.getLogger(classObject);
        PropertyConfigurator.configure("log4j.properties");

        return logger;
    }

    public static Logger initLog(Class classObject) {
        return initLog(classObject, "main");
    }

    private static void checkAndCreateConfig(){
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
