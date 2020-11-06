# Commands

## Topic
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
