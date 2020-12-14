package com.baidu.shop.fegin;

import com.baidu.service.ShopElasticsearchService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@FeignClient(contextId = "ShopElasticsearchService",value = "api-search")
public interface ESFegin extends ShopElasticsearchService {

}
