package pdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUTransmitterResp {
    private static final Logger logger = LoggerFactory.getLogger(PDUTransmitterResp.class);

    private String uuid;
    //    private int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String systemId;

    public PDUTransmitterResp(String uuid, int commandStatus, int sequenceNumber, String systemId) {

        this.uuid = uuid;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
        this.systemId = systemId;
    }


    public byte[] getPdu() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.reset();
        int bytes = 4 + 4 + 4 + 4 + systemId.length() + 1;

        out.write(PDU.makeByteArrayFromInt(bytes, 4));
        out.write(PDU.makeByteArrayFromInt(PduConstants.BIND_TRANSMITTER_RESP, 4));
        out.write(PDU.makeByteArrayFromInt(commandStatus, 4));
        out.write(PDU.makeByteArrayFromInt(sequenceNumber, 4));
        out.write(systemId.getBytes());
        out.write(PDU.makeByteArrayFromInt(0, 1));
        byte[] res = out.toByteArray();

        logger.trace("SessionId " + uuid + " | Prepare response:");
        logger.trace("SessionId " + uuid + " | " + PDU.PDUtoString(res, 16));
        logger.trace("SessionId " + uuid + " | " + PDU.PDUtoString(res, 10));

        return res;
    }
}
