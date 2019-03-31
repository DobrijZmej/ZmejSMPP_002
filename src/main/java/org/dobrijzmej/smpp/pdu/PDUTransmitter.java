package org.dobrijzmej.smpp.pdu;

import org.dobrijzmej.smpp.config.User;
import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Клас для обробки початкового PDU з початковими командами
 */
public class PDUTransmitter extends PDU {
    private static final Logger logger = Log.initLog(PDUTransmitter.class, "sessions");
    private byte[] data;

    private String labelPrefix;
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

    private User user;

    public PDUTransmitter(String labelPrefix, byte[] data) {
        super(labelPrefix, data);
        this.data = data;
        this.labelPrefix = labelPrefix;
    }

    public void init() {
        int offset = 0;
        this.commandLength = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandId = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.commandStatus = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.sequenceNumber = ByteBuffer.wrap(data, offset, 4).getInt();
        offset += 4;
        this.systemId = PDU.getStringData(data, offset);
        offset += systemId.length() + 1;
        this.password = PDU.getStringData(data, offset);
        offset += password.length() + 1;
        this.systemTun = PDU.getStringData(data, offset);
//        System.arraycopy(data, 0, this.commandLength, 0, 4);
        logger.info(labelPrefix + "systemId:" + systemId);
        logger.debug(labelPrefix + "password:" + password);
        logger.debug(labelPrefix + "systemTun:" + systemTun);

    }

    public String getSystemId() {
        return systemId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Спроба авторизації користувача, що надійшов в атрібутах system_id та password
     *
     * @param users список користувачів
     */
    public void authorize(Map<String, User> users) {
        this.user = new User("---", "", "");
        for (Map.Entry<String, User> user : users.entrySet()) {
            logger.trace(labelPrefix+user.getValue().toString());
            if (checkLoginPass(user.getValue())) {
                this.user = user.getValue();
                logger.info(labelPrefix+"Authorized user on param [" + user.getKey() + "]: login [" + this.user.getUsername() + "], alias [" + this.user.getAlias() + "]");
                break;
            }
        }
        if ("---".equals(this.user.getUsername())) {
            logger.trace(labelPrefix+"Error authorize user with param: login [" + this.systemId + "], password [" + this.password + "]");
        }
    }

    private boolean checkLoginPass(User user) {
        if (this.systemId.equals(user.getUsername())
                && this.password.equals(user.getPassword())) {
            return true;
        }
        return false;
    }

    public User getUser() {
        return user;
    }
}
