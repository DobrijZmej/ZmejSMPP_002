package org.dobrijzmej.smpp.pdu;

import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PDUDataSm extends PDU {
    private static final Logger logger = Log.initLog(PDUDataSm.class, "sessions");
    private byte[] data;

    private String labelPrefix;
    private int commandLength;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String serviceType;
    private int sourceAddrTon;
    private int sourcrAddrNpi;
    private String sourceAddr;
    private int destAddrTon;
    private int destAddrNpi;
    private String destinationAddr;
    private int esmClass;
    private int registeredDelivery;
    private int dataCoding;
    private String shortMessage;
    private int sourcePortTag;
    private int sourcePortLength;
    private int sourcePortValue;


    public PDUDataSm(String labelPrefix, byte[] data) {
        super(labelPrefix, data);
        this.labelPrefix = labelPrefix;
        this.data = data;
    }

    /**
     * Ініціювання внутрішніх параметрів
     */
    public void init() {
        int offset = 0;
        this.commandLength = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandId = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandStatus = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.sequenceNumber = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.serviceType = PDU.getStringData(data, offset);
        offset += serviceType.length() + 1;
        this.sourceAddrTon = data[offset];
        offset += 1;
        this.sourcrAddrNpi = data[offset];
        offset += 1;
        this.sourceAddr = PDU.getStringData(data, offset);
        offset += sourceAddr.length() + 1;
        this.destAddrTon = data[offset];
        offset += 1;
        this.destAddrNpi = data[offset];
        offset += 1;
        this.destinationAddr = PDU.getStringData(data, offset);
        offset += destinationAddr.length() + 1;
        this.esmClass = data[offset];
        offset += 1;
        this.registeredDelivery = data[offset];
        offset += 1;
        this.dataCoding = data[offset];
        offset += 1;
        this.sourcePortTag = data[offset];//+data[offset+1];//ByteBuffer.wrap(data, offset, 2).getInt();
        offset += 1;
        this.sourcePortLength = data[offset];//+data[offset+1];//ByteBuffer.wrap(data, offset, 2).getInt();
        offset += 1;
        this.sourcePortValue = data[offset] + data[offset + 1];//ByteBuffer.wrap(data, offset, 2).getInt();
        offset += 2;

        // залишається тільки розібрати текст повідомлення
        byte[] data_message = new byte[(data.length - offset)];
        System.arraycopy(data, offset, data_message, 0, data.length - offset);
        this.shortMessage = parseMessage(dataCoding, data_message);

        logger.trace(labelPrefix + "sequenceNumber:" + sequenceNumber);
        logger.trace(labelPrefix + "serviceType:" + serviceType);
        logger.trace(labelPrefix + "sourceAddr:" + sourceAddr);
        logger.trace(labelPrefix + "destinationAddr:" + destinationAddr);
        logger.trace(labelPrefix + "esmClass:" + esmClass);
        logger.trace(labelPrefix + "dataCoding:" + dataCoding);
        logger.trace(labelPrefix + "sourcePortTag:" + sourcePortTag);
        logger.trace(labelPrefix + "sourcePortLength:" + sourcePortLength);
        logger.trace(labelPrefix + "sourcePortValue:" + sourcePortValue);
        logger.info(labelPrefix + "shortMessage:" + shortMessage);

    }

    private String parseMessage(int dataCoding, byte[] data_message) {
        System.out.println(PDU.getStringData(data_message, 0));
        System.out.println(new String(data_message, StandardCharsets.UTF_8));
        System.out.println(new String(data_message, StandardCharsets.UTF_16));
        System.out.println(new String(data_message, StandardCharsets.US_ASCII));
        if (dataCoding == 0) {
            return PDU.getStringData(data_message, 0);
        }
        if (dataCoding == 35) {
            return new String(data_message, StandardCharsets.UTF_8);
        }
        if (dataCoding == 8) {
            return new String(data_message, StandardCharsets.UTF_16);
        }
        return "123";
    }

    @Override
    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
