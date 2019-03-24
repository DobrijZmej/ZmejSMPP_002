package org.dobrijzmej.smpp.pdu;

public class PDUTranscieverResp extends PDUTransmitterResp {
    public PDUTranscieverResp(String uuid, int commandStatus, int sequenceNumber, String systemId) {
        super(uuid, commandStatus, sequenceNumber, systemId);
        super.commandId = PduConstants.BIND_TRANSCEIVER_RESP;
    }
}
