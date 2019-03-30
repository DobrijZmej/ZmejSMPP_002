package org.dobrijzmej.smpp;

import org.dobrijzmej.smpp.config.Configuration;
import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.BlockingQueue;

/**
 * Клас для запису в чергу повідомлень, що надходять із мережі
 */
public class MessageProducer implements Runnable {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");

    private ServerSocket server;
    Socket fromclient;
    private BlockingQueue<MessageQueue> queue;

    public MessageProducer(BlockingQueue<MessageQueue> queue) throws IOException {
        Configuration conf = new Configuration();
        int port = conf.getPort();
        server = new ServerSocket(port);
        logger.info("Server listener start on port " + port);
        this.queue = queue;
    }

    /**
     * Основний цикл обробки даних із сокету
     */
    @Override
    public void run() {

        do try {
            // Очікуємо наступного підключення
            logger.info("Waiting next client");
            // підключаємось до сокету
            Socket session = this.server.accept();;
            // стартуємо обмін даними
            ClientSession sessionThread = new ClientSession(session, this.queue);
            new Thread(sessionThread).start();

            if (Thread.currentThread().isInterrupted()) {
                logger.info("Stream disconnection detected. Complete the work cycle.");
                break;
            }
            Thread.sleep(50);
        } catch (InterruptedIOException | InterruptedByTimeoutException | InterruptedException e) {
            logger.error("Stream exception disconnection detected. Complete the work cycle.", e);
            break;
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("EXCEPTION: ", e);
        }
        while (true);

    }

}
