# Consumer
## 客户端开发
### 代码示例
- Producer客户端配置信息
```
    public static Properties initConfig(String deserializerKey, String deserializerValue) {
        Properties properties = new Properties();
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, HOST_PORT);
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, "test_consumer_group_id");
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.CLIENT_ID_CONFIG, "test_consumer_client_id");
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        properties.setProperty(org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, deserializerKey);
        properties.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializerValue);
        return properties;
    }
```
参数说明：   
1. bootstrap.servers：指定生产者连接的broker，可设置一个或多个，设置单个即可连接集群，但建议设置多个，以避免单点宕机风险
2. key.deserializer、value.deserializer：传递消息的key和value反序列化的类，需与producer中的序列化类对应
3. client.id：客户端id，如设定亦可，cosumer会自动生成，如consumer-1
4. group.id：consumer隶属消息组名称，默认""，设置为null会抛异常

- 消费者消费消息——所有分区
```
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
```
代码消费了topic的所有分区的消息   
![](pic/05Consumer/producer_send.png)
![](pic/05Consumer/consumer_all_partitions.png)
代码说明：   
1. kafkaConsumer.subscribe()消息订阅可以订阅多个topic，采用直接罗列或正则表达式
2. 多次执行subscribe()，仅最后一次生效，而非对多个主题生效

- 消费者消费消息——指定分区
```
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
```
代码消费了topic的指定分区的消息   
![](pic/05Consumer/consumer_assign_partitions.png)
代码说明：
1. TopicPartition类用来创建Topic + Partition的实例，该类只有两个属性，用于指定topic下的分区
2. kafkaConsumer.assign()方法不仅指定主题，还指定分区

- Consumer获取主题中的分区信息
```
    public static void getPartitionInfo() {
        Properties properties = CsmConfig.initConfig(StringDeserializer.class.getName(), StringDeserializer.class.getName());
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        List<PartitionInfo> partitionInfoList = kafkaConsumer.partitionsFor(TEST_TOPIC_NAME_MUTI_PARTITION);
        for (PartitionInfo partitionInfo : partitionInfoList) {
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            LOGGER.info(topicPartition.toString());
        }
    }
```
![](pic/05Consumer/getPartitionInfo.png)   
除主题分区信息外，PartitionInfo以下副本信息可以进行精细化的处理   
```
    // leader副本所在节点
    private final Node leader;
    // AR
    private final Node[] replicas;
    // ISR
    private final Node[] inSyncReplicas;
    // OSR
    private final Node[] offlineReplicas;
```
- 消费者退订消息
