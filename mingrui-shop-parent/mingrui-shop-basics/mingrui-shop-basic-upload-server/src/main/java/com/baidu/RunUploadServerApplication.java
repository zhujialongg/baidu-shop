package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@SpringBootApplication
@EnableEurekaClient
public class RunUploadServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunUploadServerApplication.class);

    }
}
