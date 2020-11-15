# Producer
## 客户端开发
### 代码示例
- Producer客户端配置信息
```
    public static Properties initConfig(String serializerKey, String serializerValue) {
        Properties properties = new Properties();
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, Cons.host_port);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.RETRIES_CONFIG, "3");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BATCH_SIZE_CONFIG, "16384");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.LINGER_MS_CONFIG, "1");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, serializerKey);
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, serializerValue);
        return properties;
    }
```
参数说明：   
1. bootstrap.servers：指定生产者连接的broker，可设置一个或多个，设置单个即可连接集群，但建议设置多个，以避免单点宕机风险
2. key.serializer、value.serializer：传递消息的key和value序列化的类，kafka只接收字节数组byte[]，故需要序列化
3. client.id：客户端id，如设定亦可，Producer会自动生成
4. retries：对于可重试的异常，允许的重试次数，默认为0。异常如：网络异常、leader不可用等短时间可恢复的异常场景
5. Producer参数较多，可通过org.apache.kafka.clients.producer.ProducerConfig中的常量来避免书写错误。   
6. 序列化、反序列化的class全名也较易出错，可以将"org.apache.kafka.common.serialization.StringSerializer"替换为

- 生产者发送消息——异步发送（async）
```
    /**
     * producer异步发送
     */
    public static void producerSend() {
        Properties properties = ProducerConfig.initConfig(Cons.STRING_SERIALIZER_KEY, Cons.STRING_SERIALIZER_VALUE);
        Producer<String, String> producer = new KafkaProducer<>(properties);
        // send10条记录
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Cons.TEST_TOPIC_NAME, "key" + i, "record" + i);
            producer.send(producerRecord);
        }
        producer.close();
    }
```
- 消费情况   
消费消息顺序不是0-9，原因是该topic中有4个partition，多partition无法保证顺序。   
![](pic/04Producer/consumer.png)
![](pic/04Producer/partitions.png)

- 生产者发送消息——异步回调发送
```
    /**
     * producer异步发送（优化）
     */
    public static void producerSendGrace() {
        Properties properties = ProducerConfig.initConfig(Cons.STRING_SERIALIZER_KEY, Cons.STRING_SERIALIZER_VALUE);
        Producer<String, String> producer = new KafkaProducer<>(properties);
        // send10条记录
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Cons.TEST_TOPIC_NAME_MUTI_PARTITION, "key" + i, "record" + i);
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e != null) {
                        // 异常的处理逻辑
                    } else {
                        // 异步正常回调处理逻辑
                    }
                }
            });
        }
        producer.close();
    }
```
- 生产者发送消息——异步阻塞发送   
```
    /**
     * producer异步阻塞发送
     */
    public static void producerSyncSend() throws ExecutionException, InterruptedException {
        Properties properties = ProducerConfig.initConfig(Cons.STRING_SERIALIZER_KEY, Cons.STRING_SERIALIZER_VALUE);
        Producer<String, String> producer = new KafkaProducer<>(properties);
        // send10条记录
        for (int i = 0; i < 10; i++) {
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(Cons.TEST_TOPIC_NAME_ONE_PARTITION, "key" + i, "record" + i);
            Future<RecordMetadata> future = producer.send(producerRecord);
            long offset = future.get().offset();
            int partition = future.get().partition();
            LOGGER.info("============================producer异步阻塞发送： partition" + partition + " |offset：" + offset + "============================");
        }
        producer.close();
    }
```
代码说明：
1. KafkaProducer线程安全，可以在多线程中共享KafkaProducer实例
2. producer.send为异步方法，直接调用为异步；但对返回Future.get()，则为异步阻塞式发送
3. send(ProducerRecord, Callback)方法为异步+回调，回调中可记录异常或成功信息及相关处理，其中回调函数中的recordMetadata、exception是互斥的，非异常即正常；同时，回调函数亦能保证有序
3. Producer<String, String>泛型代表消息对象中的key、value类型
4. 异常：Producer中有两类异常：分为可重试异常、不可重试异常。可短暂恢复的为可重试，如NetworkException、LeaderNotAvailableException、UnkownTopicOrPartitionException等。可在Properties对象中设置重试次数，默认该次数为0

### 消息对象——ProducerRecord
对象包含多个属性：
```
public class ProducerRecord<K, V> {
    private final String topic;
    private final Integer partition;
    private final Headers headers;
    private final K key;
    private final V value;
    private final Long timestamp;
...
}
```
- topic：目标主题
- partition：目标分区
- headers：选用，用于设定业务相关信息
- key：消息的附加信息，可控制消息发往的分区，相同key消息会发往同一分区
- value：要发的消息，null的话代表墓碑消息
- timestamp：分为创建时间、追加时间

### 序列化
- 序列化器：生产者一端，将消息对象转化为字节数组
- 反序列化器：消费者一端，将字节数组恢复为消息对象
- 序列化器实现org.apache.kafka.common.serialization.Serializer接口，可以自定义序列化、反序列化类进行序列化处理
- 客户端提供部分基础数据类型的序列化处理类，若不能满足可引入protobuf、protostuff、json等序列化器

### 分区器
消息序列化后，需要发往topic的对应分区，若ProducerRecord中指定了分区，则发往指定分区，否则需要通过分区器选择分区。   
- 分区器实现org.apache.kafka.clients.producer.Partitioner接口，partition方法负责返回分区号，如自定义则重写该方法   
```
int partition(String var1, Object var2, byte[] var3, Object var4, byte[] var5, Cluster var6)
```
- 默认分区器通过key进行消息分区   
1. 当key为null，则消息在当前可用分区中进行轮询投递；
2. 当key非null，则对key进行hash处理，通过hash值在所有分区中选择分区号，key值相同的消息，在分区数未调整的前提下，一定分配至同一分区  
注：轮询方式只在可用分区中轮询，hash方式在所有分区中进行分配

## 详细原理

