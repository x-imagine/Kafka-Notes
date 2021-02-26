# Monitor
- kafka监控维度主要分为集群信息、broker、topic、消费等部分
- 监控方式用两种：   
1.使用成型的开源工具，优点快速简单，缺点无法与自建系统融合并建立个性化监控管理   
2.通过JMX获取关键数据自行实现监控程序，优缺点与开源工具相反   

## CMAK(Cluster Manager for Apache Kafka)
雅虎开源工具，简单部署即可使用，提供有监控、管理功能
### 安装
1.获取   
https://github.com/yahoo/CMAK获取release压缩包   
下载不了从百度网盘下载：https://pan.baidu.com/s/12Bn_8MjzcFUsAYawarPEvQ 提取码：9527 
2.配置   
- 解压至服务器
```
unzip cmak-3.0.0.4.zip -d /xx_path
```
- 修改配置  
```
vi conf/application.conf 
```
![](pic/13Monitor/manage-config.png)
- 启动
```
nohup bin/cmak -Dconfig.file=conf/application.conf &
```
### 增加集群配置
首先确保kafka正常工作后，浏览器访问：ip:9000即可，9000是默认端口，可以在启动脚本修改默认端口号
![](pic/13Monitor/add-cluster.png)
![](pic/13Monitor/add-config.png)
![](pic/13Monitor/cluster.png)
### 集群信息
![](pic/13Monitor/cluster-info.png)
### 主题信息
![](pic/13Monitor/topic-info.png)
### 管理功能
![](pic/13Monitor/func.png)
进入集群后，有很多管理功能，如增加主题、分区、优化副本等，都能对应到kafka/bin下的脚本工具，只不过增加了可视化支持
