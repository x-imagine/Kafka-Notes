package com.dce.kafka.sample.api.producer;

import com.dce.kafka.constants.Cons;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ProducerOperator {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProducerOperator.class);

    /**
     * producer异步发送
     */
    public static void producerSend() {
        Properties properties = PdcConfig.initConfig(Cons.STRING_SERIALIZER_KEY, Cons.STRING_SERIALIZER_VALUE);
        Producer<String, String> producer = new KafkaProducer<>(properties);
        // send10条记录
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Cons.TEST_TOPIC_NAME_MUTI_PARTITION, "key" + i, "record" + i);
            producer.send(producerRecord);
        }
        producer.close();
    }

    /**
     * producer异步发送（优化）
     */
    public static void producerSendGrace() {
        Properties properties = PdcConfig.initConfig(Cons.STRING_SERIALIZER_KEY, Cons.STRING_SERIALIZER_VALUE);
        Producer<String, String> producer = new KafkaProducer<>(properties);
        // send10条记录
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Cons.TEST_TOPIC_NAME_MUTI_PARTITION, "key" + i, "record" + i);
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {
                        // 异常的处理逻辑
                    } else {
                        // 异步正常回调处理逻辑
                    }
                }
            });
        }
        producer.close();
    }

    /**
     * producer异步阻塞发送
     */
    public static void producerSyncSend() throws ExecutionException, InterruptedException {
        Properties properties = PdcConfig.initConfig(Cons.STRING_SERIALIZER_KEY, Cons.STRING_SERIALIZER_VALUE);
        Producer<String, String> producer = new KafkaProducer<>(properties);
        // send10条记录
        for (int i = 0; i < 1000; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Cons.TEST_TOPIC_NAME_MUTI_PARTITION, "key" + i, "record" + i);
            Future<RecordMetadata> future = producer.send(producerRecord);
            long offset = future.get().offset();
            int partition = future.get().partition();
            LOGGER.info("============================producer异步阻塞发送： partition" + partition + " |offset：" + offset + "============================");
        }
        producer.close();
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // producerSend();
        producerSyncSend();
        // ConsumerOperator.isRunning.set(false);
    }
}
