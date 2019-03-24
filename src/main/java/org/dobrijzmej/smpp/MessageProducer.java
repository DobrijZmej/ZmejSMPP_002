package org.dobrijzmej.smpp;

import org.dobrijzmej.smpp.config.Configuration;
import org.dobrijzmej.smpp.log.Log;
import org.dobrijzmej.smpp.ClientSession;
import org.slf4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.concurrent.BlockingQueue;

/**
 * Клас для запису в чергу повідомлень, що надходять із мережі
 */
public class MessageProducer implements Runnable {
    private static final Logger logger = Log.initLog(ClientSession.class, "main");

    private ServerSocket server;
    Socket fromclient;
    private BlockingQueue<String> queue;

    public MessageProducer(BlockingQueue<String> queue) throws IOException {
        Configuration conf = new Configuration();
        int port = conf.readPort();
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
            // підключаємось до сокету
            fromclient = this.server.accept();
            // стартуємо обмін даними
            startSession(fromclient);
            // закриваємо сесію
            fromclient.close();
            // Очікуємо наступного підключення
            logger.info("Waiting data");
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

    /**
     * Очікування даних від клієнта з черги
     *
     * @param fromClient - посилання на сесію з сокетом
     */
    private void startSession(Socket fromClient) throws IOException {

        ClientSession session = new ClientSession(fromClient, this.queue);
        session.process();
    }

}
