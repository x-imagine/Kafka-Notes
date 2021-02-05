package com.dce.kafka.sample.api.producer;

import com.dce.kafka.constants.Cons;
import com.dce.kafka.sample.interceptor.PrefixProducerInterceptor;

import java.util.Properties;

/**
 * Producer配置
 *
 * @anthor lcl
 */
public class PdcConfig {
    public static Properties initConfig(String serializerKey, String serializerValue) {
        Properties properties = new Properties();
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Cons.HOST_PORT);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, "3");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.CLIENT_ID_CONFIG, "producer-1");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerKey);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerValue);
        // 配置拦截器，如果配置多个拦截器，多个类名拼成一个字符串，类间用逗号分隔
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, PrefixProducerInterceptor.class.getName());
        return properties;
    }
}
