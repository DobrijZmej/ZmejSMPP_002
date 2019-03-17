import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Listener {
    static final Logger logger = LoggerFactory.getLogger(Listener.class);
    private ServerSocket server;
    private BlockingQueue<ByteBuffer> queue = new LinkedBlockingQueue<>(1);



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
