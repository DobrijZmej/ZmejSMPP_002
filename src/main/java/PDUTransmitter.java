import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Клас для обробки початкового PDU з початковими командами
 */
public class PDUTransmitter {
    static final Logger logger = LoggerFactory.getLogger(ZmejSMPP.class);
    private byte[] data;

    private int commandLength;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;
    private String systemId;
    private String password;
    private String systemTun;
    private int interfaceVersion;
    private int addrTon;
    private int addrNpi;
    private int addrRange;

    public PDUTransmitter(byte[] data) {
        int offset = 0;
        this.data = data;
        this.commandLength = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandId = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandStatus = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.sequenceNumber = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.systemId = getStringData(data, offset);
        offset += systemId.length();
        this.password = getStringData(data, offset);
        offset += password.length();
        this.systemTun = getStringData(data, offset);
//        System.arraycopy(data, 0, this.commandLength, 0, 4);
        System.out.println(systemId);
        System.out.println(password);
        System.out.println(systemTun);
        logger.debug("systemId:"+systemId);
        logger.debug("password:"+password);
        logger.debug("systemTun:"+systemTun);

    }


    public int getSequenceNumber() {
        return sequenceNumber;
    }

    private String getStringData(byte[] data, int offset) {
        String result = "";
        for(int i=offset;i<data.length;i++){
            result += Character.toString(data[i]);
            if(data[i]==0){
                return result;
            }
        }
        return result;
    }
}
