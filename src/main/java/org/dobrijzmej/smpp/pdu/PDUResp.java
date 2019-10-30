package org.dobrijzmej.smpp.pdu;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.dobrijzmej.smpp.log.Log;
//import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUResp {
    private static final Logger logger = Log.initLog(PDUResp.class, "sessions");

    private String uuid;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String labelPrefix;

    public PDUResp(String uuid, int commandId, int commandStatus, int sequenceNumber, String labelPrefix) {

        this.uuid = uuid;
        this.commandId = commandId;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
        this.labelPrefix = labelPrefix;
    }

    public byte[] getPdu() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.reset();
        int bytes = 4 + 4 + 4 + 4;

        out.write(PDU.makeByteArrayFromInt(bytes, 4));
        out.write(PDU.makeByteArrayFromInt(commandId, 4));
        out.write(PDU.makeByteArrayFromInt(commandStatus, 4));
        out.write(PDU.makeByteArrayFromInt(sequenceNumber, 4));
        byte[] res = out.toByteArray();

        logger.trace(labelPrefix + "Prepare response:");
        logger.trace(labelPrefix + PDU.PDUtoString(res, 16));
        logger.trace(labelPrefix + PDU.PDUtoString(res, 10));

        return res;
    }
}
