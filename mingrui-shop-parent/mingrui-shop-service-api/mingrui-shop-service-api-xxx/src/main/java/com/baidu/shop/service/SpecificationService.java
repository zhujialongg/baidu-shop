package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MingruiOperation;
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
@Api(tags = "规格接口")
public interface SpecificationService {


    @ApiOperation(value = "通过条件查询规格")
    @GetMapping(value = "/specgroup/getSpecGroupInfo")
    Result<List<SpecGroupEntity>> getSpecGroupInfo(SpecGroupDTO specGroupDTO);


    @ApiOperation(value = "新增规格组")
    @PostMapping(value = "specgroup/save")
    Result<JSONObject> saveSpecGroup(@Validated({MingruiOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);


    @ApiOperation(value = "修改规格组")
    @PutMapping(value = "specgroup/save")
    Result<JSONObject> editSpecGroup(@Validated({MingruiOperation.Update.class}) @RequestBody SpecGroupDTO specGroupDTO);


    @ApiOperation(value = "删除规格组")
    @DeleteMapping(value = "specgroup/remove")
    Result<JSONObject> removeSpecGroup(Integer id);

    @ApiOperation(value = "查询规格参数")
    @GetMapping(value = "specparam/getSpecParamInfo")
    Result<SpecParamEntity> getSpecParamInfo(SpecParamDTO specParamDTO);


    @ApiOperation(value = "新增规格参数")
    @PostMapping(value = "specparam/saveSpecParam")
    Result<SpecParamEntity>  saveSpecParam(@Validated({MingruiOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);



    @ApiOperation(value = "修改规格参数")
    @PutMapping(value = "specparam/saveSpecParam")
    Result<SpecParamEntity>  editSpecParam(@Validated({MingruiOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiOperation(value = "修改规格参数")
    @DeleteMapping(value = "sspecparam/removeSpecParam")
    Result<SpecParamEntity>  removeSpecParam(Integer  id);


}