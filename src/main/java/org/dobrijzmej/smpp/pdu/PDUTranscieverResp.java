package org.dobrijzmej.smpp.pdu;

public class PDUTranscieverResp extends PDUTransmitterResp {
    public PDUTranscieverResp(String uuid, int commandStatus, int sequenceNumber, String systemId, String labelPrefix) {
        super(uuid, commandStatus, sequenceNumber, systemId, labelPrefix);
        super.commandId = PduConstants.BIND_TRANSCEIVER_RESP;
    }
}
