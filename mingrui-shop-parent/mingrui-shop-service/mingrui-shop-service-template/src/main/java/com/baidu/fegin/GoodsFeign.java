package com.baidu.fegin;

import com.baidu.service.GoodsService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
                //上下文唯一标识 可随意命名   与 GoodsFeign区分
@FeignClient(contextId = "GoodsService",value = "xxx-service")
public interface GoodsFeign extends GoodsService {

}
