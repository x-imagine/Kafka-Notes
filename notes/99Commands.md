# Commands
Kafka安装目录中bin下的脚本执行。
## 一、Topic
kafka_topics.sh 参数说明：
- --bootstrap-server 脚本连接的Kafka服务器地址
- --zookeeper 作用同bootstrap-server，对早期版本的支持（0.9以前，通过zookeeper）
- --replication-factor 副本因子
- --partitions 分区个数
- --create 创建topic的动作指令
- --describe 展示topic的动作指令
### 1.创建topic
```
kafka-topics.sh --create --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/create-topic.png)
### 2.查看某topic
```
kafka-topics.sh --describe --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/desc-topic.png)
### 3.查看topic list
```
kafka-topics.sh --list --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/listTopic.png)
### 4.删除topic
```
kafka-topics.sh --delete --topic topic-1,topic-2 --bootstrap-server 192.168.137.88:9092
```
### 5.修改分区
```
kafka-topics.sh --alter --partitions 4 --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/increase-partition.png)
### 6.指定分区及副本分配方案创建topic 
```
kafka-topics.sh --bootstrap-server 192.168.137.88:9092 --create --topic topic-name --replica-assignment 2:0,0:1,1:2,2:1
```
### 7.覆盖默认参数创建topic
```
kafka-topics.sh --bootstrap-server 192.168.137.88:9092 --create --topic topic-name  --partitions 4 --replication-factor 2
--config max.message.bytes=20000
```
### 8.查看覆盖配置的topic
```
kafka-topics.sh --describe --bootstrap-server 192.168.137.88:9092 --topics-with-overrides
```
### 9.修改topic配置
```
只修改分区
kafka-topics.sh --bootstrap-server 192.168.137.88:9092 --alter --topic topic-config --partitions 4
修改分区和属性 
kafka-topics.sh --zookeeper 192.168.137.88:2181 --alter --topic topic-config --partitions 6 --config segment.bytes=100000000
```
### 10.删除topic配置，恢复默认配置
```
kafka-topics.sh --zookeeper 192.168.137.88:2181 --alter --topic topic-config --delete-config segment.bytes
```

### 11.手动分区平衡处理
较早版本kafka：   
- 全部主题重新分区平衡
```
kafka-preferred-replica-election.sh --zookeeper 192.168.137.88:2181
```
注：脚本对全部主题重新分区平衡，成本较高；如果主题和分区过多，信息也可能占满zookeeper中的/admin/preferred-replica-election节点（默认1M），导致失败
- 指定主题重新分区平衡
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
2.6.0版本kafka:
- 全部主题重新分区平衡
```
kafka-leader-election.sh --bootstrap-server 192.168.137.88:9092 --all-topic-partitions --election-type PREFERRED
```
- 指定主题重新分区平衡
```
kafka-leader-election.sh --bootstrap-server 192.168.137.88:9092 --election-type PREFERRED --path-to-json-file election-rule.json
```

### 12.分区重分配处理
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

### 复制限流 之 kafka-config.sh
```
kafka-configs.sh --bootstrap-server 192.168.137.88:9092 --entity-type brokers --entity-name 1 --alter --add-config --follower.replication.thrott
led.rate=1024,leader.replication.throttled.rate=1024
```
![](pic/07Partitions/config-throttle-broker.png) 
关键参数：
- --entity-type：指定修改类型为brokers
- --entity-name：提供一个int型的broker id
- --alter：需要修改broker配置
- --add-config：修改类型为增加配置项
- --follower.replication.throttled.rate=1024,leader.replication.throttled.rate=1024 增加内容及参数值

### 取消复制限流
```
kafka-configs.sh --bootstrap-server 192.168.137.88:9092 --entity-type brokers --entity-name 1 --alter --delete-config --follower.replication.throttled.rate,leader.replication.throttled.rate
```

## 二、Producer
Producer参数说明：
- --bootstrap-server 目标Kafka服务
- --broker-list 同--bootstrap-server，均可表示目标Kafka服务
- --topic 目标topic
### 1.生产数据
```
kafka-console-producer.sh --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/producerByTopic.png)

## 三、Consumer
### 1.指定topic消费
```
kafka-console-consumer.sh --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/consumerByTopic.png)

### 2.指定partition消费
```
kafka-console-consumer.sh --topic topic-a --partition 0 --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/consumerByPartition.png)
