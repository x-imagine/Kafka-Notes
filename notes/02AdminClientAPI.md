# 工程构建
maven工程，引入依赖
```$xslt
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
            <version>2.1.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
            <version>2.1.2.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
            <version>2.3.8.RELEASE</version>
        </dependency>
    </dependencies>
```
# AdminClientAPI
## 用途
Kafka管理客户端API，实现topic、broker等各类对象的管理。

