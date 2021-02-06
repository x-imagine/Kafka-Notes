package com.dce.kafka.springboot.producer;

import com.dce.kafka.sample.api.consumer.ConsumerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;

import static com.dce.kafka.constants.Cons.TEST_TOPIC_NAME_MUTI_PARTITION;

@Component
public class KafkaProducer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerOperator.class);
    
    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String s) {
        LOGGER.info("准备发送消息为：{}", s);
        //发送消息
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(TEST_TOPIC_NAME_MUTI_PARTITION, s);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
                LOGGER.info(TEST_TOPIC_NAME_MUTI_PARTITION + " - 生产者 发送消息失败：" + throwable.getMessage());
            }
            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理
                LOGGER.info(TEST_TOPIC_NAME_MUTI_PARTITION + " - 生产者 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
    }
}
