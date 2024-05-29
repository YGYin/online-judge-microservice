package com.ygyin.ojgateway.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Arrays;

@Configuration
public class CrossDomainFilter {
    // 给网关接收到请求的时候，给该请求的响应头添加允许跨域的注解
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.addAllowedMethod("*");
        corsConfig.setAllowCredentials(true);
        // 应改为实际的访问 url 或 本地访问 url
        corsConfig.setAllowedOriginPatterns(Arrays.asList("*"));
        corsConfig.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(corsConfigSource);
    }
}
