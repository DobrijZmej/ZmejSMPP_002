import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Configuration {
    private final String fileName = "config.yml";
    private Yaml yaml;

    private Map<String, Map<String, Object>> data = new LinkedHashMap<>();

    public Configuration(){
        yaml = new Yaml();
        readFile();
    }

    private void readFile(){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        if(is != null) {
            data = yaml.load(is);
//            System.out.println(data);
//            System.out.println(data.General.get("Port"));
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            setDefault();
            dump();
        };
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

    public int readPort(){
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
