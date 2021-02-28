package com.dce.kafka.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dce.kafka.constants.Cons.*;

public class JmxMgr {
    private static Logger log = LoggerFactory.getLogger(JmxMgr.class);
    private static List<JmxConnection> conns = new ArrayList<>();

    public static boolean init(List<String> ipPortList, boolean newKafkaVersion){
        for(String ipPort:ipPortList){
            log.info("init jmxConnection [{}]",ipPort);
            JmxConnection conn = new JmxConnection(newKafkaVersion, ipPort);
            boolean bRet = conn.init();
            if(!bRet){
                log.error("init jmxConnection error");
                return false;
            }
            conns.add(conn);
        }
        return true;
    }

    public static Map<Integer, Long> getEndOffset(String topicName){
        Map<Integer,Long> map = new HashMap<>();
        for(JmxConnection conn:conns){
            Map<Integer,Long> tmp = conn.getTopicEndOffset(topicName);
            if(tmp == null){
                log.warn("get topic end offset return null, topic {}", topicName);
                continue;
            }
            for(Integer parId:tmp.keySet()){//change if bigger
                if(!map.containsKey(parId) || (map.containsKey(parId) && (tmp.get(parId)>map.get(parId))) ){
                    map.put(parId, tmp.get(parId));
                }
            }
        }
        return map;
    }

    public static void main(String[] args) {
        List<String> ipPortList = new ArrayList<>();
        ipPortList.add(JMX_HOST_PORT_1);
        ipPortList.add(JMX_HOST_PORT_2);
        JmxMgr.init(ipPortList,true);

        String topicName = TEST_TOPIC_NAME_MUTI_PARTITION;
        System.out.println(getEndOffset(topicName));
    }
}
