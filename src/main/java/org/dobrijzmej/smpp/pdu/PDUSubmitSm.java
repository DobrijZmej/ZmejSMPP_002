package org.dobrijzmej.smpp.pdu;

import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;

import java.nio.ByteBuffer;

/**
 * Клас отримання самого повідомлення, з текстом та номером телефону отримувача
 */
public class PDUSubmitSm extends PDU {
    private static final Logger logger = Log.initLog(PDU.class, "sessions");
    private byte[] data;

    private String uuid;
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
    private int protokolId;
    private int priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private int registeredDelivery;
    private int replaceIfPresentFlag;
    private int dataCoding;
    private int smDefaultMessageId;
    private int smLength;
    private String shortMessage;

    /**
     * Отримання байтів, та запис до класу
     *
     * @param data
     */
    public PDUSubmitSm(String uuid, byte[] data) {
        super(uuid, data);
        this.uuid = uuid;
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
        offset += serviceType.length()+1;
        this.sourceAddrTon = data[offset];
        offset += 1;
        this.sourcrAddrNpi = data[offset];
        offset += 1;
        this.sourceAddr = PDU.getStringData(data, offset);
        offset += sourceAddr.length()+1;
        this.destAddrTon = data[offset];
        offset += 1;
        this.destAddrNpi = data[offset];
        offset += 1;
        this.destinationAddr = PDU.getStringData(data, offset);
        offset += destinationAddr.length()+1;
        this.esmClass = data[offset];
        offset += 1;
        this.protokolId = data[offset];
        offset += 1;
        this.priorityFlag = data[offset];
        offset += 1;
        this.scheduleDeliveryTime = PDU.getStringData(data, offset);
        offset += scheduleDeliveryTime.length()+1;
        this.validityPeriod = PDU.getStringData(data, offset);
        offset += validityPeriod.length()+1;
        this.registeredDelivery = data[offset];
        offset += 1;
        this.replaceIfPresentFlag = data[offset];
        offset += 1;
        this.dataCoding = data[offset];
        offset += 1;
        this.smDefaultMessageId = data[offset];
        offset += 1;
        this.smLength = data[offset];
        offset += 1;
        this.shortMessage = PDU.getStringData(data, offset);

        logger.trace("SessionId " + uuid + " | sequenceNumber:" + sequenceNumber);
        logger.trace("SessionId " + uuid + " | serviceType:" + serviceType);
        logger.trace("SessionId " + uuid + " | sourceAddr:" + sourceAddr);
        logger.info("SessionId " + uuid + " | destinationAddr:" + destinationAddr);
        logger.trace("SessionId " + uuid + " | dataCoding:" + dataCoding);
        logger.info("SessionId " + uuid + " | shortMessage:" + shortMessage);

    }

    public String getShortMessage() {
        return shortMessage;
    }

    public String getDestinationAddr() {
        return destinationAddr;
    }

    @Override
    public int getSequenceNumber() {
        return sequenceNumber;
    }
}
