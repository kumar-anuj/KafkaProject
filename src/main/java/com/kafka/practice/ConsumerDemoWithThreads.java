package com.kafka.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerDemoWithThreads {
    public static void main(String[] args) {
        System.out.println("Hello kafka");
        ConsumerDemoWithThreads consumerDemoWithThreads = new ConsumerDemoWithThreads();
        consumerDemoWithThreads.run();
    }

    private void run() {
        final Logger logger = LoggerFactory.getLogger(ConsumerDemoWithThreads.class);
        CountDownLatch latch = new CountDownLatch(1);

        String bootstrapServers ="127.0.0.1:9092";
        String groupId = "g3";

        //Step 1. Create Consumer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ConsumerRunnable consumerRunnable = new ConsumerRunnable(latch, properties);

        Thread newThread = new Thread(consumerRunnable);
        newThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("caught shutdown hook");
            consumerRunnable.shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info("Application has exited");
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("logger got interrupted", e);
        } finally {
            logger.info("Application is closing");
        }
    }

    public class ConsumerRunnable implements Runnable {
        private CountDownLatch latch;
        private KafkaConsumer<String, String> consumer;
        final Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);

        public ConsumerRunnable(CountDownLatch latch, Properties properties) {
            this.latch = latch;
            //Step 2. Create Consumer
            consumer = new KafkaConsumer<>(properties);
        }

        @Override
        public void run() {
            String topic = "first_topic";

            //Step 3. Subscribe consumer to topic(s)
            consumer.subscribe(Arrays.asList(topic));

            //Step 4. Poll for new data
            try {
                while (true) {
                    ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

                    for (ConsumerRecord consumerRecord : consumerRecords) {
                        logger.info("Key: " + consumerRecord.key() + ", Value: " + consumerRecord.value());
                        logger.info("Partition: " + consumerRecord.partition() + ", Offset: " + consumerRecord.offset());
                    }
                }
            } catch(WakeupException e) {
                logger.info("Recieved shutdown signal");
            } finally {
                consumer.close();
                latch.countDown();
            }
        }

        public void shutdown() {
            consumer.wakeup();
        }
    }
}
