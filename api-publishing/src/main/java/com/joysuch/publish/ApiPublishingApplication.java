package com.joysuch.publish;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author <a href="https://github.com/lizhe-0423">蓝莓</a>
 
 */
@SpringBootApplication()
@MapperScan("com.joysuch.publish")
@EnableScheduling
public class ApiPublishingApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiPublishingApplication.class, args);
    }

}
