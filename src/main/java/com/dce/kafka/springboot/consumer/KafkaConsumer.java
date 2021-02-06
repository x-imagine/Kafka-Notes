package com.dce.kafka.springboot.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(id= "test",topics = "topic-b")
    public void listen1(String message) {
        LOGGER.info("message content [{}]", message);
    }
}
