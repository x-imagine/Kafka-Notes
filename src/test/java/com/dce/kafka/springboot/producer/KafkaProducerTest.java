package com.dce.kafka.springboot.producer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaProducerTest {

    @Resource
    KafkaProducer kafkaProducer;

    @Test
    public void sendTest() throws InterruptedException {
        kafkaProducer.send("hello");
        Thread.sleep(10000);
    }
}