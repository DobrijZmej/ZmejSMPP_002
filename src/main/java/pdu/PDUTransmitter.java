package pdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Клас для обробки початкового PDU з початковими командами
 */
public class PDUTransmitter extends PDU {
    static final Logger logger = LoggerFactory.getLogger(PDUTransmitter.class);
    private byte[] data;

    private int commandLength;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String systemId;
    private String password;
    private String systemTun;
    private int interfaceVersion;
    private int addrTon;
    private int addrNpi;
    private int addrRange;

    public PDUTransmitter(byte[] data) {
        super(data);
        int offset = 0;
        this.data = data;
        this.commandLength = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandId = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandStatus = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.sequenceNumber = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.systemId = PDU.getStringData(data, offset);
        offset += systemId.length();
        this.password = PDU.getStringData(data, offset);
        offset += password.length();
        this.systemTun = PDU.getStringData(data, offset);
//        System.arraycopy(data, 0, this.commandLength, 0, 4);
        logger.debug("systemId:" + systemId);
        logger.debug("password:" + password);
        logger.debug("systemTun:" + systemTun);

    }


    public int getSequenceNumber() {
        return sequenceNumber;
    }

}
