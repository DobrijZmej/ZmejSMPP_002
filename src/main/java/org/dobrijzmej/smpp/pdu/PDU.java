package org.dobrijzmej.smpp.pdu;

//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.dobrijzmej.smpp.log.Log;
//import org.slf4j.Logger;

import java.nio.ByteBuffer;

/**
 * Базовий клас, який реалізує мінімум атрібутів протоколу
 */
public class PDU {
    private static final Logger logger = Log.initLog(PDU.class, "sessions");
    private byte[] data;

    private String labelPrefix;
    private int commandLength;
    private int commandId;
    private int commandStatus;
    private int sequenceNumber;

    public PDU(String labelPrefix, byte[] data) {
        this.labelPrefix = labelPrefix;
        this.data = data;
    }

    public void init() {
        String text = "Start read PDU from data";
        try {
            int offset = 0;
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
            logger.error(labelPrefix + text, e);
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

        StringBuilder result = new StringBuilder();
        if (type == 16) {
            char[] hexArray = "0123456789ABCDEF".toCharArray();
            for (int j = 0; j < data.length; j++) {
                int v = data[j] & 0xFF;
                result.append(hexArray[v >>> 4])
                        .append(hexArray[v & 0x0F])
                        .append(" ");
            }
            return result.toString();
        }

        for (int b : data) {
            result.append(b)
                    .append(" ");
        }
        return result.toString();
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

    /**
     * Метод приймає байтовий масив, так конвертує його з вказаної позиції у стрічку символів
     * Конвертація відбувається доти, доки не буде знайдено символ 0x00, чи до завершення байтового масиву
     *
     * @param data   - байтовий масив
     * @param offset - позиція, з якої треба почати конвертацію
     * @return - стрічка символів
     */
    public static String getStringData(byte[] data, int offset) {
        StringBuilder result = new StringBuilder();
        for (int i = offset; i < data.length; i++) {
            if (data[i] == 0) {
                return result.toString();
            }
            result.append((char) data[i]);
        }
        return result.toString();
    }

}
