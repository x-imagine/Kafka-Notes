[toc]
## Consumer Config
### key.deserializer & value.deserializer
消息中key、value的反序列化类，需与producer中的序列化类对应

### bootstrap.servers
指定消费者连接的broker，可设置一个或多个，设置单个即可连接集群，但建议设置多个，避免单点宕机风险   

### group.id
consumer隶属消息组名称，默认""，设置为null会抛异常

### client.id
客户端id，如设定亦可，consumer会自动生成，如consumer-1

### enable.auto.commit
consumer客户端是否自动提交，默认为true，所谓的自动提交非每消费一条就提交一次，而是定期提交，提交周期根据另一个参数auto.commit.interval.ms所设置的时间决定，如果需要手动提交需要设置参数为false

### auto.commit.interval.ms
consumer自动提交时，提交的时间间隔，默认5秒。生效前提为enable.auto.commit设置true