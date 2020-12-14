package com.baidu.feign;

import com.baidu.service.SpecificationService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@FeignClient(contextId = "SpecificationService",value = "xxx-service")
public interface SpecificationFeign extends SpecificationService {

}
