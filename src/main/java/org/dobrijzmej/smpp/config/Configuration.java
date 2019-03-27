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

/**
 *
 */
public class Configuration {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");
    private static final String FILE_NAME = "config.yml";
    private Yaml yaml;

    private Params params;

    /**
     *
     */
    public Configuration() {
        yaml = new Yaml(new Constructor(Params.class));
        params = new Params();
        readFile();
    }

    /**
     * Читаємо конфігурацію з файла
     */
    private void readFile() {
        // Якщо файлу конфігурації щє нема, то треба його створити з параметрами по замовчуванню
        if (Files.notExists(Paths.get(FILE_NAME))) {
            setDefault();
            save();
        }
        // на цьому етапі файл з параметрами вже повинен бути, отже можна читати параметри з нього
        try {
            String s = new String(Files.readAllBytes(Paths.get(FILE_NAME)));
            this.params = yaml.load(s);

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    /**
     * Встановлення первинних значень по замовчуванню
     */
    private void setDefault() {
        HashMap<String, Object> general = new LinkedHashMap<>();
//        general.put("port", 2775);
        this.params.general.put("Port", 2775);
        this.params.general.put("KeyStoreFileName", "keystore.jks");
        this.params.general.put("KeyStorePass", "123456");

        User user = new User("TAS24test", "Qq123456", "TAS24_test");
        this.params.users.put("TAS24Test", user);

        Output outputs = new Output("https://api-dev.tascombank.ua/gwm/sendmessage", "POST", "{${text}}");
        this.params.outputs.put("REST_ESB", outputs);
    }

    /**
     * Конвертуємо поточні дані в формат YAML та зберігаємо у файл
     */
    private void save() {
        String stringYaml = yaml.dumpAsMap(this.params);
        ByteBuffer buffer = ByteBuffer.wrap(stringYaml.getBytes());
        try (FileChannel file = FileChannel.open(Paths.get(FILE_NAME), StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            file.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @return
     */
    public int getPort() {
        return Integer.parseInt(this.params.general.get("Port").toString());
    }

    /**
     * @return
     */
    public String getKeyStoreFileName() {
        return this.params.general.get("KeyStoreFileName").toString();
    }

    public String getKeyStorePass() {
        return this.params.general.get("KeyStorePass").toString();
    }

    /**
     * @param userCode
     * @return
     */
    public User getUser(String userCode) {
        return params.users.get(userCode);
    }

    /**
     * @param outputCode
     * @return
     */
    public Output getOutput(String outputCode) {
        return params.outputs.get(outputCode);
    }

    public Map<String, Output> getOutputs() {
        return this.params.outputs;
    }

    public Map<String, User> getUsers() {
        return this.params.users;
    }
}
