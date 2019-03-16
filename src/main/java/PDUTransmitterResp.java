import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PDUTransmitterResp {
    static final Logger logger = LoggerFactory.getLogger(PDUTransmitterResp.class);

//    private int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String systemId;

    public PDUTransmitterResp(int commandStatus, int sequenceNumber, String systemId) {

//        this.commandId = commandId;
        this.commandStatus = commandStatus;
        this.sequenceNumber = sequenceNumber;
        this.systemId = systemId;
    }

    private byte[] makeByteArrayFromInt(int i, int numBytes) {
        byte[] result = new byte[numBytes];
        int shiftBits = (numBytes - 1) * 8;

        for (int j = 0; j < numBytes; j++) {
            result[j] = (byte) (i >>> shiftBits);
            shiftBits -= 8;
        }
        return result;
    }


    public byte[] getPdu() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.reset();
        int bytes =4+4+4+4+systemId.length()+1;

        out.write(makeByteArrayFromInt(bytes, 4));
        out.write(makeByteArrayFromInt(PduConstants.BIND_TRANSMITTER_RESP, 4));
        out.write(makeByteArrayFromInt(commandStatus, 4));
        out.write(makeByteArrayFromInt(sequenceNumber, 4));
        out.write(systemId.getBytes());
        out.write(makeByteArrayFromInt(0, 1));
        byte[] res = out.toByteArray();

        StringBuilder s = new StringBuilder();
        StringBuilder d = new StringBuilder();
        for (int r = 0; r<bytes; r++){
            int b = res[r];
            s.append(b<10?"0"+Integer.toString(b, 16):Integer.toString(b, 16));
            d.append(b).append(" ");
        }

        logger.debug("Prepare response:");
        logger.debug(s.toString());
        logger.debug(d.toString());

        return res;
    }
}
