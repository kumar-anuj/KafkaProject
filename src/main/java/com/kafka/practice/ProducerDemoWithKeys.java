package com.kafka.practice;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithKeys {

    public static void main(String[] args) {
        System.out.println("Hello kafka");
        final Logger logger = LoggerFactory.getLogger(ProducerDemoWithKeys.class);

        //Step 1. Create Producer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Step 2. Create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        for(int i =0 ;i < 10; i ++) {
            String key = "id_" + i;
            //Step 3. Create Producer Record
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("first_topic", key, "message from java : " + i);
            logger.info("Key:" + key);

            //Step 4. Send Data
            producer.send(record, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    //executes every time a record is sent successfully or an exception in thrown
                    if (e != null)
                        logger.error("Exception:" + e);
                    else {
                        logger.info("Topic:" + recordMetadata.topic());
                        logger.info("Partition:" + recordMetadata.partition());
                        logger.info("Offset:" + recordMetadata.offset());
                        logger.info("Timestamp:" + recordMetadata.timestamp());
                    }
                }
            });
        }

        //flush data
        producer.flush();
        //flush data and close producer
        producer.close();
    }
}
