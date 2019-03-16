package pdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * Базовий клас, який реалізує мінімум атрібутів протоколу
 */
public class PDU {
    static final Logger logger = LoggerFactory.getLogger(PDU.class);
    private byte[] data;

    private int commandLength;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;

    public PDU(byte[] data) {
        String text = "Start read PDU from data";
        try {
            int offset = 0;
            this.data = data;
            text = "Before read commandLength";
            this.commandLength = ByteBuffer.wrap(data, offset, 4).getInt();
            offset += 4;
            text = "Before read commandId";
            this.commandId = ByteBuffer.wrap(data, offset, 4).getInt();
            offset += 4;
            text = "Before read commandStatus";
            this.commandStatus = ByteBuffer.wrap(data, offset, 4).getInt();
            offset += 4;
            text = "Before read sequenceNumber";
            this.sequenceNumber = ByteBuffer.wrap(data, offset, 4).getInt();
        } catch (RuntimeException e) {
            logger.error(text, e);
            throw e;
        }
    }

    public int getCommandLength() {
        return commandLength;
    }

    public int getCommandId() {
        return commandId;
    }

    public int getCommandStatus() {
        return commandStatus;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Метод вертає стрічку даних у вигляді 16-річних даних, чи 10-річних, відокремлених пробілами
     *
     * @param data - стрічка байтових даних
     * @param type - тип даних (16 чи будь якій інший, який буде означати 10)
     * @return - повертається одна чи інша послідовність, у залежності від переданого типу
     */
    public static String PDUtoString(byte[] data, int type) {
        StringBuilder s = new StringBuilder();
        int i = data.length;
        for (int r = 0; r < i; r++) {
            int b = data[r];
            if (type == 16) {
                s.append(b < 10 ? "0" + Integer.toString(b, 16) : Integer.toString(b, 16));
            } else {
                s.append(b).append(" ");
            }
        }
        return s.toString();
    }

    /**
     * Метод конвертує ціле число у байтовий буфер
     *
     * @param i        - ціле число
     * @param numBytes - кількість байт
     * @return - байтовий буфер, що повертається
     */
    public static byte[] makeByteArrayFromInt(int i, int numBytes) {
        byte[] result = new byte[numBytes];
        int shiftBits = (numBytes - 1) * 8;

        for (int j = 0; j < numBytes; j++) {
            result[j] = (byte) (i >>> shiftBits);
            shiftBits -= 8;
        }
        return result;
    }

}
