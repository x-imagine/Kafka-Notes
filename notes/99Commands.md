# Commands
Kafka安装目录中bin下的脚本执行。
## kafka_topics.sh
 参数说明：
- --alter 修改topic配置关键字，修改分区、副本或其他主题配置
- --bootstrap-server REQUIRED，脚本连接的Kafka服务器地址
- --config 修改并覆盖topic创建之初的参数
- --delete-config 移除覆盖的topic参数
- --create 创建topic的动作指令
- --delete 删除主题
- --describe 展示topic的动作指令
- --replication-factor 副本因子
- --partitions 分区个数
- --zookeeper DEPRECATED，作用同bootstrap-server，对早期版本的支持（0.9以前，通过zookeeper）
### 1.创建topic
- 基础款：采用默认配置创建topic
```
kafka-topics.sh --create --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/create-topic.png)
- 定制款A：指定分区、副本创建topic，覆盖部分默认参数
```
kafka-topics.sh --bootstrap-server 192.168.137.88:9092 --create --topic topic-name  --partitions 4 --replication-factor 2
--config max.message.bytes=20000
```
--partitions：指定分区数量   
--replication-factor：指定副本因子   
--config：指定需要覆盖的参数

- 定制款B：指定分区及副本分配方案创建topic 
```
kafka-topics.sh --bootstrap-server 192.168.137.88:9092 --create --topic topic-name --replica-assignment 2:0,0:1,1:2,2:1
```
此方式隐含了分区数量、副本，故不必显示--partitions、--replication-factor   
2:0,0:1,1:2,2:1代表4个分区、3个副本，其中，不同分区用“,”间隔，如上为4个分区指定分配情况，每个分区为副本分配的broker用“:”间隔

### 2.查看topic
- 指定topic：指定topic名，获取topic详细信息
```
kafka-topics.sh --describe --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/desc-topic.png)
- 获取topic列表
```
kafka-topics.sh --list --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/listTopic.png)
- 查看覆盖配置的topic
```
kafka-topics.sh --describe --bootstrap-server 192.168.137.88:9092 --topics-with-overrides
```
### 3.删除topic
- 通过topic名删除，多个用逗号分隔
```
kafka-topics.sh --delete --topic topic-1,topic-2 --bootstrap-server 192.168.137.88:9092
```
### 4.修改topic
- 修改分区
```
kafka-topics.sh --alter --partitions 4 --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/increase-partition.png)
注：不能改小，只能改大

- 修改topic配置
```
只修改分区
kafka-topics.sh --bootstrap-server 192.168.137.88:9092 --alter --topic topic-config --partitions 4
修改分区和属性 
kafka-topics.sh --zookeeper 192.168.137.88:2181 --alter --topic topic-config --partitions 6 --config segment.bytes=100000000
```
- 删除topic配置，恢复默认配置
```
kafka-topics.sh --zookeeper 192.168.137.88:2181 --alter --topic topic-config --delete-config segment.bytes
```

## kafka-preferred-replica-election.sh
较早版本kafka，如果存在broker节点分区负载较大，且未开启自动平衡参数（auto.leader.rebalance.enable），可手动平衡  
- 全部主题重新分区平衡
```
kafka-preferred-replica-election.sh --zookeeper 192.168.137.88:2181
```
注：脚本对全部主题重新分区平衡，成本较高；如果主题和分区过多，信息也可能占满zookeeper中的/admin/preferred-replica-election节点（默认1M），导致失败

- 指定主题、分区重新分区平衡
```
kafka-preferred-replica-election.sh --zookeeper 192.168.137.88:2181 election-rule.json
```
json样例   
```
{
    "partitions": [
        {
            "topic": "topic-parts",
            "partition": 0
        },
        {
            "topic": "topic-parts",
            "partition": 1
        }
    ]
}
```
## kafka-leader-election.sh
2.6.0版本kafka:
- 全部主题重新分区平衡
```
kafka-leader-election.sh --bootstrap-server 192.168.137.88:9092 --all-topic-partitions --election-type PREFERRED
```
- 指定主题重新分区平衡
```
kafka-leader-election.sh --bootstrap-server 192.168.137.88:9092 --election-type PREFERRED --path-to-json-file election-rule.json
```

## kafka-reassign-partitions.sh
在新增、分区重分配处理
- 定义重分配主题的json文件
```
{
    "topics": [
        {
            "topic": "topic-reassign",
            "partition": 0
        }
    ],  
    "version":1
}

```
- 根据上述json生成分配的配置方案
```
kafka-reassign-partitions.sh --bootstrap-server 192.168.137.88:9092 --generate --topics-to-move-json-file reassign-rule.json --broker-list 0,2
```
- 执行重分配   
将重分配的方案创建json文件
```json
{
    "version": 1, 
    "partitions": [
        {
            "topic": "topic-reassign", 
            "partition": 0, 
            "replicas": [
                2, 
                0
            ], 
            "log_dirs": [
                "any", 
                "any"
            ]
        }, 
        {
            "topic": "topic-reassign", 
            "partition": 1, 
            "replicas": [
                0, 
                2
            ], 
            "log_dirs": [
                "any", 
                "any"
            ]
        }, 
        {
            "topic": "topic-reassign", 
            "partition": 2, 
            "replicas": [
                2, 
                0
            ], 
            "log_dirs": [
                "any", 
                "any"
            ]
        }
    ]
}
```
通过kafka-reassign-partitions.sh的--execute执行方案
```
kafka-reassign-partitions.sh --bootstrap-server 192.168.137.88:9092 --execute --reassignment-json-file reassign-execute.json
```
执行后，broker节点1已不再拥有该主题的分区

## kafka-config.sh
关键参数：
- --entity-type：指定修改类型为brokers
- --entity-name：提供一个int型的broker id
- --alter：需要修改broker配置
- --add-config：修改类型为增加配置项   
不同类型对象可修改的属性，通过--help查询
### 1.增加配置
entity-type topic
```
 kafka-configs.sh --bootstrap-server 192.168.137.88:9092 --alter --entity-type topics --entity-name topic-b --add-config 'max.message.bytes=50000000,flush.ms=100000'
```
修改配置于增加配置一致，直接修改属性值即可
![](pic/99Commands/kafka-configs-topics.png) 

### 2.删除配置
```
 kafka-configs.sh --bootstrap-server 192.168.137.88:9092 --alter --entity-type topics --entity-name topic-b --delete-config 'max.message.bytes,flush.ms'
```
![](pic/99Commands/kafka-configs-topics-delete.png) 

### 3.查看配置
```
 kafka-configs.sh --bootstrap-server 192.168.137.88:9092 --describe --entity-type topics --entity-name topic-b
```
可以获取修改过的配置

### 4.限流   
- 复制限流
```
kafka-configs.sh --bootstrap-server 192.168.137.88:9092 --entity-type brokers --entity-name 1 --alter --add-config --follower.replication.thrott
led.rate=1024,leader.replication.throttled.rate=1024
```
![](pic/07Partitions/config-throttle-broker.png) 

- --follower.replication.throttled.rate=1024,leader.replication.throttled.rate=1024 增加内容及参数值

- 取消复制限流
```
kafka-configs.sh --bootstrap-server 192.168.137.88:9092 --entity-type brokers --entity-name 1 --alter --delete-config --follower.replication.throttled.rate,leader.replication.throttled.rate
```

## kafka-console-producer.sh
Producer参数说明：
- --bootstrap-server 目标Kafka服务
- --broker-list 同--bootstrap-server，均可表示目标Kafka服务
- --topic 目标topic
### 1.无key数据
```
kafka-console-producer.sh --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/producerByTopic.png)

### 2.带key数据
```
kafka-console-producer.sh --topic topic-a --bootstrap-server 192.168.137.88:9092 --property parse.key=true
```
![](pic/99Commands/producerByTopicWithKey.png)
注：key与value之间需要用tab分隔

## kafka-console-consumer.sh
### 1.即时消费
不会追溯历史消息
```
kafka-console-consumer.sh --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/consumerByTopic.png)
### 2.指定起点消费
从头开始消费
```
kafka-console-consumer.sh --bootstrap-server 192.168.137.88:9092 --topic topic-b --from-beginning 
```
![](pic/99Commands/consumer-from-beginning.png)
从指定位移消费
```
kafka-console-consumer.sh --bootstrap-server 192.168.137.88:9092 --topic topic-b --partition 0 --offset 2
```
![](pic/99Commands/consumer-from-offset.png)
注：必须指定分区，否则offset无法确定是哪个分区，同时，offset可以指定为earliest、latest或无符号数

### 3.指定partition消费
```
kafka-console-consumer.sh --topic topic-a --partition 0 --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/consumerByPartition.png)

## kafka-consumer-groups.sh
### 1.查看消费组列表
```
kafka-consumer-groups.sh --bootstrap-server 192.168.137.88:9092 --list
```
![](pic/99Commands/consumer-group-list.png)

### 2.查看消费组详细
```
kafka-consumer-groups.sh --bootstrap-server 192.168.137.88:9092 --describe --group test_consumer_group_id
```
![](pic/99Commands/consumer-groups-describe.png)
- GROUP：组名
- TOPIC：订阅的主题
- PARTITION：对应的分区编号
- CURRENT-OFFSET：当前偏移量
- LOG-END-OFFSET：HW高水位
- LAG：消费滞后的数量，等于LOG-END-OFFSET - CURRENT-OFFSET
- CONSUMER-ID：消费组成员id
- HOST：消费者所在host
- CLIENT-ID：消费者客户端clientId

### 3.查看消费组状态
```
kafka-consumer-groups.sh --bootstrap-server 192.168.137.88:9092 --describe --group test_consumer_group_id --state
```
- stable：有消费者成员
![](pic/99Commands/consumer-groups-state.png)
- empty：无消费者成员
![](pic/99Commands/consumer-groups-state-empty.png)
其他状态：
- prepareingRebalance：准备再平衡
- completeRebalance：完成再平衡
- dead：挂了

### 4.查看消费组成员
```
kafka-consumer-groups.sh --bootstrap-server 192.168.137.88:9092 --describe --group test_consumer_group_id --members --verbose
```
![](pic/99Commands/consumer-group-members.png)

### 5.删除消费组
删除只可处理未在运行的消费组，否则报错
```
kafka-consumer-groups.sh --bootstrap-server 192.168.137.88:9092 --delete --group test_consumer_group_id
```
![](pic/99Commands/consumer-group-delete.png)
