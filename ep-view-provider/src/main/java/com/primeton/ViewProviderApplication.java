package com.primeton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling//此注解为允许定时方法
@EnableFeignClients//开启feign的远程调用
public class ViewProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ViewProviderApplication.class,args);
    }
}
