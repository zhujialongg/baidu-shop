package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Api(tags="商品接口")
public interface GoodsService {


    @ApiOperation(value = "获取spu信息")
    @GetMapping(value = "good/getSpuInfo")
    Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO);


}
