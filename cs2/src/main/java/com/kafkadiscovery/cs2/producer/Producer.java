package com.kafkadiscovery.cs2.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {
    private final KafkaProducer<String, String> kafkaProducer;
    private final String topic;

    public Producer(String servers, String topic) {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        this.kafkaProducer = new KafkaProducer<>(properties);
        this.topic = topic;
    }

    public void sendRecord(String command) {
        kafkaProducer.send(new ProducerRecord<>(topic, command));
    }

    public void close() {
        kafkaProducer.close();
    }
}
