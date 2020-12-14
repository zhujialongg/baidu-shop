package com.baidu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
//日志需要去掉这个(exclude = {DataSourceAutoConfiguration.class}) 作用是使默认数据源失效
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class RunCarServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RunCarServerApplication.class);
    }

}
