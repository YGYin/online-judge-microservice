package com.ygyin.ojjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.ygyin")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.ygyin.ojservicecli.service"})
public class OjJudgeServiceApplication {
    // 纯粹调用代码沙箱，没有mapper
    public static void main(String[] args) {
        SpringApplication.run(OjJudgeServiceApplication.class, args);
    }

}
