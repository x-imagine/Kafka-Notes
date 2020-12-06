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
-
```
kafka-topics.sh --zookeeper IP:2181/kafka --create --topic topic-name  --partitions 4 --replication-facotr 2
--config max.message.bytes=20000
```
如上命令中的config将覆盖主题参数的max.message.bytes设置，将主题内最大消息大小设置为20000   
通过config命令自定义的参数，通过--describe可以回查

6.主题的命名
- 重复的命名，报TopicExistsException，在命令中增加--if-not-exists，则不报错，且不覆盖主题
- 命名中避免包括“.”，因为kafka后台处理过程会将“.”转化为“_”，这样topic.name和topic_name就会冲突，报InvalidTopicException
- 由于kafka内部主题以“__”开头，所以自定义主题最好避免以双下划线开头，避免误解

7.机架信息设置
- kafka支持在broker节点设置机架信息，指定机架后kafka在分区副本分配时，会尽量让副本不在同一机架的broker上，避免同一机架故障导致多副本失效
- 机架信息在kafka集群中要么全不设置，要么全都设置，仅设置部分broker的机架信息，则会报AdminOperationException
- 可以通过--disable-rack-aware来忽略broker.rack参数