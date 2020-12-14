package com.baidu.service;


import com.alibaba.fastjson.JSONObject;
import com.baidu.dto.SpuDTO;
import com.baidu.entity.SkuEntity;
import com.baidu.entity.SpuDetailEntity;
import com.baidu.base.Result;
import com.baidu.dto.SkuDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;
import java.util.List;


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
    Result<List<SpuDTO>> getSpuInfo(@SpringQueryMap SpuDTO spuDTO);


    @ApiOperation(value = "新增商品")
    @PostMapping(value = "goods/save")
    Result<JSONObject> saveGoods(@RequestBody SpuDTO spuDTO);


    //回显操作
    @ApiOperation(value = "获取spu详情信息")
    @GetMapping(value = "goods/getSpuDetailBydSpu")
    Result<SpuDetailEntity>  getSpuDetailBydSpu(@RequestParam Integer spuId);

    //回显操作
    @ApiOperation(value = "获取sku信息")
    @GetMapping(value = "goods/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(@RequestParam Integer spuId);


    @ApiOperation(value = "修改商品")
    @PutMapping(value = "goods/save")
    Result<JSONObject> editGoods(@RequestBody SpuDTO spuDTO);


    @ApiOperation(value = "删除商品")
    @DeleteMapping(value = "goods/removeGoods")
    Result<JSONObject> reoveGoods(Integer spuId);

    @ApiOperation(value = "下架商品")
    @PutMapping(value = "goods/editSaleable")
    Result<JSONObject> editSaleable(Integer id);



    @ApiOperation(value="通过skuId查询信息")
    @GetMapping(value = "goods/getSkuBySkuId")
    Result<SkuEntity> getSkuBySkuId(@RequestParam Long skuId);

}
