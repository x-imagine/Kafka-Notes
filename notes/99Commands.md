# Commands
Kafka安装目录中bin下的脚本执行。
## Topic
kafka_topics.sh 参数说明：
- --bootstrap-server 脚本连接的Kafka服务器地址
- --zookeeper 作用同bootstrap-server，对早期版本的支持（0.9以前，通过zookeeper）
- --replication-factor 副本因子
- --partitions 分区个数
- --create 创建topic的动作指令
- --describe 展示topic的动作指令
### 创建topic
```
kafka-topics.sh --create --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/create-topic.png)

### 查看某topic
```
kafka-topics.sh --describe --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/desc-topic.png)
### 查看topic list
```
kafka-topics.sh --list --zookeeper 192.168.137.88:2181
```
![](pic/99Commands/listTopic.png)

### 创建分区
```
kafka-topics.sh --alter --partitions 4 --topic topic-a --zookeeper 192.168.137.88:2181 
```
![](pic/99Commands/increase-partition.png)

## Producer
Producer参数说明：
- --bootstrap-server 目标Kafka服务
- --broker-list 同--bootstrap-server，均可表示目标Kafka服务
- --topic 目标topic
### 生产数据
```
kafka-console-producer.sh --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/producerByTopic.png)

## Consumer
### 指定topic消费
```
kafka-console-consumer.sh --topic topic-a --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/consumerByTopic.png)

### 指定partition消费
```
kafka-console-consumer.sh --topic topic-a --partition 0 --bootstrap-server 192.168.137.88:9092
```
![](pic/99Commands/consumerByPartition.png)
