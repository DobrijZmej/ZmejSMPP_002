import log.Log;
import org.slf4j.Logger;
import pdu.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.UUID;

import static pdu.PduConstants.*;

/**
 * Клас реалізації клієнтської сесії
 */
public class ClientSession {
//    private static final Logger logger = LoggerFactory.getLogger(ClientSession.class);
    private String uuid = UUID.randomUUID().toString();
    private static final Logger logger = Log.initLog(ClientSession.class, "sessions");
    private Socket clientChannel;

    private OutputStream writeStream;

    public ClientSession(Socket clientChannel) throws IOException {
        this.clientChannel = clientChannel;
        this.writeStream = clientChannel.getOutputStream();
    }

    public void process() {
        boolean isProcessed = true;
        while (isProcessed) {
            logger.info("SessionID " + uuid + " | Start new session.");
            try {
                byte[] buffer = readData();
                if (buffer.length <= 0) {
                    return;
                }
                PDU pdu = new PDU(uuid, buffer);
                pdu.init();
                switch (pdu.getCommandId()) {
                    case (BIND_RECEIVER):
                        logger.info("SessionID " + uuid + " | Incoming command_id is defined as RECEIVER");
                        processReceiver(buffer);
                        break;
                    case (BIND_TRANSMITTER):
                        logger.info("SessionID " + uuid + " | Incoming command_id is defined as TRANSMITTER");
                        processTransmitter(buffer);
                        break;
                    case (BIND_TRANSCEIVER):
                        logger.info("SessionID " + uuid + " | Incoming command_id is defined as TRANSCEIVER");
                        processTransciver(buffer);
                        break;
                    case (ENQUIRE_LINK):
                        logger.info("SessionID " + uuid + " | Incoming command_id is defined as ENQUIRE_LINK");
                        processEnquire(buffer, pdu);
                        break;
                    case (SUBMIT_SM):
                        logger.info("SessionID " + uuid + " | Incoming command_id is defined as SUBMIT_SM");
                        processSubmitSm(buffer);
                        break;
                    case (UNBIND):
                        logger.info("SessionID " + uuid + " | Incoming command_id is defined as UNBIND");
                        processUnbind(buffer, pdu);
                        logger.info("SessionID " + uuid + " | This command is the final session. I close the connection with the client.");
                        isProcessed = false;
                        break;
                    default:
                        logger.error("SessionID " + uuid + " | Command is not defined. Can not continue the session, I interrupt contact with the client. command_id=" + pdu.getCommandId());
                        isProcessed = false;
                }
            } catch (IOException | RuntimeException e) {
                logger.error("EXCEPTION", e);
                isProcessed = false;
            }

        }
    }

    /**
     * @param buffer
     */
    private void processReceiver(byte[] buffer) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(uuid, buffer);
        trans.init();
        PDUTransmitterResp resp = new PDUReceieverResp(uuid, PduConstants.ESME_ROK, trans.getSequenceNumber(), "TascomBank");
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processTransmitter(byte[] buffer) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(uuid, buffer);
        trans.init();
        PDUTransmitterResp resp = new PDUTransmitterResp(uuid, PduConstants.ESME_ROK, trans.getSequenceNumber(), "TascomBank");
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     */
    private void processTransciver(byte[] buffer) throws IOException {
        PDUTransmitter trans = new PDUTransmitter(uuid, buffer);
        trans.init();
        PDUTranscieverResp resp = new PDUTranscieverResp(uuid, PduConstants.ESME_ROK, trans.getSequenceNumber(), "TascomBank");
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
    private void processSubmitSm(byte[] buffer) throws IOException {
        PDUSubmitSm message = new PDUSubmitSm(uuid, buffer);
        message.init();
        PDUSubmitSmResp resp = new PDUSubmitSmResp(uuid, ESME_ROK, message.getSequenceNumber());
        writeStream.write(resp.getPdu());
    }

    /**
     * @param buffer
     * @param pdu
     */
    private void processUnbind(byte[] buffer, PDU pdu) throws IOException {
        PDUResp resp = new PDUResp(uuid, UNBIND_RESP, ESME_ROK, pdu.getSequenceNumber());
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
