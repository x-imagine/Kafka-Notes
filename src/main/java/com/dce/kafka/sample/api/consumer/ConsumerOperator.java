package com.dce.kafka.sample.api.consumer;

import com.google.common.collect.Lists;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;

import static com.dce.kafka.sample.Cons.TEST_TOPIC_NAME_MUTI_PARTITION;

/**
 * 消费者操作
 *
 * @anthor lcl
 */
public class ConsumerOperator {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConsumerOperator.class);
    public static void main(String[] args) {
        // consumerAutoCommit();
        // consumerAssignPartition();
        // consumerNotAutoCommit();
        // consumerCommitBypartition();
        // consumerCommitBySomePartition();

        getPartitionInfo();
    }

    /**
     * 自动提交（不推荐的方式）
     */
    public static void consumerAutoCommit() {
        // 定义配置信息
        Properties properties = CsmConfig.initConfig(StringDeserializer.class.getName(), StringDeserializer.class.getName());
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        // 订阅主题，可多个
        kafkaConsumer.subscribe(Arrays.asList(TEST_TOPIC_NAME_MUTI_PARTITION));
        while (true) {
            ConsumerRecords<String, String> pollRecords = kafkaConsumer.poll(Duration.ofMillis(10000));
            for (ConsumerRecord<String, String> pollRecord : pollRecords) {
                LOGGER.info("---------- partition" + pollRecord.partition() + ",offset " + pollRecord.offset() + ",key " + pollRecord.key() + ",value " + pollRecord.value());
            }
        }
    }

    /**
     * 指定分区的消费
     */
    public static void consumerAssignPartition() {
        TopicPartition topicPartition1 = new TopicPartition(TEST_TOPIC_NAME_MUTI_PARTITION, 0);
        TopicPartition topicPartition2 = new TopicPartition(TEST_TOPIC_NAME_MUTI_PARTITION, 1);
        List<TopicPartition> topicPartitionArrayList = Lists.newArrayList(topicPartition1, topicPartition2);
        // 定义配置信息
        Properties properties = CsmConfig.initConfig(StringDeserializer.class.getName(), StringDeserializer.class.getName());
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        // 订阅主题，可多个
        kafkaConsumer.assign(topicPartitionArrayList);
        while (true) {
            ConsumerRecords<String, String> pollRecords = kafkaConsumer.poll(Duration.ofMillis(10000));
            for (ConsumerRecord<String, String> pollRecord : pollRecords) {
                LOGGER.info("---------- partition" + pollRecord.partition() + ",offset " + pollRecord.offset() + ",key " + pollRecord.key() + ",value " + pollRecord.value());
            }
        }
    }

    /**
     * 代码逻辑提交
     */
    public static void consumerNotAutoCommit() {

        Properties properties = CsmConfig.initConfig(StringDeserializer.class.getName(), StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        kafkaConsumer.subscribe(Arrays.asList(TEST_TOPIC_NAME_MUTI_PARTITION));

        while (true) {
            ConsumerRecords<String, String> pollRecords = kafkaConsumer.poll(Duration.ofMillis(10000));
            for (ConsumerRecord<String, String> pollRecord : pollRecords) {
                LOGGER.info("---------- partition" + pollRecord.partition() + ",offset " + pollRecord.offset() + ",key " + pollRecord.key() + ",value " + pollRecord.value());
            }
            // 可处理为业务处理成功提交，否则不提交，确保下次仍能获得该消息
            kafkaConsumer.commitAsync();
        }
    }

    /**
     * 逐个partition处理并提交（为未来多线程处理partition做准备）
     */
    public static void consumerCommitBypartition() {

        Properties properties = CsmConfig.initConfig(StringDeserializer.class.getName(), StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        kafkaConsumer.subscribe(Arrays.asList(TEST_TOPIC_NAME_MUTI_PARTITION));

        while (true) {
            ConsumerRecords<String, String> pollRecords = kafkaConsumer.poll(Duration.ofMillis(10000));
            Set<TopicPartition> partitions = pollRecords.partitions();
            for (TopicPartition partition : partitions) {
                List<ConsumerRecord<String, String>> partitionRecords = pollRecords.records(partition);
                for (ConsumerRecord<String, String> pollRecord : partitionRecords) {
                    LOGGER.info("---------- partition" + pollRecord.partition() + ",offset " + pollRecord.offset() + ",key " + pollRecord.key() + ",value " + pollRecord.value());
                }
                Map<TopicPartition, OffsetAndMetadata> offsetMap = new HashMap<>();
                long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(lastOffset + 1);
                offsetMap.put(partition, offsetAndMetadata);
                // 可处理为业务处理成功提交，否则不提交，确保下次仍能获得该消息
                kafkaConsumer.commitAsync(offsetMap, null);
            }
        }
    }

    /**
     * 指定订阅某些个partition（模拟多线程的某个线程处理部分partition）
     */
    public static void consumerCommitBySomePartition() {

        Properties properties = CsmConfig.initConfig(StringDeserializer.class.getName(), StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        TopicPartition topicPartition0 = new TopicPartition(TEST_TOPIC_NAME_MUTI_PARTITION, 0);
        TopicPartition topicPartition1 = new TopicPartition(TEST_TOPIC_NAME_MUTI_PARTITION, 1);
        kafkaConsumer.assign(Arrays.asList(topicPartition0, topicPartition1));
        // kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true) {
            ConsumerRecords<String, String> pollRecords = kafkaConsumer.poll(Duration.ofMillis(10000));
            Set<TopicPartition> partitions = pollRecords.partitions();
            for (TopicPartition partition : partitions) {
                List<ConsumerRecord<String, String>> partitionRecords = pollRecords.records(partition);
                for (ConsumerRecord<String, String> pollRecord : partitionRecords) {
                    LOGGER.info("---------- partition" + pollRecord.partition() + ",offset " + pollRecord.offset() + ",key " + pollRecord.key() + ",value " + pollRecord.value());
                }
                Map<TopicPartition, OffsetAndMetadata> offsetMap = new HashMap<>();
                long lastOffset = partitionRecords.get(partitionRecords.size() - 1).offset();
                OffsetAndMetadata offsetAndMetadata = new OffsetAndMetadata(lastOffset + 1);
                offsetMap.put(partition, offsetAndMetadata);
                // 可处理为业务处理成功提交，否则不提交，确保下次仍能获得该消息
                kafkaConsumer.commitAsync(offsetMap, null);
            }
        }
    }

    /**
     * consumer客户端获取主题的分区信息
     */
    public static void getPartitionInfo() {
        Properties properties = CsmConfig.initConfig(StringDeserializer.class.getName(), StringDeserializer.class.getName());
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        List<PartitionInfo> partitionInfoList = kafkaConsumer.partitionsFor(TEST_TOPIC_NAME_MUTI_PARTITION);
        for (PartitionInfo partitionInfo : partitionInfoList) {
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            LOGGER.info(topicPartition.toString());
        }
    }
}