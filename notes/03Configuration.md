[toc]
# Configuration
配置文件： server.properties
## Broker Configs
### broker.id
重要，集群内不可重复的int值，未设置则生成一个不重复的id   
![](pic/03Configuration/brokerid.png)

### log.dirs
必设参数，日志目录，默认tmp下   
![](pic/03Configuration/log.dirs.png)

### zookeeper.connect
必设参数，kafka运行依赖zookeeper，多个zookeeper用逗号连接   
![](pic/03Configuration/zookeeper.png)

### advertised.host.name
废弃，仅用于advertised.listeners，listeners未设置情况的替代参数

### advertised.listeners
重要，listeners是kafka真正bind的地址，advertised.listeners是暴露给外部的地址，未设置，默认采用listener配置

### advertised.port
废弃，仅用于advertised.listeners，listeners未设置情况的替代参数

### auto.create.topics.enable
重要，默认true，当生产者生产消息时，若topic不存在是否创建，默认为true   
![](pic/03Configuration/auto.create.topics.enable.png)

### background.threads
重要，用于各种后台处理的线程数量，int，大于1

### compression.type
重要，压缩类型，支持标准的压缩方式如：'gzip', 'snappy', 'lz4', 'zstd'，可设置为不压缩：uncompressed。默认为producer为自带的原始压缩方式

### delete.topic.enable
重要，默认true，如不允许通过admin工具删除topic，将其设置为false

### host.name
重要，已作废，Broker主机IP地址或域名，当listeners未设置是用于替代之用

### auto.leader.rebalance.enable
重要，是否允许leader rebalance，默认true。若允许kafka controller开启一个后台进程   
在leader.imbalance.check.interval.seconds所设置的时间频度内，在失衡百分比在leader.imbalance.per.broker.percentage所设置的比例以上时
进行leader重选举

### leader.imbalance.check.interval.seconds
重要，进行rebalance检查处理的时间频度参数，long，默认300

### leader.imbalance.per.broker.percentage
重要，允许broker的leader失衡的比例，默认10（%）





### port
已作废，Broker端口





### listeners
重要，Broker监听，Broker间，Client与Broker间通信时，建立连接的关键信息，设置多个用逗号分隔。   
格式：protocol://host:port,protocol2://host2:port2
```
listeners=PLAINTEXT://192.168.137.88:9092
```

### listener.security.protocol.map
重要，以Key:Value的形式定义监听者的安全协议   
```
listener.security.protocol.map=PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL 
```


### inter.broker.listener.name
中等。broker之间内部通信使用的监听，其值设置为监听安全策略值即可，未设置则取security.inter.broker.protocol值，故二者不能同时配置

### security.inter.broker.protocol
中等。broker之间内部通信使用的安全策略，默认为PLAINTEXT，不与inter.broker.listener.name同时设置

### 中低等级待整理










### num.network.threads
重要，Broker在网络间接收\发送消息的线程数，int，默认3

### num.io.threads
重要，Broker在进行磁盘IO处理的线程数，int，默认8



