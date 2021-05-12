package com.dce.kafka.sample.api.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreatePartitionsResult;
import org.apache.kafka.clients.admin.NewPartitions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * partition 操作
 *
 * @anthor lcl
 */
public class PartitionOperator {

    private static String TOPIC_NAME = "partition-test-topic";

    /**
     * 管理partition
     */
    public static void incrPartitions(int partitionTotalCount) {
        AdminClient adminClient = AdminClientFactory.getAdminClient();
        Map<String, NewPartitions> partitionsMap = new HashMap<>();
        NewPartitions newPartitions = NewPartitions.increaseTo(partitionTotalCount);
        partitionsMap.put(TOPIC_NAME, newPartitions);

        adminClient.createPartitions(partitionsMap);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TopicOperator.createTopic(TOPIC_NAME);
        PartitionOperator.incrPartitions(3);
        TopicOperator.topicList();
    }
}
