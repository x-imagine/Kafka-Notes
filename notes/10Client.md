# Client
## 一、客户端分区分配
消费者客户端通过partition.assignment.strategy设置消费者与主题之间分区分配策略   
策略包括：   
- org.apache.kafka.clients.consumer.RangeAssignor
- org.apache.kafka.clients.consumer.RoundRobinAssignor
- org.apache.kafka.clients.consumer.StickyAssignor   
可以设置一个或多个策略
### 1.RangeAssignor
RangeAssignor策略原理是按照分区数和消费者数进行整除运算获得一个跨度，按照跨度进行平均分配。简单理解就是对某主题的分区尽可能平分，如：   
例1：   
主题A下的分区数：10，p0-p9   
客户端消费者数：2，c0-c1   
则分配效果是：   
c0：p0 p1 p2 p3 p4   
c1：p5 p6 p7 p8 p9   
例2：   
主题A下的分区数：7，p0-p6     
客户端消费者数：3，c0-c2    
则分配效果是：   
c0：p0 p1 p2   
c1：p3 p4    
c2：p5 p6 

缺点：由于分配顺序是按照消费者名称的字典顺序分配，如果多个主题分区都不能被整除，那么字典顺序靠前的几个消费者，负载将更高，甚至过载。   
如：
上例2中的情况，如果存在相同的主题B、C、D，分区数均与A一致，则分配结果为
c0：A的p0 p1 p2，B的p0 p1 p2，C的p0 p1 p2，共9个分区      
c1：A的p3 p4，B的p3 p4，C的p3 p4，共6个分区  
c2：A的p5 p6，B的p5 p6，C的p5 p6，共6个分区  

### 2.RoundRobinAssignor

### 3.StickyAssignor