# Introduction
## 介绍
kafka：分布式流平台，非MQ   
安装包：kafka_scala版本-kafka版本.tgz   
如：kafka_2.11-2.4.0.tgz
依赖：zookeeper + jdk（scala是jdk开发）

## 核心概念
1. topic：逻辑存在的一个概念，由多个partition组成
2. partition：实际存储消息的单位——一个有序的队列，每个消息一个id，kafka保证partition内的消息按顺序被consumer消费，跨partition无法保证顺序
3. producer：生产者，向broker发消息
4. consumer：消费者，从broker取消息
5. kafka broker：理解为一个kafka的服务节点，可包含多个topic，多个broker可组成cluster

## API
1. AdminClientAPI：管理检测topic、broker等
2. Producer API：发布消息到topic
3. Consumer API：订阅topic、处理消息
4. Stream API：输入流转输出流
5. Connector API：系统或应用程序中拉取数据到kafka

## Producer
1. 写入机制：顺序写磁盘（高吞吐量），非随机写内存
2. 机制：不是接到一条发一条，而是单位时间内批量发送，降低IO
3. 分区原则：
- 计算批次：不断创建批次，并向批次append记录，批次有大小限制，超出阀值，则进行发送，并创建新的批次
- 计算分区：指定了partition，则直接写入，未指定，通过计算，决定消息进入哪一个partition
4. 发送原理：直接发送leader节点；发送到哪个partition默认是伪随机，也可指定负载均衡策略；发送是异步发送，减少io，提升吞吐量

## Consumer
1. 单个分区的消息，只能由ConsumerGroup中的某个Consumer消费，即一个partition不能对应多个Consumergroup
2. Consumer 从partition中消费的顺序，默认从头开始消费
3. 非指定情况下，单个ConsumerGroup会消费所有partition中的消息
4. 比较高效的处理是partition与同组内consumer一一对应（多partition单个consumer，其本质仍为线性处理）
5. consumer才处理消息方式
- 以consumer为主体的多线程处理，一个线程一个consumer，处理一个或多个partition，consumer线程内部为阻塞式的消息处理；对partiton的管控比较好，可单独提交partiton的offset
- 以消息为主题的多线程处理，一个consumer获取数据后，数据的处理为线程池，将数据分发给线程池的不同线程进行处理，消息处理非阻塞；相对易于快速处理数据，但对partition管控能力差
没有固定的好坏之分，需要根据业务分析，选择或组合使用

## 消息传递保障机制
1. 至少一次：只收到1次，可能多次收到
2. 正好一次：收到1-n次，速度最慢，需要比对去重
3. 最多一次：收到0-1次，只发不收，速度最快

## consumer group对partition的rebalance机制
当存在多个partition，consumer组内的不同成员，需要平均分配消费任务，由协调器负责协调。   
1. 新进成员向协调器发心跳请求（心跳请求长期存在，除非退出或崩溃）
2. 协调员接到请求，对现有成员发送新成员入场消息，通知需rebalance，成员提交offset
3. 协调员根据partition和组内成员情况，重新分配负载，再次执行消费
4. 期间当有成员离开或崩溃（无心跳），以同样方式重新rebalance
和生产队种地差不多，一共12陇地，开始2个人，一人6陇（或其他算法），又加入2人，一人3陇，病倒1个，一人4陇，离职一个，又一人6陇。   
问题：当协调员通知重新分配时，各路consumer成员同步offset，但如有同步失败的，再重新分配后可能产生重复消费问题。

## Stream


## springboot的集成与启动
1. 集成：https://www.cnblogs.com/toutou/archive/2019/10/07/11354330.html   
2. 启动：KafkaListenerEndpointRegistry在SpringIOC中已经被注册为Bean，通过该类完成启动
- 随容器启动：自定义ApplicationRunner接口，注入KafkaListenerEndpointRegistry实例，在run方法start监听
```
    @Resource
    private KafkaListenerEndpointRegistry registry;
    
    public void run(ApplicationArguments args) throws Exception {
        registry.getListenerContainerIds().forEach(id -> {
            if(registry.getListenerContainer(id).isRunning()) {
                registry.getListenerContainer(id).start();
            }
        });
    }
    
```
- 定时启动：创建一个@Component类，同时@EnableScheduling
```
    //定时器，每天凌晨0点开启监听
    @Scheduled(cron = "0 0 0 * * ?")
    public void startListener() {
        //判断监听容器是否启动，未启动则将其启动
        if (!registry.getListenerContainer("id").isRunning()) {
            registry.getListenerContainer("id").start();
        }
        registry.getListenerContainer("id").resume();
    }

    //定时器，每天早上10点关闭监听
    @Scheduled(cron = "0 0 10 * * ?")
    public void shutDownListener() {
        registry.getListenerContainer("id").pause();
    }
```
