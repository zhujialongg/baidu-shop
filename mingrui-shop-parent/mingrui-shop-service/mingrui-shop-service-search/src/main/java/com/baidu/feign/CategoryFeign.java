package com.baidu.feign;

import com.baidu.service.CategoryService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@FeignClient(contextId = "CategoryService",value = "xxx-service")
public interface CategoryFeign extends CategoryService {

}
