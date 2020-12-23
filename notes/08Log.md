# 日志
主题有多个分区，分区有多个副本，副本对应副本日志文件，日志文件对应多个日志分段，日志分段细分为索引文件、存储文件、快照文件
## 一、文件结构
- 不考虑副本，则一个分区拥有一个Log文件夹，文件夹中包括多个LogSegment，每个LogSegment对应磁盘上的一个日志文件两个索引文件
- Log文件夹命名类似topic-name + partition，如topic-test-0、topic-test-1
- 当向Log中追加消息时，由于LogSegment是顺序性的存储，消息会追加到当前的activeSegment中，当该activeSegment满足一定条件，则创建新LogSegment，继续接受消息
- 两个索引文件分别为：offset索引文件（.index后缀）、timestamp索引文件（.timeIndex后缀）
- 每个 LogSegment 有一个baseOffset，标识当前分段第一条消息的offset
- LogSegment命名采用baseOffset，长度为20位数字，后缀为.log，如baseOffset为0，则日志、索引文件命名为
```
00000000000000000000.log
00000000000000000000.index
00000000000000000000.timeindex
```
- LogSegment还可能存在 .deleted .cleaned .swap临时文件，以及 .snapshot .txnindex等
- 创建主题时，如果log目录指定了多个，那么log文件会在分区最少的那个目录创建本次任务的文件

## 二、日志格式
日志结构的设计
- 冗余字段，会使partition消息增大，进而存储的开销变大、网络传输开销变大，Kafka性能下降
- 缺少字段，在最初的Kafka消息版本中没有timestamp字段，对内影响了日志保存、切分策略，对外影响消息审计、端到端延迟等功能的扩展，虽然可以在消息体内部添加一个时间戳，但是解析变长的消息体会带来额外的开销，而存储在消息体（参考下图中的value字段）前面可以通过指针偏量获取其值而容易解析，进而减少了开销
早期版本——v0   
![](pic/08Log/v0.png) 
其中，RECORD为消息格式，每个消息都对应一组offset和message size，即日志头LOG_OVERHEAD，RECORD+LOG_OVERHEAD构成一条消息，多条消息构成消息集合（Message Set）   
- crc32（4B）：crc32校验值。校验范围为magic至value之间
- magic（1B）：消息格式版本号，v0版本的magic值为0
- attributes（1B）：消息的属性。总共占1个字节，低3位表示压缩类型：0表示NONE、1表示GZIP、2表示SNAPPY、3表示LZ4（LZ4自Kafka 0.9.x引入），其余位保留
- key length（4B）：表示消息的key的长度。如果为-1，则表示没有设置key，即key=null
- key：可选，如果没有key则无此字段
- value length（4B）：实际消息体的长度。如果为-1，则表示消息为空
- value：消息体。可以为空
早期版本——v1   
![](pic/08Log/v1.png) 
- magic（1B）：消息格式版本号，v0版本的magic值为1
- RECORD新增了一个timestamp
- attributes字段中的低3位和v0版本的一样，还是表示压缩类型，第4个bit：0表示timestamp类型为CreateTime，而1表示timestamp类型为LogAppendTime

升级版本——v2   
此版在数据格式上改动较大，引入了变长整型Varints和ZigZag编码
- Varints：是使用一个或多个字节来序列化整数的一种方法，数值越小，其所占用的字节数就越少，但Varints并非一直会省空间，长位数反而会高于传统int
- ZigZag编码：一种锯齿形的方式来回标识正负整数，使带符号整数映射为无符号整数，绝对值较小的负数仍有较小的Varints编码值，比如-1编码为1,1编码为2，-2编码为3
- 消息集改称为Record Batch，替代Message Set
- 消息格式Record的关键字段大量采用了Varints，这样可根据具体值来确定需要几个字节
![](pic/08Log/v2.png) 
结构说明    
- first offset：表示当前RecordBatch的起始位移
- length：计算partition leader epoch到headers之间的长度
- partition leader epoch：用来确保数据可靠性，略
- magic：消息格式的版本号，对于v2版本而言，magic等于2
- attributes：消息属性，注意这里占用了两个字节。低3位表示压缩格式，可以参考v0和v1；第4位表示时间戳类型；第5位表示此RecordBatch是否处于事务中，0表示非事务，1表示事务。第6位表示是否是Control消息，0表示非Control消息，而1表示是Control消息，Control消息用来支持事务功能
- last offset delta：RecordBatch中最后一个Record的offset与first offset的差值。主要被broker用来确认RecordBatch中Records的组装正确性
- first timestamp：RecordBatch中第一条Record的时间戳
- max timestamp：RecordBatch中最大的时间戳，一般情况下是指最后一个Record的时间戳，和last offset delta的作用一样，用来确保消息组装的正确性
- producer id：用来支持幂等性，略
- producer epoch：和producer id一样，用来支持幂等性，略
- first sequence：和producer id、producer epoch一样，用来支持幂等性，略
- records count：RecordBatch中Record的个数

## 三、消息压缩
压缩率：把100MB的文件压缩后是90MB，压缩率为90/100*100%=90%，压缩空间越大越好，压缩率越小越好   
通常对较大的文件，压缩效果会更好，kafka消息通常比较小而数量多，所以kafka在压缩时非逐条压缩，而是将消息集进行压缩
![](pic/08Log/compress_message1.png) 
将左侧原始消息的多条，封装为一个大消息的value中，原始消息成为内部消息，消息集合成为外层消息，kafka压缩外层消息
![](pic/08Log/compress_message2.png)
内外层消息的Offset为内层消息最大值，内层从0开始offset，如外层1030对应内层的offset=5
