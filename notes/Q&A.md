# 一、环境
## 1.云服务器部署kafka，客户机通过API无法调用kafka
由于云主机公网IP与其真实网卡IP并非同一IP，server.properties中配置的监听ip为网卡IP，客户机通过API访问的IP为公网IP，两者不一致，无法监听到。同时这样的访问场景也无实际业务意义。
## 2.本地虚拟机部署kafka，实体机无法调用虚拟机kafka
检查虚拟机防火墙，关闭防火墙重试
```$shell
#查看状态
firewall-cmd --state
#关闭服务
systemctl stop firewalld.service
#禁止开机启动
systemctl disable firewalld.service
```
## 3.主机休眠或睡眠再唤醒，无法连接虚拟机（kafka所在的虚拟机）
原因是休眠后虚拟网卡没有被唤醒，需要在“网络和 Internet\更改适配器选项”找到虚拟网卡，禁用-启用即可

# 二、kafka机制
## 1.消息在kafka中存多久
broker的log.cleanup.policy=delete\compact决定删除还是压缩，可同时支持两种   
log.retention.hours \ log.retention.minutes \ log.retention.ms 时间单位越小，优先级越高   
log.retention.hours默认168，即7天之后就删除或压缩了

## 2.集群环境，启停kafka节点，分区分配的变化
集群中如果有broker宕机或停止服务，该broker承担的leader分区，将转至其他broker节点负载。再重启该broker后，其不会自动恢复原始的分配   
如果auto.leader.rebalance.enable=true（默认值true），则在leader.imbalance.check.interval.seconds（默认300s）的检查频度后，重新进行分区的再分配   
如果auto.leader.rebalance.enable=false，则服务端不会自动重分配，需要手动通过kafka-leader-election.sh分配