package com.dce.kafka.sample.api.admin;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

import static com.dce.kafka.sample.Cons.host_port;

/**
 * 获取AdminClient
 */
public class AdminClientFactory {
    private final static Logger LOGGER = LoggerFactory.getLogger(AdminClientFactory.class);
    /**
     * 获取AdminClient
     * @return AdminClient
     */
    public static AdminClient getAdminClient() {
        Properties properties = new Properties();
        properties.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, host_port);
        LOGGER.info("============================getAdminClient from " + host_port + "====================================");
        return AdminClient.create(properties);
    }

    public static void main(String[] args) {
        AdminClientFactory.getAdminClient();
    }
}
