# Kafka-Quick-Start
## 系统环境及运行版本
- OS版本：CentOS7+
- JDK：1.8+
- Kafka：kafka_2.13-2.6.0
- zookeeper：zookeeper-3.6.2
## 构建
1. JDK
- 下载jdk-8u121-linux-x64.tar.gz至服务器
- 解压
```
tar -xzvf jdk-8u121-linux-x64.tar.gz
```
- 配置环境变量(root用户为例)
```
vi /etc/profile
```
```$xslt
export PATH
export JAVA_HOME=/mnt/hgfs/share/jdk1.8.0_221
export JRE_HOME=${JAVA_HOME}/jre
export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib
export PATH=${JAVA_HOME}/bin:$PATH:.
```
```$xslt
source /etc/profile
```
![image text](pic/01QuickStart/jdk.png)