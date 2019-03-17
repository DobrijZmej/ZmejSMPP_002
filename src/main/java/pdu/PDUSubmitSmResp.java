package pdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUSubmitSmResp {
    private static final Logger logger = LoggerFactory.getLogger(PDUSubmitSmResp.class);
    private int commandStatus;
    private int sequenceNumber;
    private String messageId;

    public PDUSubmitSmResp(int commandStatus, int sequenceNumber, String messageId) {

        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
        this.messageId = messageId;
    }

    public byte[] getPdu() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.reset();
        int bytes = 4 + 4 + 4 + 4+2;

        out.write(PDU.makeByteArrayFromInt(bytes, 4));
        out.write(PDU.makeByteArrayFromInt(PduConstants.SUBMIT_SM_RESP, 4));
        out.write(PDU.makeByteArrayFromInt(commandStatus, 4));
        out.write(PDU.makeByteArrayFromInt(sequenceNumber, 4));
        out.write(PDU.makeByteArrayFromInt(49, 1));
        out.write(PDU.makeByteArrayFromInt(0, 1));
        byte[] res = out.toByteArray();

        logger.debug("Prepare response:");
        logger.debug(PDU.PDUtoString(res, 16));
        logger.debug(PDU.PDUtoString(res, 10));

        return res;
    }

}
