package com.baidu.shop.fegin;

import com.baidu.service.TemplateService;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@FeignClient(contextId = "TemplateService",value = "api-template")
public interface TemplateFegin extends TemplateService {

}
