import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class Listener {
    static final Logger logger = LoggerFactory.getLogger(ZmejSMPP.class);
    private ServerSocket server;

    public Listener(int port) throws IOException {
        server = new ServerSocket(port);
        logger.info("Server listener start on port "+port);
    }

    public void stop() throws IOException {
        if (server != null) {
            server.close();
            logger.info("Server listener stop");
        }
    }
}
