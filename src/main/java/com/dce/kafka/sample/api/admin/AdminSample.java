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
}
