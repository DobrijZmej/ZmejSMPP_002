import log.Log;
import org.slf4j.Logger;

import java.io.IOException;

public class ZmejSMPP {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");

    public static void main(String[] args) {
        try{
            Configuration conf = new Configuration();
            Listener l = new Listener(conf.readPort());
            l.stop();
        } catch (IOException e) {
            logger.error("MAIN EXCEPTION", e);
//            logger.error(e.getStackTrace().toString());
        };
//        logger.info("!!!");
    }


}
