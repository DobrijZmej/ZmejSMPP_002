import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ZmejSMPP {
    static final Logger logger = LoggerFactory.getLogger(ZmejSMPP.class);

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
