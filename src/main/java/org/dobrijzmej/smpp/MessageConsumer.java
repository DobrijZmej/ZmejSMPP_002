package org.dobrijzmej.smpp;

import org.dobrijzmej.smpp.config.Configuration;
import org.dobrijzmej.smpp.config.Output;
import org.dobrijzmej.smpp.config.Params;
import org.dobrijzmej.smpp.log.Log;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Клас для обробки повідомлень, поступаючих по мережі
 */
public class MessageConsumer implements Runnable {
    private static final Logger logger = Log.initLog(MessageConsumer.class, "consumer");

    private BlockingQueue<MessageQueue> queue;

    public MessageConsumer(BlockingQueue<MessageQueue> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        logger.info("Start process consumer");
        while (true) {
            try {
                MessageQueue message = queue.take();
                logger.debug("Getting next message " + message);
                Map<String, Output> outputs;
                Configuration config = new Configuration();
                outputs = config.getOutputs();
                for (Map.Entry<String, Output> output : outputs.entrySet()) {
                    processOutput(output, message);
                }
            } catch (InterruptedException e) {
                logger.error("Stream exception disconnection detected. Complete the work cycle.", e);
                break;
            }
        }
        logger.info("End process consumer");
    }

    private void processOutput(Map.Entry<String, Output> output, MessageQueue message) {
        logger.debug("Start send message to " + output.getValue());
        try {
            if ("POST".equals(output.getValue().getMethod().toUpperCase())) {
                sendOnRest(output.getValue(), message);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendOnRest(Output output, MessageQueue message) throws IOException {
        String bodyRequest = output.getMask();
        bodyRequest = bodyRequest.replace("${phone}", message.getPhone());
        bodyRequest = bodyRequest.replace("${text}", message.getMessage());
        bodyRequest = bodyRequest.replace("${alias}", message.getAlias());

        URL url = new URL(output.getUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        logger.trace("Send body:");
        logger.trace(bodyRequest);
        DataOutputStream write = new DataOutputStream(conn.getOutputStream());
        write.write(bodyRequest.getBytes());
        BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        BufferedReader write = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder responce = new StringBuilder();
        String readLine;
        while((readLine = read.readLine())!=null){
            responce.append(readLine);
        }
        System.out.println(responce);
    }
}
