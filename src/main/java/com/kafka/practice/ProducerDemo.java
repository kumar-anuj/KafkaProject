package com.kafka.practice;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerDemo {
    public static void main(String[] args) {
        System.out.println("Hello kafka");

        //Step 1. Create Producer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Step 2. Create Producer
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(properties);

        //Step 3. Create Producer Record
        ProducerRecord<String, String> record =  new ProducerRecord<String, String>("first_topic", "message from java");

        //Step 4. Send Data
        producer.send(record);

        //flush data
        producer.flush();
        //flush data and close producer
        producer.close();
    }
}
