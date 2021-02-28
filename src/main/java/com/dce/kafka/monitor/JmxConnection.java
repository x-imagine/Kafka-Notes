package com.dce.kafka.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.dce.kafka.constants.Cons.JMX_HOST_PORT;

public class JmxConnection {
    private static Logger log = LoggerFactory.getLogger(JmxConnection.class);

    private MBeanServerConnection conn;
    private String jmxURL;
    private String ipAndPort = JMX_HOST_PORT;
    private int port = 9999;
    private boolean newKafkaVersion = false;

    public JmxConnection(Boolean newKafkaVersion, String ipAndPort){
        this.newKafkaVersion = newKafkaVersion;
        this.ipAndPort = ipAndPort;
    }

    public boolean init(){
        jmxURL = "service:jmx:rmi:///jndi/rmi://" +ipAndPort+ "/jmxrmi";
        log.info("init jmx, jmxUrl: {}, and begin to connect it",jmxURL);
        try {
            JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);
            JMXConnector connector = JMXConnectorFactory.connect(serviceURL,null);
            conn = connector.getMBeanServerConnection();
            if(conn == null){
                log.error("get connection return null!");
                return  false;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Object getAttribute(ObjectName objName, String objAttr){
        if(conn== null)
        {
            log.error("jmx connection is null");
            return null;
        }

        try {
            return conn.getAttribute(objName,objAttr);
        } catch (MBeanException e) {
            e.printStackTrace();
            return null;
        } catch (AttributeNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (InstanceNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (ReflectionException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @param topicName
     * @return 获取topicName中每个partition所对应的logSize(即offset)
     */
    public Map<Integer,Long> getTopicEndOffset(String topicName){
        Set<ObjectName> objs = getEndOffsetObjects(topicName);
        if(objs == null){
            return null;
        }
        Map<Integer, Long> map = new HashMap<>();
        for(ObjectName objName:objs){
            int partId = getParId(objName);
            Object val = getAttribute(objName,"Value");
            if(val !=null){
                map.put(partId,(Long)val);
            }
        }
        return map;
    }

    private int getParId(ObjectName objName){
        if(newKafkaVersion){
            String s = objName.getKeyProperty("partition");
            return Integer.parseInt(s);
        }else {
            String s = objName.getKeyProperty("name");

            int to = s.lastIndexOf("-LogEndOffset");
            String s1 = s.substring(0, to);
            int from = s1.lastIndexOf("-") + 1;

            String ss = s.substring(from, to);
            return Integer.parseInt(ss);
        }
    }

    private Set<ObjectName> getEndOffsetObjects(String topicName){
        String objectName;
        if (newKafkaVersion) {
            objectName = "kafka.log:type=Log,name=LogEndOffset,topic="+topicName+",partition=*";
        }else{
            objectName = "\"kafka.log\":type=\"Log\",name=\"" + topicName + "-*-LogEndOffset\"";
        }
        ObjectName objName = null;
        Set<ObjectName> objectNames = null;
        try {
            objName = new ObjectName(objectName);
            objectNames = conn.queryNames(objName,null);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
            return  null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return objectNames;
    }
}