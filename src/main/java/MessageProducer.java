import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

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
        logger.info("Server listener start on port " + port);
        this.queue = queue;
    }

    /**
     * Основний цикл обробки даних із сокету
     *
     */
    @Override
    public void run() {

        while (true) {
            try {
                   // підключаємось до сокету
                fromclient = this.server.accept();
                   // стартуємо обмін даними
                startSession(fromclient);
//                startSession(fromclient);
                    // закриваємо сесію
                fromclient.close();
                logger.info("Waiting data");

                //Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

    }

    /**
     * Очікування даних від клієнта з черги
     *
     * @param fromClient
     */
    private void startSession(Socket fromClient) throws IOException {
        byte[] buffer = readData(fromClient);
//        System.out.println(buffer);
        if(buffer.length>0){
            PDUTransmitter trans = decodeData(buffer);
            OutputStream os = fromClient.getOutputStream();
            PDUTransmitterResp resp = new PDUTransmitterResp(PduConstants.ESME_ROK, trans.getSequenceNumber(), "TascomBank");
            os.write(resp.getPdu());
        }



    }

    /**
     * Дешифрування надісланих даних
     *
     * @param buffer
     */
    private PDUTransmitter decodeData(byte[] buffer) {
//        for(int i = 0; i<buffer.length; i++){
//            System.out.println(buffer[i]);
//        }
        PDUTransmitter trans = new PDUTransmitter(buffer);
        return trans;
    }

    /**
     * Метод чекає надходження даних на порт, та повертає отримані дані
     *
     * @param fromClient - сокет, з якого очікуються дані
     * @return масив отриманих даних
     */
    private byte[] readData(Socket fromClient) {
        byte[] buffer = new byte[1024];
        byte[] bufferReturn = null;
        try {
            int i = fromClient.getInputStream().read(buffer);
            if(i > -1) {
                bufferReturn = new byte[i];
                System.arraycopy(buffer, 0, bufferReturn, 0, i);
                logger.debug("Read ["+i+"] bytes:");
                StringBuilder s = new StringBuilder();
                StringBuilder d = new StringBuilder();
                for (int r = 0; r<i; r++){
                    int b = buffer[r];
                    s.append(b<10?"0"+Integer.toString(b, 16):Integer.toString(b, 16));
                    d.append(b).append(" ");
                }
                logger.debug(s.toString());
                logger.debug(d.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error create BufferedReader");
            logger.error(e.getMessage(), e);
        }
        return bufferReturn;
    }

    private void StartSession_old(Socket fromclient) {
//        BufferedReader in;
        PrintWriter out;
        try {
            DataInputStream in = new DataInputStream(new BufferedInputStream(fromclient.getInputStream()));
//            in = new BufferedReader(new InputStreamReader(fromclient.getInputStream()));
            out = new PrintWriter(fromclient.getOutputStream(), true);
            byte[] buffer = new byte[1024];
            String s = "";
//            int i = in.read(buffer);
            int i = fromclient.getInputStream().read(buffer);
//            if(i > -1) {
//                byte[]
//            }
            while (i > -1) {
                logger.info("Read " + i + " bytes");
                String r = String.valueOf((int) buffer[0]);
                logger.info(r);
                s = s + r;
                i = fromclient.getInputStream().read(buffer);
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
            logger.error(e.getMessage(), e);
            System.exit(-1);
        }
    }
}
