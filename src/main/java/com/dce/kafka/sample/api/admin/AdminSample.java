package com.dce.kafka.sample.api.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.dce.kafka.sample.Cons.host_port;

/**
 * The Admin API to manage and inspect topics, brokers, and other Kafka objects
 */
public class AdminSample {
    private static String TOPIC_NAME = "test";

    /**
     * 获取adminClient
     *
     * @return AdminClient
     */
    public static AdminClient adminClient() {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, host_port);
        AdminClient adminClient = AdminClient.create(properties);
        return adminClient;
    }

    /**
     * 创建topic，打印topic信息
     */
    public static void createTopic(String topicName) {
        AdminClient adminClient = adminClient();
        short partitionsNum = 1;
        short refactor = 1;
        NewTopic newTopic = new NewTopic(topicName, partitionsNum, refactor);
        CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(newTopic));
        System.out.println("--------------   topics   -------------------- :" + topics);
    }

    /**
     * 获取topic列表
     */
    public static void topicList() throws ExecutionException, InterruptedException {
        AdminClient adminClient = adminClient();
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        System.out.println("--------------   topic size   -------------------- :" + listTopicsResult.names().get().size());
        listTopicsResult.names().get().stream().forEach(s -> {
            System.out.println("--------------   topic name   -------------------- :" + s);
        });

        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);
        listTopicsResult = adminClient.listTopics(listTopicsOptions);
        listTopicsResult.listings().get().stream().forEach(topic -> {
            System.out.println("--------------   topic list   -------------------- " + topic);
        });
    }

    /**
     * 删除topic
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void delTopic(String topicName) throws ExecutionException, InterruptedException {
        AdminClient adminClient = adminClient();
        adminClient.deleteTopics(Arrays.asList(topicName));
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        System.out.println("--------------   topic size   -------------------- :" + listTopicsResult.names().get().size());
        listTopicsResult.names().get().stream().forEach(s -> {
            System.out.println("--------------   topic name   -------------------- :" + s);
        });
    }

    /**
     * 获取配置信息（用于kafka监控）
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void descConfig() throws ExecutionException, InterruptedException {
        AdminClient adminClient = adminClient();

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource));
        Map<ConfigResource, Config> configResourceConfigMap = describeConfigsResult.all().get();
        configResourceConfigMap.entrySet().stream().forEach((entry) -> {
            System.out.println("--------config key---------:" + entry.getKey());
            System.out.println("--------config value---------:" + entry.getValue());
        });
    }

    /**
     * 修改配置
     */
    public static void alterConfig() {
        AdminClient adminClient = adminClient();

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
        AlterConfigOp alterConfigOp = new AlterConfigOp(new ConfigEntry("preallocate", "true"), AlterConfigOp.OpType.SET);
        Collection<AlterConfigOp> opCollection = Arrays.asList(alterConfigOp);

        Map<ConfigResource, Collection<AlterConfigOp>> configResourceConfigMap = new HashMap<>();
        configResourceConfigMap.put(configResource, opCollection);
        adminClient.incrementalAlterConfigs(configResourceConfigMap);
    }

    /**
     * 管理partition
     */
    public static void incrPartitions() {
        AdminClient adminClient = adminClient();
        Map<String, NewPartitions> partitionsMap = new HashMap<>();
        NewPartitions newPartitions = NewPartitions.increaseTo(3);
        partitionsMap.put(TOPIC_NAME, newPartitions);

        final CreatePartitionsResult partitionsResult = adminClient.createPartitions(partitionsMap);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        createTopic("test-topic");
//        topicList();
        // incrPartitions();
         delTopic("test-topic");
        // descConfig();
        // alterConfig();
    }
}
