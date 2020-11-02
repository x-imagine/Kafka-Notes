1. 云服务器部署kafka，客户机通过API无法调用kafka
由于云主机公网IP与其真实网卡IP并非同一IP，server.properties中配置的监听ip为网卡IP，客户机通过API访问的IP为公网IP，两者不一致，无法监听到。同时这样的访问场景也无实际业务意义。
2. 本地虚拟机部署kafka，实体机无法调用虚拟机kafka
检查虚拟机防火墙，关闭防火墙重试
```$shell
#查看状态
firewall-cmd --state
#关闭服务
systemctl stop firewalld.service
#禁止开机启动
systemctl disable firewalld.service
```