import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pdu.*;

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
     */
    @Override
    public void run() {

        do try {
            // підключаємось до сокету
            fromclient = this.server.accept();
            // стартуємо обмін даними
            startSession(fromclient);
            // закриваємо сесію
            fromclient.close();
            // Очікуємо наступного підключення
            logger.info("Waiting data");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("EXCEPTION: ", e);
        }
        while (true);

    }

    /**
     * Очікування даних від клієнта з черги
     *
     * @param fromClient - посилання на сесію з сокетом
     */
    private void startSession(Socket fromClient) throws IOException {
        byte[] buffer = readData(fromClient);
        if (buffer.length <= 0) {
            return;
        }
        PDU pdu = new PDU(buffer);
        switch (pdu.getCommandId()) {
            case (PduConstants.BIND_TRANSMITTER):
                logger.info("Incoming command_id is defined as TRANSMITTER");
                processTransmitter(buffer, fromClient);
                break;
        }
    }

    /**
     * Обробка запиту, який починається з команди трансмітера
     *
     * @param data    - байтовий потік
     * @param session - посилання на сессію з сокетом
     * @throws IOException - так, таке може статися
     */
    private void processTransmitter(byte[] data, Socket session) throws IOException {
        // це перший запрос із черги, розпочинаємо обробку
        PDUTransmitter trans = new PDUTransmitter(data);
        // вдалося отримати дані, починаємо зворотній зв'язок
        OutputStream os = session.getOutputStream();
        // формуємо першу відповідь
        PDUTransmitterResp resp = new PDUTransmitterResp(PduConstants.ESME_ROK, trans.getSequenceNumber(), "TascomBank");
        os.write(resp.getPdu());
        // Отримуємо наступну чергу даних
        data = readData(session);
        if (data.length <= 0) {
            throw new RuntimeException("Not found data on second step...");
        }
        PDU pdu = new PDU(data);
        if (pdu.getCommandId() == PduConstants.ENQUIRE_LINK) {
            PDUEnquireLinkResp linkResp = new PDUEnquireLinkResp(PduConstants.ESME_ROK, pdu.getSequenceNumber());
            os.write(linkResp.getPdu());
        }
        // Отримуємо наступну чергу даних
        data = readData(session);
        pdu = new PDU(data);
        if(pdu.getCommandId()==PduConstants.SUBMIT_SM){
            PDUSubmitSm message = new PDUSubmitSm(data);
            message.init();
            PDUSubmitSmResp messageResp = new PDUSubmitSmResp(PduConstants.ESME_ROK, pdu.getSequenceNumber(), "1");
            os.write(messageResp.getPdu());
        }
        data = readData(session);
        pdu = new PDU(data);
        if (pdu.getCommandId() == PduConstants.UNBIND) {
            PDUUnbindResp unbindResp = new PDUUnbindResp(PduConstants.ESME_ROK, pdu.getSequenceNumber());
            os.write(unbindResp.getPdu());
        }
    }

    /**
     * Метод чекає надходження даних на порт, та повертає отримані дані
     *
     * @param fromClient - сокет, з якого очікуються дані
     * @return масив отриманих даних
     */
    private byte[] readData(Socket fromClient) throws IOException {
        byte[] buffer = new byte[1024];
        byte[] bufferReturn = null;
        try {
            int i = fromClient.getInputStream().read(buffer);
            if (i > -1) {
                bufferReturn = new byte[i];
                System.arraycopy(buffer, 0, bufferReturn, 0, i);
                logger.debug("Read [" + i + "] bytes:");
                logger.debug(PDU.PDUtoString(bufferReturn, 16));
                logger.debug(PDU.PDUtoString(bufferReturn, 10));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return bufferReturn;
    }
}
