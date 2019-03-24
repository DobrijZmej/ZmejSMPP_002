package org.dobrijzmej.smpp.config;

import org.dobrijzmej.smpp.log.Log;
import org.dobrijzmej.smpp.ClientSession;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");
    private static final String FILE_NAME = "config.yml";
    private Yaml yaml;

    private Map<String, Map<String, Object>> data = new LinkedHashMap<>();

    private Params params;

    /**
     *
     *
     */
    public Configuration() {
        yaml = new Yaml(new Constructor(Params.class));
        params = new Params();
        readFile();
//        save2();
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
            this.params = yaml.load(s);
//            System.out.println(data);
//            System.out.println(yaml.represent(s).get("Port").toString());

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Встановлення первинних значень по замовчуванню
     */
    private void setDefault() {
        HashMap<String, Object> general = new LinkedHashMap<>();
        general.put("port", 2775);
        this.params.general.put("General", general);
//        this.params.general.put("Port", "2775");

        User user = new User("TAS24test", "Qq123456", "TAS24_test");
        this.params.users.put("TAS24Test", user);

        Output outputs = new Output("https://api-dev.tascombank.ua/gwm/sendmessage", "POST", "{${text}}");
        this.params.outputs.put("REST_ESB", outputs);
        /*
        Map<String, Object> general = new LinkedHashMap<>();
        general.put("Port", 2775);
        data.put("General", general);*/
    }

    private void save() {
        String stringYaml = yaml.dumpAsMap(this.params);
        ByteBuffer buffer = ByteBuffer.wrap(stringYaml.getBytes());
        try (FileChannel file = FileChannel.open(Paths.get(FILE_NAME), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            file.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void save2() {
        Yaml yaml2 = new Yaml(new Constructor(Params.class));
//        Params params = new Params();
//        User user1 = new User();
//        user1.username = "user1";
//        user1.password = "Qq123456";
//        params.users = new LinkedHashMap<String, User>();
//        params.users.put("user1", user1);
        String stringYaml = yaml2.dumpAsMap(params);
        ByteBuffer buffer = ByteBuffer.wrap(stringYaml.getBytes());
        try (FileChannel file = FileChannel.open(Paths.get(FILE_NAME + "1"), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
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
//        return Integer.parseInt(params.general.get("General").get("Port").toString());
        return 1;
    }
}
