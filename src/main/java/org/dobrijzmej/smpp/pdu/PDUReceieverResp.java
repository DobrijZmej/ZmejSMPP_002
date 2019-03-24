package org.dobrijzmej.smpp.pdu;

public class PDUReceieverResp extends PDUTransmitterResp {
    public PDUReceieverResp(String uuid, int commandStatus, int sequenceNumber, String systemId) {
        super(uuid, commandStatus, sequenceNumber, systemId);
        super.commandId = PduConstants.BIND_RECEIVER_RESP;
    }
}
