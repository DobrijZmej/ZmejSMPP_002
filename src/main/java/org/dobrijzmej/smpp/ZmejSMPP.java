package org.dobrijzmej.smpp;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.dobrijzmej.smpp.config.Configuration;
import org.dobrijzmej.smpp.log.Log;
//import org.slf4j.Logger;

import java.io.IOException;

public class ZmejSMPP {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");

    public static void main(String[] args) {
//        Logback
        try{
            Configuration conf = new Configuration();
            Listener l = new Listener(conf.getPort());
            l.stop();
        } catch (IOException e) {
            logger.error("MAIN EXCEPTION", e);
//            logger.error(e.getStackTrace().toString());
        };
//        logger.info("!!!");
    }


}
