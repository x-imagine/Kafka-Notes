package com.dce.kafka.constants;

/**
 * 实验用常量
 */
public class Cons {
    // kafka server ip:port
    final public static String HOST_PORT = "127.0.0.1:9092";

    final public static String JMX_HOST_PORT = "192.168.137.89:9999";
    final public static String JMX_HOST_PORT_1 = "192.168.137.89:9999";
    final public static String JMX_HOST_PORT_2 = "192.168.137.90:9999";

    final public static String TEST_TOPIC_NAME_MUTI_PARTITION = "topic-b";
    final public static String TEST_TOPIC_NAME_ONE_PARTITION = "book-topic";

    final public static String STRING_SERIALIZER_KEY = "org.apache.kafka.common.serialization.StringSerializer";
    final public static String STRING_SERIALIZER_VALUE = "org.apache.kafka.common.serialization.StringSerializer";
}
