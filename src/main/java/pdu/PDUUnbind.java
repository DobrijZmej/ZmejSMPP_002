package pdu;

/**
 * Завершення сеансу зв'язку
 */
public class PDUUnbind extends PDU {

    public PDUUnbind(byte[] data) {
        super(data);
    }
}
