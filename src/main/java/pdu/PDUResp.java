package pdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUResp {
    private static final Logger logger = LoggerFactory.getLogger(PDUResp.class);

    private String uuid;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;

    public PDUResp(String uuid, int commandId, int commandStatus, int sequenceNumber) {

        this.uuid = uuid;
        this.commandId = commandId;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
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

        logger.trace("SessionId " + uuid + " | Prepare response:");
        logger.trace("SessionId " + uuid + " | " + PDU.PDUtoString(res, 16));
        logger.trace("SessionId " + uuid + " | " + PDU.PDUtoString(res, 10));

        return res;
    }
}
