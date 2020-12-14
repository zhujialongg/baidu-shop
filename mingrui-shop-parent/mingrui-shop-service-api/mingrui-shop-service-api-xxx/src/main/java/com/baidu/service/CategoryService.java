package com.baidu.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.entity.CategoryEntity;
import com.baidu.base.Result;
import com.baidu.validate.group.MingruiOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/

@Api(tags = "商品分类接口")
public interface CategoryService {

    @ApiOperation(value = "查询商品分类信息")
    @GetMapping(value = "category/list")
    public Result<List<CategoryEntity>> getCategoryByPid(Integer pid);


    @ApiOperation(value = "新增分类")
    @PostMapping(value = "category/add")
    //声明哪个组下面的参数参加的校验---当前是Add组下的参数参加校验
    public Result<JSONObject> saveCategory(@Validated({MingruiOperation.Add.class})@RequestBody CategoryEntity entity);


    @ApiOperation(value = "修改分类")
    @PutMapping(value = "category/edit")
    //声明哪个组下面的参数参加的校验---当前是update组下的参数参加校验
    public Result<JSONObject> editCategory(@Validated({MingruiOperation.Update.class}) @RequestBody CategoryEntity entity);

    @ApiOperation(value = "删除分类")
    @DeleteMapping(value = "category/remove")
    public Result<JSONObject> removeCategory(Integer id);


    @ApiOperation(value = "通过品牌id查询商品分类")
    @GetMapping(value = "category/getByBrand")
    public Result<List<CategoryEntity>> getByBrand(Integer brandId);


    @ApiOperation(value = "通过品牌id查询商品分类")
    @GetMapping(value = "category/getCateByIds")
    Result<List<CategoryEntity>> getCategoryByIds(@RequestParam String cateIds);



}
