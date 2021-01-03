# Server
## 一、协议
Kafka自定义了一组二进制TCP协议，每种类型的协议都有对应的request、response，Request包括RequestHeader和RequestBody   
### 1.Request
#### Header
- api_key：API标识，标识请求的类型是拉取或生产等，如0标识produce请求
- api_version：API版本
- client_id：客户端id
- correlation_id：本次请求相关id，相关id会被在响应中传递会请求端，请求端根据此id与请求对应，识别是哪个请求的响应   
注：所有类型的请求，对应的响应，其头文件相同及RequestHeader与ResponseHeader结构相同，Body不同
#### Body
例举ProduceRequestBody
- transactional_id：事务id，不使用事务则null
- acks：客户端中的acks参数，即分区中必须要有多少个副本收到消息，生产者才认为消息是发送成功
- timeout：请求超时时间，默认30s，客户端request.timeout.ms配置
- topic_data：发送数据的集合数组，以主题名称分类，包括：   
1.topic：主题名
2.data：主题数据，其下包括partition（分区号）、record_set(消息数据)

### 2.Response
例举ProduceResponse
- throttle_time_ms：超过配额限制需要延迟该请求的处理时间，无配额值为0
- response：类似请求的topic_data，是一个数组，包括：   
1.topic：主题名   
2.partition_responses:所有分区的响应，旗下包括partition（分区号）、error_code、base_offset、log_append_time、log_start_offset

其他拉取等协议类似

## 二、时间轮
- kafka中存在延时动作，如：延时生产、消费、删除等行为
- 由于Java中Timer、DelayQueue时间复杂度O(nlogn)
- 自定义时间轮复杂度O(1)
- 多种开源项目均有时间轮身影：zookeeper、Netty、Quartz等

### 1.时间轮结构
时间轮结构类似机械表齿轮机制：秒针齿轮转动60m进入分针齿轮1个单位，分针齿轮转动60分针进入时针1个单位，时针12个单位一周   
- 先说单时间轮的机制   
![](pic/09Server/time_wheel_single.png)
- 多级轮结构及升降级
![](pic/09Server/time_wheel_muti.png)

