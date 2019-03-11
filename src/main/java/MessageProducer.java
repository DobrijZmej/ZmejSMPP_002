import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Клас для запису в чергу повідомлень, що надходять із мережі
 */
public class MessageProducer implements Runnable {
    static final Logger logger = LoggerFactory.getLogger(ZmejSMPP.class);

    private ServerSocket server;
    Socket fromclient;
    private BlockingQueue<ByteBuffer> queue;

    public MessageProducer(BlockingQueue<ByteBuffer> queue) throws IOException {
        Configuration conf = new Configuration();
        int port = conf.readPort();
        server = new ServerSocket(port);
        logger.info("Server listener start on port "+port);
        this.queue = queue;
    }

    @Override
    public void run() {

        while(true){
            try {
                fromclient = this.server.accept();
                StartSession(fromclient);
                fromclient.close();
                Thread.sleep(1000);
                logger.info("Waiting data");
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

    }

    private void StartSession(Socket fromclient) {
//        BufferedReader in;
        PrintWriter out;
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(fromclient.getInputStream()));
//            in = new BufferedReader(new InputStreamReader(fromclient.getInputStream()));
            out = new PrintWriter(fromclient.getOutputStream(), true);
            byte[] buffer = new byte[1];
            String s = "";
            while(in.read(buffer) > 0) {
//                logger.info("Read " + buffer.length);
                  String r = String.valueOf((int)buffer[0]);
                  logger.info(r);
                  s = s + r;
            }
            logger.info(s);
//            ReadSesion(in, out);
            String input;
//            while ((input = in.readLine()) != null) {
//                if (input.equalsIgnoreCase("exit")) break;
//                out.println("S ::: " + input);
//                System.out.println(input);
//                logger.info(input);
//            }
            out.close();
            in.close();
        } catch (IOException e) {
            System.out.println("Error create BufferedReader");
            System.exit(-1);
        }
    }
}
