package pdu;

import log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUSubmitSmResp {
    private static final Logger logger = Log.initLog(PDU.class, "sessions");

    private String uuid;
    private int commandStatus;
    private int sequenceNumber;

    public PDUSubmitSmResp(String uuid, int commandStatus, int sequenceNumber) {

        this.uuid = uuid;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
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

        logger.trace("SessionId " + uuid + " | Prepare response:");
        logger.trace("SessionId " + uuid + " | " + PDU.PDUtoString(res, 16));
        logger.trace("SessionId " + uuid + " | " + PDU.PDUtoString(res, 10));

        return res;
    }

}
