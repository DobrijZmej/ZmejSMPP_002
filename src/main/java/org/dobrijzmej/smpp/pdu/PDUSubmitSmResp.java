package org.dobrijzmej.smpp.pdu;

import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUSubmitSmResp {
    private static final Logger logger = Log.initLog(PDU.class, "sessions");

    private String uuid;
    private int commandStatus;
    private int sequenceNumber;
    private String labelPrefix;

    public PDUSubmitSmResp(String uuid, int commandStatus, int sequenceNumber, String labelPrefix) {

        this.uuid = uuid;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
        this.labelPrefix = labelPrefix;
//        System.out.println(":"+sequenceNumber+"/"+this.sequenceNumber);
    }

    public byte[] getPdu() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.reset();
        int bytes = 4 + 4 + 4 + 4 + uuid.length() + 1;

        out.write(PDU.makeByteArrayFromInt(bytes, 4));
        out.write(PDU.makeByteArrayFromInt(PduConstants.SUBMIT_SM_RESP, 4));
        out.write(PDU.makeByteArrayFromInt(commandStatus, 4));
        out.write(PDU.makeByteArrayFromInt(sequenceNumber, 4));
        out.write(uuid.getBytes());
        out.write(PDU.makeByteArrayFromInt(0, 1));
        byte[] res = out.toByteArray();

        logger.trace(labelPrefix + "Prepare response:");
        logger.trace(labelPrefix + PDU.PDUtoString(res, 16));
        logger.trace(labelPrefix + PDU.PDUtoString(res, 10));

        return res;
    }

}
