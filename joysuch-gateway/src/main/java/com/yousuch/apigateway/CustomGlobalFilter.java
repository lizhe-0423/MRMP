package com.yousuch.apigateway;

import io.vavr.collection.Ordered;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Comparator;
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Comparator comparator() {
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
       log.info("请求唯一标识: {}", request.getId());
       log.info("请求路径: {}", request.getPath());
       log.info("请求方法: {}", request.getMethod());
       log.info("请求参数: {}", request.getQueryParams());
       log.info("请求来源地址：{}",request.getRemoteAddress());
        return null;
    }
}
