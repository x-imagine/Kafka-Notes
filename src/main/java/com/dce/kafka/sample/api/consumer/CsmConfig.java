package com.dce.kafka.sample.api.consumer;

import java.util.Properties;

import static com.dce.kafka.constants.Cons.HOST_PORT;

/**
 * Consumer配置
 *
 * @anthor lcl
 */
public class CsmConfig {
    public static Properties initConfig(String deserializerKey, String deserializerValue) {
        Properties properties = new Properties();
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_PORT);
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "test_consumer_group_id");
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.CLIENT_ID_CONFIG, "test_consumer_client_id");
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializerKey);
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializerValue);
        return properties;
    }
}
