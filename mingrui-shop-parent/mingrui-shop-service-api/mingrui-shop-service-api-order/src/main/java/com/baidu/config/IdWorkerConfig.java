package com.baidu.config;

import com.baidu.utils.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Configuration
public class IdWorkerConfig {


    @Value(value = "${mrshop.worker.workerId}")
    private long workerId; //工作机器id


    @Value(value = "${mrshop.worker.datacenterId}")
    private long datacenterId;// 序列号id

    @Bean
    public IdWorker idWorker(){
        return  new IdWorker(workerId,datacenterId);
    }

}
