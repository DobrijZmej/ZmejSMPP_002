package org.dobrijzmej.smpp.pdu;

import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Клас отримання самого повідомлення, з текстом та номером телефону отримувача
 */
public class PDUSubmitSm extends PDU {
    private static final Logger logger = Log.initLog(PDUSubmitSm.class, "sessions");
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
    private int headerLength;
    private int headerPartsCount;
    private int headerPartNumber;

    /**
     * Отримання байтів, та запис до класу
     *
     * @param data
     */
    public PDUSubmitSm(String labelPrefix, byte[] data) {
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
        this.protokolId = data[offset];
        offset += 1;
        this.priorityFlag = data[offset];
        offset += 1;
        this.scheduleDeliveryTime = PDU.getStringData(data, offset);
        offset += scheduleDeliveryTime.length() + 1;
        this.validityPeriod = PDU.getStringData(data, offset);
        offset += validityPeriod.length() + 1;
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

        // далі йде чи вже текст повідомлення, чи хидер додаткових атрібутів
        // фактично, за атрібути повинний відповідати флаг у esmClass, але щось не завжди...
        this.headerLength = data[offset];
        if (this.headerLength == 5) {
            byte[] header = new byte[this.headerLength];
            System.arraycopy(data, offset + 1, header, 0, this.headerLength);
            logger.trace(PDU.PDUtoString(header, 16));
            this.headerPartsCount = header[3];
            this.headerPartNumber = header[4];
            offset += this.headerLength + 1;
        }

        // залишається тільки розібрати текст повідомлення
        byte[] data_message = new byte[(data.length - offset)];
        System.arraycopy(data, offset, data_message, 0, data.length - offset);
        this.shortMessage = parseMessage(dataCoding, data_message);

        logger.trace(labelPrefix + "sequenceNumber:" + sequenceNumber);
        logger.trace(labelPrefix + "serviceType:" + serviceType);
        logger.trace(labelPrefix + "sourceAddr:" + sourceAddr);
        logger.info(labelPrefix + "destinationAddr:" + destinationAddr);
        logger.trace(labelPrefix + "esmClass:" + esmClass);
        logger.trace(labelPrefix + "dataCoding:" + dataCoding);
        logger.trace(labelPrefix + "headerLength:" + headerLength);
        logger.trace(labelPrefix + "headerPartsCount:" + headerPartsCount);
        logger.trace(labelPrefix + "headerPartNumber:" + headerPartNumber);
        logger.info(labelPrefix + "shortMessage:" + shortMessage);

    }

    private String parseMessage(int dataCoding, byte[] data_message) {
        logger.trace(labelPrefix+PDU.PDUtoString(data_message, 16));
        logger.trace(labelPrefix+"string:"+PDU.getStringData(data_message, 0));
        logger.trace(labelPrefix+"UTF_8"+(new String(data_message, StandardCharsets.UTF_8)));
        logger.trace(labelPrefix+"UTF_16"+(new String(data_message, StandardCharsets.UTF_16)));
        logger.trace(labelPrefix+"US_ASCII"+(new String(data_message, StandardCharsets.US_ASCII)));
        String message;
        switch (dataCoding) {
            case 8:
                message = new String(data_message, StandardCharsets.UTF_16);
                break;
            case 35:
                message = new String(data_message, StandardCharsets.UTF_8);
                break;
            default:
                message = PDU.getStringData(data_message, 0);
        }
        return message;
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

    public int getHeaderLength() {
        return headerLength;
    }

    public int getHeaderPartsCount() {
        return headerPartsCount;
    }

    public int getHeaderPartNumber() {
        return headerPartNumber;
    }
}
