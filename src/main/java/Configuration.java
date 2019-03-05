import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

public class Configuration {
    private Yaml yaml;
    private Map<String, Object> data;

    public Configuration(){
        yaml = new Yaml();
        InputStream file = this.getClass().getClassLoader().getResourceAsStream("config.yaml");
//        System.out.println(this.getClass().getClassLoader().getResourceAsStream("log4j.properties"));
        if(file != null) {
            data = yaml.load(file);
        };
        System.out.println(data);
    }
}
