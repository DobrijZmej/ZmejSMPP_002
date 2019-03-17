package pdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUUnbindResp {
    private static final Logger logger = LoggerFactory.getLogger(PDUUnbindResp.class);
    private int commandStatus;
    private int sequenceNumber;

    public PDUUnbindResp(int commandStatus, int sequenceNumber) {

        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
    }

    public byte[] getPdu() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.reset();
        int bytes = 4 + 4 + 4 + 4;

        out.write(PDU.makeByteArrayFromInt(bytes, 4));
        out.write(PDU.makeByteArrayFromInt(PduConstants.UNBIND_RESP, 4));
        out.write(PDU.makeByteArrayFromInt(commandStatus, 4));
        out.write(PDU.makeByteArrayFromInt(sequenceNumber, 4));
        byte[] res = out.toByteArray();

        logger.debug("Prepare response:");
        logger.debug(PDU.PDUtoString(res, 16));
        logger.debug(PDU.PDUtoString(res, 10));

        return res;
    }
}
