# Partition
- 分区使用副本机制，提升其可靠性
- 任意多副本只有一个leader，其余副本为follower，leader负责对外发收，follower只负责同步leader消息
- broker节点中的leader数量，体现了该节点的负载，leader越多，负载越大
- 同一个broker节点，最多只有一个某分区的副本
- 主题创建之初，kafka控制器默认会将副本的leader节点均匀的分配到各个broker节点，使负载处于相对均衡状态
- 当某个分区的leader节点发生故障，将从follower节点中选举出新的leader节点，原有leader节点恢复工作后，并不会恢复leader节点身份
- 优先副本：优先副本指在创建主题后，副本列表中首个节点号（红色列）
![](pic/07Partitions/prefer-replica.png) 
## 一、优先副本
问题场景：根据副本机制特点，当由于宕机或网络等原因导致leader节点聚集在某个broker时，会造成该节点负载过大   
![](pic/07Partitions/prefer-replica-2.png) 
上图为停掉某一broker节点后，leader副本转移情况，即便重启节点，原leader副本不会自动还原
### 1.自动分区平衡机制
kafka提供分区自动平衡功能，auto.leader.rebalance.enable（默认true）参数为该功能开关，功能打开时，kafka控制器将进行优先副本的重新分区平衡处理   
处理逻辑：   
- kafka控制器启动定时任务，每个leader.imbalance.check.interval.seconds（默认300s），检查每个broker节点的分区不平衡率   
```
分区不平衡率 = 非优先副本个数 / 分区总数
```
- 当不平衡率大于参数leader.imbalance.per.broker.percentage（默认10%），触发基于优先副本机制的重选举处理，以重建分区平衡
注：生产环境中，自动平衡可能导致阻塞，导致性能问题，故建议将参数设置为false，在必要时，手动进行leader的分区平衡处理

### 2. 手动分区平衡处理（所有主题）
- kafka-preferred-replica-election.sh提供了手动平衡功能，平衡过程是安全的
```
 kafka-preferred-replica-election.sh --zookeeper 192.168.137.88:2181
```
直接执行脚本，脚本将对所有的主题进行分区平衡处理，并将分区方案打印
![](pic/07Partitions/kafka-preferred-replica-election.png) 
注：This tool is deprecated. Please use kafka-leader-election tool.
- 问题：对全部主题重新分区平衡，成本较高；如果主题和分区过多，信息也可能占满zookeeper中的/admin/preferred-replica-election节点（默认1M），导致失败

### 3.手动分区平衡处理（指定分区副本方案）
kafka-preferred-replica-election.sh追加path-to-json-file + json文件路径，可实现对自定义分区副本进行平衡处理
- json文件
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
- 执行
```
 kafka-preferred-replica-election.sh --zookeeper 192.168.137.88:2181 election-rule.json
```
![](pic/07Partitions/prefer-by-json-file.png) 
