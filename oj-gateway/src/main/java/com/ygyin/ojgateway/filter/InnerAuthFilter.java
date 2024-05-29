package com.ygyin.ojgateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class InnerAuthFilter implements GlobalFilter, Ordered {

    private AntPathMatcher matcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest httpRequest = exchange.getRequest();
        String path = httpRequest.getURI().getPath();
        // 路径包含 inner，给前端返回 403
        if (matcher.match("/**/inner/**", path)){
            ServerHttpResponse httpResponse = exchange.getResponse();
            httpResponse.setStatusCode(HttpStatus.FORBIDDEN);
            DataBufferFactory dataBufferFactory = httpResponse.bufferFactory();
            DataBuffer dataBuffer = dataBufferFactory.wrap("No permission".getBytes(StandardCharsets.UTF_8));
            return httpResponse.writeWith(Mono.just(dataBuffer));
        }
        // 不拦截，放行继续
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
