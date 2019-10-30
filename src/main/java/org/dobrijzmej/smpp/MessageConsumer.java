package org.dobrijzmej.smpp;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.Logger;
import org.dobrijzmej.smpp.config.Configuration;
import org.dobrijzmej.smpp.config.Output;
import org.dobrijzmej.smpp.log.Log;
//import org.slf4j.Logger;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * <p>Клас для обробки повідомлень, поступаючих по мережі</p>
 *
 * <p>Стежимо за чергою, в яку закидають повідомлення, та запускаємо обробники в залежності від типу відправки. Назараз існують такі варіанти відправки:
 * <ul>
 *     <li>REST - відправляє на вказане посилання у форматі JSON, який можна настроювати у вигляді маски:
 *     <ul>
 *         <li>url - посилання, на яке потрібно відправити запит</li>
 *         <li>method - наразі може бути лише POST</li>
 *         <li>mask - маска для текста, який можна відправити за посиланням</li>
 *     </ul>
 *     </li>
 * </ul>
 * </p>
 *
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

    /**
     * Обробка повідомлень, що надійшли, в залежності від каналу доставки
     *
     * @param output канал, по якому потрібно доставити повідомлення
     * @param message повідомлення, що треба доставити
     */
    private void processOutput(Map.Entry<String, Output> output, MessageQueue message) {
        logger.debug("Start send message to channel " + output.getValue());
        try {
            if ("POST".equals(output.getValue().getMethod().toUpperCase())) {
                sendOnRest(output.getValue(), message);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | KeyManagementException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * <p>Реалізація передачі повідомлення через HTTP REST.
     * Адреса запиту приходить у параметрі output</p>
     *
     * Шаблони підстановки у масці:<ul>
     * <li>${phone} - номер телефону</li>
     * <li> ${text} - текст повідомлення</li>
     * <li>${alias} - назва системи, з якої відправляється повідомлення</li>
     * </ul>
     *
     * @param output структура, у якій є адреса відправлення, маска для формування повідомлення
     * @param message структура, у якій є номер телефону, текст повідомлення, та назва системи, з якої прийшло повідомлення
     * @throws IOException
     * @throws KeyStoreException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyManagementException
     */
    private void sendOnRest(Output output, MessageQueue message) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, KeyManagementException {
        // треба з маски зробити той текст, що будемо відправляти
        String bodyRequest;
        bodyRequest = output.getMask()
                .replace("${phone}", message.getPhone())
//                .replace("${text}",  message.getMessage().replaceAll("[^\\p{L}\\p{Nd}\\s]+", "*"))
//                .replace("${text}",  message.getMessage().replaceAll("[^\\x00-\\x09]+", "*").replace("\"", "\\\""))
                .replace("${text}",  message.getMessage().replace("\"", "\\\""))
                .replace("${alias}", message.getAlias());

        // підключаємо конфіг та читаємо налаштування сховища сертифікатів
        Configuration config = new Configuration();
        final String KEY_STORE_FILE_NAME = config.getKeyStoreFileName();
        final String KEY_STORE_PASS = config.getKeyStorePass();

        // створюємо сховище, та завантажуємо сертифікати
        KeyStore key = KeyStore.getInstance("JKS");
        key.load(Files.newInputStream(Paths.get(KEY_STORE_FILE_NAME)), KEY_STORE_PASS.toCharArray());

        // перевантажуємо сертифікати до контексту, плюс довіряємо самопідписаним сертифікатам
        SSLContext ssl = SSLContexts.custom().loadTrustMaterial(key, new TrustSelfSignedStrategy()).build();

        // збираємо параметри безпеки в одну купу
        SSLConnectionSocketFactory connection = new SSLConnectionSocketFactory(
                ssl,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

        // створюємо підключення до серверу
        HttpClient http = HttpClients.custom().setSSLSocketFactory(connection).build();

        // наповнюємо запит даними
        HttpPost post = new HttpPost(output.getUrl());
        post.setEntity(new StringEntity(bodyRequest, HTTP.UTF_8));
//        post.setEntity(new StringEntity(bodyRequest));
        post.setHeader("Accept", "application/json");
        post.setHeader("Content-type", "application/json");
        // відправляємо на сервер
        HttpResponse responce = http.execute(post);

        // трохи логів
        logger.info("Send body to URL["+output.getUrl()+"]:");
        logger.info(bodyRequest);
        // записуємо результат
        logger.info(responce.getStatusLine().toString());
        logger.info(EntityUtils.toString(responce.getEntity()));
    }
}
