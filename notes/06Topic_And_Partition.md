# Topic & Partition
- 分区为kafka提供可伸缩性、水平扩展
- 副本机制为kafka提供高可靠性
- 主题有多个分区，分区有多个副本，副本对应副本日志文件，日志文件对应多个日志分段，日志分段细分为索引文件、存储文件、快照文件
## 主题管理
### 一、主题创建
1.broker参数：auto.create.topics.enable默认为true，当true时：
- 生产者生产一条消息，对应的主题若不存在，则自动创建
- 消费者拉取一条消息，对应的主题若不存在，则自动创建
- 默认创建的分区数量为num.partitions数量（默认1）
- 从易于运维角度考虑，尽量避免自动创建，如自动创建可能因为手误，创建不想创建的主题   
2.主题创建可使用kafka-topicks.sh创建
```
kafka-topics.sh --zookeeper IP:2181/kafka --create --topic topic-name  --partitions 4 --replication-facotr 2
```
- --partitions ：分区数量
- --topic-name ：分区名称
- --replication-factor ：副本因子
- --create ：命令行为为创建，另有describe\list\delete\alter等行为   

3.主题创建后产生的日志文件可以在log.dir（或log.dirs）配置的的目录中查看
- 创建日志文件数量为：分区数 * 副本因子，如上例为 4 * 2 = 8 个分区文件
- 每个副本对应一个日志文件
- 副本默认平均分布在各个boroker中，如上例命令，若broker为3个，则8个文件将以2:3:3的数量比例分布在不同broker中   

4.可通过命令参数--replica-assignment指定不同分区的分配方案   
```
kafka-topics.sh --zookeeper IP:2181/kafka --create --topic topic-name  --partitions 4 --replication-facotr 2
--replica-assignment 2:0,0:1,1:2,2:1
```
- 不同分区用“,”间隔，如上为4个分区指定分配情况
- 每个分区为副本分配的broker用“:”间隔
- 一个分区的副本不允许重复分配给相同broker，如：0:0,1:1,2:2,1:1，将报AdminCommandFailException异常
- 分区之间不允许指定的不同数量的副本，如：2:0,0,2,1，将报AdminOperatorException异常
- 跳过某个分区亦不被允许，如2:0,,,2:1，将报NumberFormatException异常

5.创建某个主题时若需要覆盖某些默认参数，则可通过--config参数来进行参数重写
```
kafka-topics.sh --zookeeper IP:2181/kafka --create --topic topic-name  --partitions 4 --replication-facotr 2
--config max.message.bytes
=20000
```
如上命令中的config将覆盖

