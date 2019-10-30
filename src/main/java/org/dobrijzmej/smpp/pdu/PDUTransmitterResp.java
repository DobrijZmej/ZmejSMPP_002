package org.dobrijzmej.smpp.pdu;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.dobrijzmej.smpp.log.Log;
//import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUTransmitterResp {
    private static final Logger logger = Log.initLog(PDU.class, "sessions");

    private String uuid;
    protected int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String systemId;
    private String labelPrefix;

    public PDUTransmitterResp(String uuid, int commandStatus, int sequenceNumber, String systemId, String labelPrefix) {

        this.uuid = uuid;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
        this.systemId = systemId;
        this.commandId = PduConstants.BIND_TRANSMITTER_RESP;
        this.labelPrefix = labelPrefix;
    }


    public byte[] getPdu() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.reset();
        int bytes = 4 + 4 + 4 + 4 + systemId.length() + 1;

        out.write(PDU.makeByteArrayFromInt(bytes, 4));
        out.write(PDU.makeByteArrayFromInt(commandId, 4));
        out.write(PDU.makeByteArrayFromInt(commandStatus, 4));
        out.write(PDU.makeByteArrayFromInt(sequenceNumber, 4));
        out.write(systemId.getBytes());
        out.write(PDU.makeByteArrayFromInt(0, 1));
        byte[] res = out.toByteArray();

        logger.trace(labelPrefix + "Prepare response:");
        logger.trace(labelPrefix + PDU.PDUtoString(res, 16));
        logger.trace(labelPrefix + PDU.PDUtoString(res, 10));

        return res;
    }
}
