package com.dce.kafka.sample.api.producer;

import com.dce.kafka.sample.Cons;

import java.util.Properties;

/**
 * Producer配置
 *
 * @anthor lcl
 */
public class ProducerConfig {
    public static Properties initConfig(String serializerKey, String serializerValue) {
        Properties properties = new Properties();
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Cons.host_port);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, "3");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG, "producer-1");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerKey);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerValue);
        return properties;
    }
}
