package com.baidu.controller;

import com.baidu.service.PageService;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;


/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
//@Controller
//@RequestMapping(value = "item")  //不用加/
public class PageController {

//        @Autowired
        private PageService pageService;

//        @GetMapping(value = "/{spuId}.html")
        public String test(@PathVariable(value = "spuId") Integer spuId, ModelMap modelMap){

            //spu信息
            Map<String,Object> map =  pageService.getPageInfoSpuId(spuId);
            modelMap.putAll(map);
            return "item";
        }


}
