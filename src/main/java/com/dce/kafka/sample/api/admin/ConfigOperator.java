package com.dce.kafka.sample.api.admin;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.ConfigResource;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 配置修改
 *
 * @anthor lcl
 */
public class ConfigOperator {
    private static String TOPIC_NAME = "config-test-topic";
    /**
     * 获取配置信息（用于kafka监控）
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void descConfig() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClientFactory.getAdminClient();

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
        DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(Arrays.asList(configResource));
        Map<ConfigResource, Config> configResourceConfigMap = describeConfigsResult.all().get();
        configResourceConfigMap.entrySet().stream().forEach((entry) -> {
            System.out.println("-------- config key ---------:" + entry.getKey());
            System.out.println("--------config value---------:" + entry.getValue());
        });
    }

    /**
     * 修改配置
     */
    public static void alterConfig() {
        AdminClient adminClient = AdminClientFactory.getAdminClient();

        ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, TOPIC_NAME);
        AlterConfigOp alterConfigOp = new AlterConfigOp(new ConfigEntry("preallocate", "false"), AlterConfigOp.OpType.SET);
        Collection<AlterConfigOp> opCollection = Arrays.asList(alterConfigOp);

        Map<ConfigResource, Collection<AlterConfigOp>> configResourceConfigMap = new HashMap<>();
        configResourceConfigMap.put(configResource, opCollection);
        adminClient.incrementalAlterConfigs(configResourceConfigMap);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TopicOperator.createTopic(TOPIC_NAME);
        ConfigOperator.descConfig();
        ConfigOperator.alterConfig();
        ConfigOperator.descConfig();
    }
}
