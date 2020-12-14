package com.baidu.fegin;

import com.baidu.service.BrandService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@FeignClient(contextId = "BrandService",value = "xxx-service")
public interface BrandFeign extends BrandService {


}
