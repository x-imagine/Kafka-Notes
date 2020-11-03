# Kafka-Quick-Start
## 系统环境及运行版本
- OS版本：CentOS7+
- JDK：1.8+
- Kafka：kafka_2.13-2.6.0
- zookeeper：zookeeper-3.6.2
## 环境构建
### JDK
- 下载jdk-8u121-linux-x64.tar.gz至服务器
- 解压
```
tar -xzvf jdk-8u121-linux-x64.tar.gz
```
- 配置环境变量
```
vi ~/.bash_profile
```
```
export PATH
export JAVA_HOME=/mnt/hgfs/share/jdk1.8.0_221
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH:.
```
```
source ~/.bash_profile
```
![](pic/01QuickStart/jdk.png)

### zookeeper
- 解压、设置环境变量（同上）   
![](pic/01QuickStart/zookeeper0.png)
![](pic/01QuickStart/zookeeper1.png)
- 创建文件目录、配置   
![](pic/01QuickStart/zookeeper3.png)
- 启动zookeeper   
![](pic/01QuickStart/zookeeper2.png)

### Kafka
- 直接解压，启动
quickstart过程单机通过控制台本机实验，暂不修改配置文件
```
bin/kafka-server-start.sh config/server.properties
```
![](pic/01QuickStart/kafka0.png)

## 控制台基础操作
进入bin目录
### 创建topic
```
kafka-topics.sh --create --topic hello-topic --bootstrap-server localhost:9092
```
![](pic/01QuickStart/createTopic.png)
### 查看topic信息
```
kafka-topics.sh --describe --topic hello-topic --bootstrap-server localhost:9092
```
![](pic/01QuickStart/viewTopic.png)
### 生产消息
```
kafka-console-producer.sh --topic hello-topic --bootstrap-server localhost:9092
```
![](pic/01QuickStart/producer.png)
### 消费消息
```
kafka-console-consumer.sh --topic hello-topic --from-beginning --bootstrap-server localhost:9092
```
![](pic/01QuickStart/consumer.png)
### 终止环境
- producer、consumer终止：ctrl+C
- 清除数据：先停zookeeper、kafka后，执行如下（如修改目录配置，需要调整位置）
```
rm -rf /tmp/kafka-logs /tmp/zookeeper
```
清除后，消费者无任何数据   
![](pic/01QuickStart/clean.png)
