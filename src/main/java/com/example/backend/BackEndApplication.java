package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
public class BackEndApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
        System.out.println("=================================");
        System.out.println("个人博客后端系统启动成功！");
        System.out.println("接口文档地址：http://localhost:8080/api");
        System.out.println("=================================");
    }
}
