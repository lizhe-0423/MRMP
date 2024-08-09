package com.joysuch.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@SpringBootTest
class YuapiGatewayApplicationTests {

    @Test
    void contextLoads() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName = new ObjectName("org.springframework.boot:type=Admin,name=SpringApplication");
            if (server.isRegistered(objectName)) {
                server.unregisterMBean(objectName);
                System.out.println("MBean unregistered successfully.");
            } else {
                System.out.println("MBean not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }







    }





