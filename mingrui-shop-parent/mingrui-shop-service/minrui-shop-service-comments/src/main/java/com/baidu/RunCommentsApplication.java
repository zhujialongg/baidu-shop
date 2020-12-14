package com.baidu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.baidu.shop.mapper")
public class RunCommentsApplication {


    public static void main(String[] args) {
        SpringApplication.run(RunCommentsApplication.class);
    }

}
