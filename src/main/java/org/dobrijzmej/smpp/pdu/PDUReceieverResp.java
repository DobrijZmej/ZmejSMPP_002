package org.dobrijzmej.smpp.pdu;

public class PDUReceieverResp extends PDUTransmitterResp {
    public PDUReceieverResp(String uuid, int commandStatus, int sequenceNumber, String systemId, String labelPrefix) {
        super(uuid, commandStatus, sequenceNumber, systemId, labelPrefix);
        super.commandId = PduConstants.BIND_RECEIVER_RESP;
    }
}
