package com.joysuch.storage;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/lizhe-0423">蓝莓</a>
 
 */
@SpringBootApplication()
@MapperScan("com.joysuch.storage")
@EnableScheduling
@EnableDubbo(scanBasePackages = "com.joysuch.dubbo.provider")
@PropertySource(value = "classpath:/provider-config.properties")
public class ApiStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiStorageApplication.class, args);
    }

}
