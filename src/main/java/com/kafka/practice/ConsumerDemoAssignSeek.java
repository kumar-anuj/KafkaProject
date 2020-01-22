package com.kafka.practice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerDemoAssignSeek {

    public static void main(String[] args) {
        System.out.println("Hello kafka");
        final Logger logger = LoggerFactory.getLogger(ConsumerDemoAssignSeek.class);

        String bootstrapServers ="127.0.0.1:9092";
        String groupId = "g3";
        String topic = "first_topic";

        //Step 1. Create Consumer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        //properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        //Step 2. Create Consumer
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);

        //Assing
        TopicPartition topicPartitionToReadFrom = new TopicPartition(topic, 1);
        long offsetToReadFrom = 0L;
        consumer.assign(Arrays.asList(topicPartitionToReadFrom));

        //Seek
        consumer.seek(topicPartitionToReadFrom, offsetToReadFrom);
        int numOfMessagesToRead = 2;
        int numOfMessagesReadSoFar = 0;
        boolean keepOnReading = true;

        //Step 4. Poll for new data
        while(keepOnReading) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord consumerRecord : consumerRecords) {
                numOfMessagesReadSoFar +=1;
                logger.info("Key: "+ consumerRecord.key() + ", Value: "+ consumerRecord.value());
                logger.info("Partition: "+ consumerRecord.partition() + ", Offset: "+ consumerRecord.offset());

                if(numOfMessagesReadSoFar >= numOfMessagesToRead) {
                    keepOnReading = false;
                    break;
                }
            }
        }

    }
}
