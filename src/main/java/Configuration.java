import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {
    private final String fileName = "config.yaml";
    private Yaml yaml;
    private Map<String, Object> data = new LinkedHashMap<>();

    public Configuration(){
        yaml = new Yaml();
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
//        System.out.println(this.getClass().getClassLoader().getResourceAsStream("log4j.properties"));
        if(is != null) {
            data = yaml.load(is);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Map<String, Object> general = new LinkedHashMap<>();
            general.put("Port", 2775);
            data.put("General", general);
            StringWriter sw = new StringWriter();
            yaml.dump(data, sw);
//            FileWriter fw = null;
            try {
//                System.out.println(this.getClass().getResource("").getPath());
//                File f = new File(this.getClass().getResource("").getPath())
                FileWriter fw = new FileWriter(this.getClass().getResource("").getPath()+fileName);
                fw.write(sw.toString());
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                sw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        System.out.println(data);
    }
}
