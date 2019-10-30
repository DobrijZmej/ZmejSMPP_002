package org.dobrijzmej.smpp;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.dobrijzmej.smpp.log.Log;
import org.dobrijzmej.smpp.ClientSession;
//import org.slf4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Listener {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");
    private ServerSocket server;
    private BlockingQueue<MessageQueue> queue = new LinkedBlockingQueue<>(10);



    public Listener(int port) throws IOException {
//        server = new ServerSocket(port);
//        logger.info("Server listener start on port "+port);
        MessageProducer writer = new MessageProducer(queue);
        MessageConsumer reader = new MessageConsumer(queue);

        new Thread(writer).start();
        new Thread(reader).start();
    }

    public void stop() throws IOException {
        if (server != null) {
            server.close();
            logger.info("Server listener stop");
        }
    }

}
