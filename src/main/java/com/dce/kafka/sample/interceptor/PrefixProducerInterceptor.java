package com.dce.kafka.sample.interceptor;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Map;

/**
 * 生产者消息增加前缀拦截器
 *
 * @anthor lcl
 */
public class PrefixProducerInterceptor implements ProducerInterceptor<String, String>{
    @Override
    public ProducerRecord onSend(ProducerRecord<String, String> producerRecord) {
        String prefixValue = "pre-" + producerRecord.value();
        return new ProducerRecord<String, String>(producerRecord.topic(), producerRecord.partition(), producerRecord.timestamp(), producerRecord.key(), prefixValue);
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
