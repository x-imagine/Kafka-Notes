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
