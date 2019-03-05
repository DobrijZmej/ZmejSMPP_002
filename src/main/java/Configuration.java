import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {
    private final String fileName = "config.yml";
    private Yaml yaml;
    private Map<String, Object> data = new LinkedHashMap<>();

    public Configuration(){
        yaml = new Yaml();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if(is != null) {
            data = yaml.load(is);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setDefault();
            dump();
        };
//        System.out.println(data);
    }

    private void setDefault(){
        Map<String, Object> general = new LinkedHashMap<>();
        general.put("Port", 2775);
        data.put("General", general);
    }

    private void dump() {
        String filePath = this.getClass().getResource("").getPath()+fileName;
        try (StringWriter sw = new StringWriter();
               FileWriter fw = new FileWriter(filePath);) {
            yaml.dump(data, sw);
            fw.write(sw.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
