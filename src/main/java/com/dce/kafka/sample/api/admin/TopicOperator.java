package com.dce.kafka.sample.api.admin;

import org.apache.kafka.clients.admin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * topic operator
 *
 * @anthor lcl
 */
public class TopicOperator {
    private final static Logger LOGGER = LoggerFactory.getLogger(TopicOperator.class);

    /**
     * 创建topic，打印topic信息
     */
    public static void createTopic(String topicName) {
        AdminClient adminClient = AdminClientFactory.getAdminClient();
        short partitionsNum = 1;
        short refactor = 1;
        NewTopic newTopic = new NewTopic(topicName, partitionsNum, refactor);
        CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(newTopic));
        LOGGER.info("--------------   topics   -------------------- :" + topics);
    }

    /**
     * 获取topic列表
     */
    public static void topicList() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClientFactory.getAdminClient();
        // 包含一个KafkaFuture，用于异步发送请求之后等待操作结果，支持链式调用以及其他异步编程模型
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        LOGGER.info("--------------   topic size   -------------------- :" + listTopicsResult.names().get().size());
        listTopicsResult.names().get().stream().forEach(s -> {
            LOGGER.info("--------------   topic name   -------------------- :" + s);
        });

        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        // 是否需要内部Topic
        listTopicsOptions.listInternal(true);
        // 包含timeoutMs这个成员变量，用来设定请求的超时时间
        listTopicsResult = adminClient.listTopics(listTopicsOptions);
        listTopicsResult.listings().get().stream().forEach(topic -> {
            LOGGER.info("--------------   topic list   -------------------- " + topic);
        });
    }

    /**
     * 删除topic
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void delTopic(String topicName) throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClientFactory.getAdminClient();
        adminClient.deleteTopics(Arrays.asList(topicName));
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        LOGGER.info("--------------   topic size   -------------------- :" + listTopicsResult.names().get().size());
        listTopicsResult.names().get().stream().forEach(s -> {
            LOGGER.info("--------------   topic name   -------------------- :" + s);
        });
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String topicName = "book-topic";
        // TopicOperator.createTopic(topicName);
        TopicOperator.topicList();
        // TopicOperator.delTopic(topicName);
    }
}
