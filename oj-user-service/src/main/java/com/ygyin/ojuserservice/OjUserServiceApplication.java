package com.ygyin.ojuserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.ygyin.ojuserservice.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.ygyin")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.ygyin.ojservicecli.service"})
public class OjUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjUserServiceApplication.class, args);
    }

}
