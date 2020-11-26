# 一、工程构建
- maven工程，pom.xml引入依赖
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>2.1.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.apache.kafka</groupId>
            <artifactId>kafka-clients</artifactId>
            <version>2.6.0</version>
        </dependency>
```
- logback-test.xml   
过滤掉过多日志
```
<?xml version="1.0" encoding="UTF-8"?>
<configuration debug="false">
    <!-- definition of appender STDOUT -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg %n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <!-- appender referenced after it is defined -->
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```
# 二、 AdminClientAPI
## 1.用途
Kafka管理客户端API，实现topic、broker等各类对象的管理。
对应源码包：com.dce.kafka.sample.api.admin

## 2.获得AdminClient
对kafka任何对象的管理，均需要首先获取AdminClient。
```
    /**
     * 获取AdminClient
     * @return AdminClient
     */
    public static AdminClient getAdminClient() {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, host_port);
        LOGGER.info("============================getAdminClient from " + host_port + "====================================");
        return AdminClient.create(properties);
    }
```
1. AdminClient.create()接受Properties、Map作为入参，结果一致；
2. AdminClientConfig包括需要配置的各类属性常量，避免变量拼错。

## 3.Topic操作
### 创建
```
    /**
     * 创建topic，打印topic信息
     */
    public static void createTopic(String topicName) {
        AdminClient adminClient = AdminClientFactory.getAdminClient();
        short partitionsNum = 1;
        short refactor = 1;
        NewTopic newTopic = new NewTopic(topicName, partitionsNum, refactor);
        CreateTopicsResult topics = adminClient.createTopics(Arrays.asList(newTopic));
        LOGGER.info("--------------   topics   -------------------- :" + topics);
    }
```
### 查看
### 删除
