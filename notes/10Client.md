# Client
## 一、客户端分区分配
消费者客户端通过partition.assignment.strategy设置消费者与主题之间分区分配策略   
策略包括：   
- org.apache.kafka.clients.consumer.RangeAssignor
- org.apache.kafka.clients.consumer.RoundRobinAssignor
- org.apache.kafka.clients.consumer.StickyAssignor   
可以设置一个或多个策略
### 1.RangeAssignor
RangeAssignor策略原理是按照分区数和消费者数进行整除运算获得一个跨度，按照跨度进行平均分配。   
简单理解就是对某主题的分区尽可能平分，如：   
例1：   
主题A下的分区数：10，p0-p9   
客户端消费者数：2，c0-c1   
则分配效果是：   
c0：p0 p1 p2 p3 p4   
c1：p5 p6 p7 p8 p9   
例2：   
主题A下的分区数：7，p0-p6     
客户端消费者数：3，c0-c2    
则分配效果是：   
c0：p0 p1 p2   
c1：p3 p4    
c2：p5 p6 

缺点：由于分配顺序是按照消费者名称的字典顺序分配，如果多个主题分区都不能被整除，那么字典顺序靠前的几个消费者，负载将更高，甚至过载。   
如：
上例2中的情况，如果存在相同的主题B、C、D，分区数均与A一致，则分配结果为
c0：A的p0 p1 p2，B的p0 p1 p2，C的p0 p1 p2，共9个分区      
c1：A的p3 p4，B的p3 p4，C的p3 p4，共6个分区  
c2：A的p5 p6，B的p5 p6，C的p5 p6，共6个分区  

### 2.RoundRobinAssignor
RoundRobinAssignor是对消费组下的所有消费者及订阅的所有主题分区按照自定拍下，然后轮训方式将分区依次分给消费者   
例1：   
主题A下的分区数：5，p0-p4     
主题B下的分区数：5，p0-p4     
客户端消费者数：2，c0、c1，且均订阅了A、B主题 
则分配效果是：   
c0：A的p0 p2 p4 B的p1 p3
c1：A的p1 p3    B的p0 p2 p4   
如上，在消费者都订阅了相同的主题情况下，大家像抓扑克牌一下分配分区，则大体是平均分配的，只会有无法整除的情况下有的消费者会多分配1个   

缺点：如果消费组内不同消费者订阅了不同的分区，则可能变成每个抓扑克牌的人在不同的牌堆里抓牌，仍导致分配不均   
例2：   
主题A下的分区数：5，p0-p4     
主题B下的分区数：5，p0-p4     
客户端消费者数：2，c0订阅了A、B主题，c1仅订阅了A主题
则分配效果是：   
c0：A的p0 p2 p4 B的p0 p1 p2 p4 p3   
c1：A的p1 p3  
又出现了分配不均，可能导致某消费者过载
### 3.StickyAssignor
StickyAssignor是一种有粘性、有记忆的策略，其宗旨有二：   
1.分区尽可能均匀分配   
2.分区尽可能与上次分配保持一致   
两者发生冲突，目标1优先于目标2   
例：
主题A有1个分区：AP0   
主题B有2个分区：BP0 BP1   
主题C有3个分区：CP0 CP1 CP2   
消费组中有3个消费者：      
C0订阅A   
C1订阅A B   
C2订阅A B C   
如果使用RoundRobinAssignor策略，结果：   
C0:AP0   
C1:BP0   
C2:BP1 CP0 CP1 CP2   
采用StickyAssignor分配   
C0:AP0
C1:BP0 BP1
C2:CP0 CP1 CP2
如果此时C0脱离消费组，则分配调整为   
C1:BP0 BP1 AP0 (BP0 BP1保持第一次分配不变)   
C2:CP0 CP1 CP2 (全都保持第一次分配不变)   
### 4.自定义分配策略
还可以通过实现partitionAssignor接口，进行自定义分配策略的定义，并配置在partition.assignment.strategy参数即可，场景较少，不悉数用法

## 二、再均衡
和生产队种地差不多，一共12陇地   
开始2个人，一人6陇（或其他算法）      
又加入2人，一人3陇   
病倒1个，一人4陇   
离职一个，又一人6陇   
### 场景
某些场景会触发再均衡   
- 有新消费者加入消费组
- 既有消费者退出消费组：下线、网络延时超过既定时间
- 既有消费者取消订阅主题
- 消费组对应的消费者协调器节点变化
- 消费组内消费者订阅的主题数量或主题内的分区数量发生变化     
其中：消费者协调器ConsumerCoordinator在消费者客户端，其负责与服务端的组协调器GroupCoordinator进行交互工作，两者主要职责就是再平衡的处理   
客户端将全部消费组分成多个子集，每个消费组的子集在服务端对应一个组协调器GroupCoordinator
### 流程
当客户端新增消费者时，如下处理流程
- 一阶段：连接服务端协调器阶段   
消费者先找到其所在组对应的GroupCoordinator所在的broker，与其建立连接（寻找方式是根据消费组groupId的哈希值，逐步寻找消费组对应的分区leader的broker节点）
- 二阶段：加入服务端协调器并选leader阶段，选分区策略   
加入该消费组对应的组协调器GroupCoordinator，选举该消费组的leader（随机选），leader负责干活，干啥活？要选举一个分配策略出来   
策略选举办法：   
1.收集所有消费者支持的策略（即partition.assignment.strategy配置的1个或多个策略），形成候选人   
2.每个消费者为自己的候选人投票，支持就+1   
3.比较所有候选人票数，获票最多的胜出   
异常：当选出的策略某个消费者不支持该策略，则报IllegalArgumentException：Member does not support protocol
- 三阶段：同步策略结果，启动心跳任务阶段   
leader将二阶段选举出的分配策略，通过GroupCoordinator同步给各个组内成员，完成信息同步后，开启心跳任务，消费者定时向GroupCoordinator发送心跳确定存活
- 四阶段：心跳监控阶段   
心跳作为独立线程，在轮询消息空挡发送心跳，如果心跳在足够时间内未发出，视为消费者死亡，触发一次再均衡   
心跳间隔参数：heartbeat.interval.ms，默认3000ms，其值不得超过session.timeout.ms的1/3   
session.timeout.ms需在group.min.session.timeout.ms（6s）和group.max.session.timeout.ms（300s）之间，超过session.timeout.ms则触发再均衡   
max.poll.interval.ms指定消费者组调用poll方法拉取消息的最大延迟，超过此时间，消费者被认为失败，触发再均衡   

## __consumer_offsets