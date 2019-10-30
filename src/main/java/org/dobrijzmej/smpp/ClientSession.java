package org.dobrijzmej.smpp;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.dobrijzmej.smpp.config.Configuration;
import org.dobrijzmej.smpp.config.User;
import org.dobrijzmej.smpp.log.Log;
//import org.slf4j.Logger;
import org.dobrijzmej.smpp.pdu.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
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
    private String currentPhone = "";
    private String currentMessage = "";

    public ClientSession(Socket clientChannel, BlockingQueue<MessageQueue> queue) throws IOException {
        this.clientChannel = clientChannel;
        this.writeStream = clientChannel.getOutputStream();
        this.queue = queue;
    }

    @Override
    public void run() {
        String labelPrefix = "[" + Thread.currentThread().getId() + "] [" + uuid + "] ";

        currentLogin = "";
        currentPhone = "";
        currentMessage = "";

        //public void process() throws InterruptedException {
        boolean isProcessed = true;
        while (isProcessed) {
            logger.debug(labelPrefix + "Waiting next command.");
            try {
                byte[] buffer = readData(labelPrefix);
                if (buffer == null || buffer.length <= 0) {
                    return;
                }
                PDU pdu = new PDU(labelPrefix, buffer);
                pdu.init();
                switch (pdu.getCommandId()) {
                    case (BIND_RECEIVER):
                        logger.debug(labelPrefix + "Incoming command_id is defined as RECEIVER");
                        processReceiver(buffer, labelPrefix);
                        break;
                    case (BIND_TRANSMITTER):
                        logger.debug(labelPrefix + "Incoming command_id is defined as TRANSMITTER");
                        processTransmitter(buffer, labelPrefix);
                        break;
                    case (BIND_TRANSCEIVER):
                        logger.debug(labelPrefix + "Incoming command_id is defined as TRANSCEIVER");
                        processTransciver(buffer, labelPrefix);
                        break;
                    case (ENQUIRE_LINK):
                        logger.debug(labelPrefix + "Incoming command_id is defined as ENQUIRE_LINK");
                        processEnquire(buffer, pdu, labelPrefix);
                        break;
                    case (SUBMIT_SM):
                        logger.debug(labelPrefix + "Incoming command_id is defined as SUBMIT_SM");
                        processSubmitSm(buffer, labelPrefix);
                        break;
                    case (UNBIND):
                        logger.debug(labelPrefix + "Incoming command_id is defined as UNBIND");
                        processUnbind(buffer, pdu, labelPrefix);
                        logger.debug(labelPrefix + "This command is the final session. I close the connection with the client.");
                        isProcessed = false;
                        break;
                    case (DATA_SM):
                        logger.debug(labelPrefix + "Incoming command_id is defined as DATA_SM");
                        processDataSm(buffer);
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
    private void processReceiver(byte[] buffer, String labelPrefix) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(labelPrefix, buffer);
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

        PDUTransmitterResp resp = new PDUReceieverResp(uuid, status, trans.getSequenceNumber(), "TascomBank", labelPrefix);
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processTransmitter(byte[] buffer, String labelPrefix) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(labelPrefix, buffer);
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
        PDUTransmitterResp resp = new PDUTransmitterResp(uuid, status, trans.getSequenceNumber(), "TascomBank", labelPrefix);
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processTransciver(byte[] buffer, String labelPrefix) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(labelPrefix, buffer);
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
        PDUTranscieverResp resp = new PDUTranscieverResp(uuid, status, trans.getSequenceNumber(), "TascomBank", labelPrefix);
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     * @param pdu
     * @throws IOException
     */
    private void processEnquire(byte[] buffer, PDU pdu, String labelPrefix) throws IOException {
        PDUResp resp = new PDUResp(uuid, ENQUIRE_LINK_RESP, ESME_ROK, pdu.getSequenceNumber(), labelPrefix);
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processSubmitSm(byte[] buffer, String labelPrefix) throws IOException, InterruptedException {
        PDUSubmitSm message = new PDUSubmitSm(labelPrefix, buffer);
        message.init();
        logger.debug(labelPrefix+"Put into queue message " + message.getShortMessage());
//        queue.put(new MessageQueue(message.getDestinationAddr(), message.getShortMessage(), currentLogin));
        PDUSubmitSmResp resp = new PDUSubmitSmResp(uuid, ESME_ROK, message.getSequenceNumber(), labelPrefix);
        writeStream.write(resp.getPdu());

        if (message.getHeaderLength() == 0) {
            queue.put(new MessageQueue(message.getDestinationAddr(), message.getShortMessage(), currentLogin));
        } else {
            if (message.getHeaderPartNumber() == 1) {
                currentPhone = "";
                currentMessage = "";
            }
            currentPhone = message.getDestinationAddr();
            currentMessage += message.getShortMessage();
            if (message.getHeaderPartNumber() == message.getHeaderPartsCount()) {
                queue.put(new MessageQueue(currentPhone, currentMessage, currentLogin));
                currentPhone = "";
                currentMessage = "";
            }
        }

    }

    /**
     * @param buffer
     * @param pdu
     */
    private void processUnbind(byte[] buffer, PDU pdu, String labelPrefix) throws IOException {
        PDUResp resp = new PDUResp(uuid, UNBIND_RESP, ESME_ROK, pdu.getSequenceNumber(), labelPrefix);
        writeStream.write(resp.getPdu());
    }


    /**
     * @param buffer
     */
    private void processDataSm(byte[] buffer) throws IOException, InterruptedException {
        PDUDataSm message = new PDUDataSm(uuid, buffer);
        message.init();
        PDUDataSmResp resp = new PDUDataSmResp(uuid, ESME_ROK, message.getSequenceNumber());
        writeStream.write(resp.getPdu());
    }

    /**
     * Метод чекає надходження даних на порт, та повертає отримані дані
     *
     * @return масив отриманих даних
     */
    private byte[] readData(String labelPrefix) throws IOException {
        byte[] bufferReturn = null;
        try {
            byte[] packSize = new byte[4];
            int stremBytesLoad = clientChannel.getInputStream().read(packSize);
            if (stremBytesLoad > -1) {
                int packSizeInt = ByteBuffer.wrap(packSize, 0, 4).getInt() - 4;
                if(packSizeInt > (100*1024)){
                    throw new RuntimeException("Error packet size! Bigger of 100 kBytes!");
                }
                logger.debug(labelPrefix + "Wait next " + packSizeInt + " bytes...");
                byte[] bufferStream = new byte[packSizeInt];
                stremBytesLoad = clientChannel.getInputStream().read(bufferStream);
                bufferReturn = new byte[packSizeInt + 4];
                System.arraycopy(packSize, 0, bufferReturn, 0, 4);
                System.arraycopy(bufferStream, 0, bufferReturn, 4, packSizeInt);
                logger.debug(labelPrefix + "Read [" + stremBytesLoad + "] bytes.");
                logger.trace(labelPrefix + PDU.PDUtoString(bufferReturn, 16));
                logger.trace(labelPrefix + PDU.PDUtoString(bufferReturn, 10));
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return bufferReturn;
    }
}
