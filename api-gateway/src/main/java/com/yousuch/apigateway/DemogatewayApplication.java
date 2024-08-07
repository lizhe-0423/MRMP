package com.yousuch.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemogatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemogatewayApplication.class, args);
    }

    /**
     * 配置自定义的路由定位器
     * 通过RouteLocatorBuilder构建器方式来定义路由规则
     *
     * @param builder 路由构建器对象，用于创建路由配置
     * @return 返回一个RouteLocator对象，包含自定义的路由规则
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("tobaidu", r -> r.path("/baidu")
                        .uri("http://www.baidu.com"))
                .route("tohttpbin", r -> r.path("/httpbin")
                        .uri("http://httpbin.org"))
                // 定义一个主机路由，当主机名为"*.myhost.org"时，转发到"http://httpbin.org"
                .route("host_route", r -> r.path("/yupiicu")
                        .uri("http://yupi.icu"))
                // 构建并返回路由配置
                .build();
    }


}