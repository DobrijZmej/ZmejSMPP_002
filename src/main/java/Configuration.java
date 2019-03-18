import log.Log;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");
    private static final String FILE_NAME = "config.yml";
    private Yaml yaml;

    private Map<String, Map<String, Object>> data = new LinkedHashMap<>();

    public Configuration() {
        yaml = new Yaml();
        readFile();
    }

    private void readFile() {
        // если файла конфига нет - то заполняем значения по умолчанию и сохраняем
        if (Files.notExists(Paths.get(FILE_NAME))) {
            setDefault();
            save();
        }
        // тут конфиг уже должен быть, соответственно читаем из него данные и загружаем в нашу переменную
        try {
            String s = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
//            System.out.println(s);
            data = yaml.load(s);
//            System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void setDefault() {
        Map<String, Object> general = new LinkedHashMap<>();
        general.put("Port", 2775);
        data.put("General", general);
    }

    private void save() {
        String stringYaml = yaml.dumpAsMap(data);
        ByteBuffer buffer = ByteBuffer.wrap(stringYaml.getBytes());
        try (FileChannel file = FileChannel.open(Paths.get(FILE_NAME), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            file.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int readPort() {
//        Map<String, Object> general = new LinkedHashMap<>();
//        System.out.println(data);
//        general = data.get("General");
//        System.out.println(data.get("General"));
//        System.out.println(data.get("General").get("Port"));
//        System.out.println(data.get("General"));
        return Integer.parseInt(data.get("General").get("Port").toString());
//        return 1;
    }
}
