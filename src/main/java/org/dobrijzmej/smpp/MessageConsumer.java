package org.dobrijzmej.smpp;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;

/**
 * Клас для обробки повідомлень, поступаючих по мережі
 */
public class MessageConsumer implements Runnable {

    private BlockingQueue<ByteBuffer> queue;

    public MessageConsumer(BlockingQueue<ByteBuffer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

    }
}
