package org.dobrijzmej.smpp;

import org.dobrijzmej.smpp.config.Configuration;
import org.dobrijzmej.smpp.config.User;
import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;
import org.dobrijzmej.smpp.pdu.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;

import static org.dobrijzmej.smpp.pdu.PduConstants.*;

/**
 * Клас реалізації клієнтської сесії
 */
public class ClientSession implements Runnable {
    //    private static final Logger logger = LoggerFactory.getLogger(org.dobrijzmej.smpp.ClientSession.class);
    private String uuid = UUID.randomUUID().toString();
    private static final Logger logger = Log.initLog(ClientSession.class, "sessions");
    private Socket clientChannel;
    private BlockingQueue<MessageQueue> queue;

    private OutputStream writeStream;

    private String currentLogin = "";
    private String currentAlias = "";

    public ClientSession(Socket clientChannel, BlockingQueue<MessageQueue> queue) throws IOException {
        this.clientChannel = clientChannel;
        this.writeStream = clientChannel.getOutputStream();
        this.queue = queue;
    }

    @Override
    public void run() {
        String labelPrefix = Thread.currentThread().getName()+" | SessionID " + uuid + " | ";
        //public void process() throws InterruptedException {
        boolean isProcessed = true;
        while (isProcessed) {
            logger.info(labelPrefix+"Waiting next command.");
            try {
                byte[] buffer = readData();
                if (buffer == null || buffer.length <= 0) {
                    return;
                }
                PDU pdu = new PDU(uuid, buffer);
                pdu.init();
                switch (pdu.getCommandId()) {
                    case (BIND_RECEIVER):
                        logger.info(labelPrefix + "Incoming command_id is defined as RECEIVER");
                        processReceiver(buffer);
                        break;
                    case (BIND_TRANSMITTER):
                        logger.info(labelPrefix + "Incoming command_id is defined as TRANSMITTER");
                        processTransmitter(buffer);
                        break;
                    case (BIND_TRANSCEIVER):
                        logger.info(labelPrefix + "Incoming command_id is defined as TRANSCEIVER");
                        processTransciver(buffer);
                        break;
                    case (ENQUIRE_LINK):
                        logger.info(labelPrefix + "Incoming command_id is defined as ENQUIRE_LINK");
                        processEnquire(buffer, pdu);
                        break;
                    case (SUBMIT_SM):
                        logger.info(labelPrefix + "Incoming command_id is defined as SUBMIT_SM");
                        processSubmitSm(buffer);
                        break;
                    case (UNBIND):
                        logger.info(labelPrefix + "Incoming command_id is defined as UNBIND");
                        processUnbind(buffer, pdu);
                        logger.info(labelPrefix + "This command is the final session. I close the connection with the client.");
                        isProcessed = false;
                        break;
                    default:
                        logger.error(labelPrefix + "Command is not defined. Can not continue the session, I interrupt contact with the client. command_id=" + pdu.getCommandId());
                        isProcessed = false;
                }
            } catch (IOException | RuntimeException e) {
                logger.error("EXCEPTION", e);
                isProcessed = false;
            } catch (InterruptedException e) {
                logger.error(labelPrefix + "Stream exception disconnection detected. Complete the work cycle.", e);
                try {
                    clientChannel.close();
                } catch (IOException ex) {
                    logger.error(labelPrefix + "EXCEPTION", ex);
                }
                return;
            }
        }
        try {
            clientChannel.close();
        } catch (IOException e) {
            logger.error(labelPrefix + "EXCEPTION", e);
        }
    }

    /**
     * @param buffer
     */
    private void processReceiver(byte[] buffer) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(uuid, buffer);
        trans.init();
        Configuration config = new Configuration();
        Map<String, User> users;
        users = config.getUsers();
        trans.authorize(users);
        currentLogin = trans.getUser().getAlias();

        int status = PduConstants.ESME_ROK;
        if ("---".equals(trans.getUser().getUsername())) {
            status = PduConstants.ESME_RINVPASWD;
        }

        PDUTransmitterResp resp = new PDUReceieverResp(uuid, status, trans.getSequenceNumber(), "TascomBank");
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processTransmitter(byte[] buffer) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(uuid, buffer);
        trans.init();
        Configuration config = new Configuration();
        Map<String, User> users;
        users = config.getUsers();
        trans.authorize(users);
        currentLogin = trans.getUser().getAlias();
        int status = PduConstants.ESME_ROK;
        if ("---".equals(trans.getUser().getUsername())) {
            status = PduConstants.ESME_RINVPASWD;
        }
        PDUTransmitterResp resp = new PDUTransmitterResp(uuid, status, trans.getSequenceNumber(), "TascomBank");
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processTransciver(byte[] buffer) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(uuid, buffer);
        trans.init();
        Configuration config = new Configuration();
        Map<String, User> users;
        users = config.getUsers();
        trans.authorize(users);
        currentLogin = trans.getUser().getAlias();
        int status = PduConstants.ESME_ROK;
        if ("---".equals(trans.getUser().getUsername())) {
            status = PduConstants.ESME_RINVPASWD;
        }
        PDUTranscieverResp resp = new PDUTranscieverResp(uuid, status, trans.getSequenceNumber(), "TascomBank");
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     * @param pdu
     * @throws IOException
     */
    private void processEnquire(byte[] buffer, PDU pdu) throws IOException {
        PDUResp resp = new PDUResp(uuid, ENQUIRE_LINK_RESP, ESME_ROK, pdu.getSequenceNumber());
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processSubmitSm(byte[] buffer) throws IOException, InterruptedException {
        PDUSubmitSm message = new PDUSubmitSm(uuid, buffer);
        message.init();
        logger.debug("Put into queue message " + message.getShortMessage());
        queue.put(new MessageQueue(message.getDestinationAddr(), message.getShortMessage(), currentLogin));
        PDUSubmitSmResp resp = new PDUSubmitSmResp(uuid, ESME_ROK, message.getSequenceNumber());
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     * @param pdu
     */
    private void processUnbind(byte[] buffer, PDU pdu) throws IOException {
        PDUResp resp = new PDUResp(uuid, UNBIND_RESP, ESME_ROK, pdu.getSequenceNumber());
        currentLogin = "";
        currentAlias = "";
        writeStream.write(resp.getPdu());
    }


    /**
     * Метод чекає надходження даних на порт, та повертає отримані дані
     *
     * @return масив отриманих даних
     */
    private byte[] readData() throws IOException {
        byte[] buffer = new byte[1024];
        byte[] bufferReturn = null;
        try {
            int i = clientChannel.getInputStream().read(buffer);
            if (i > -1) {
                bufferReturn = new byte[i];
                System.arraycopy(buffer, 0, bufferReturn, 0, i);
                logger.debug("SessionID " + uuid + " | Read [" + i + "] bytes.");
                logger.trace("SessionID " + uuid + " | " + PDU.PDUtoString(bufferReturn, 16));
                logger.trace("SessionID " + uuid + " | " + PDU.PDUtoString(bufferReturn, 10));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return bufferReturn;
    }

}
