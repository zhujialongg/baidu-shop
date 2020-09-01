package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.baidu.shop.mapper")
public class RunXXXApplication {

    public static void main(String[] args) {

        SpringApplication.run(RunXXXApplication.class);
    }

}
