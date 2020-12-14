package com.baidu.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.dto.UserDTO;
import com.baidu.entity.UserEntity;
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
@Api(tags = "用户接口")
public interface UserService {

    @ApiOperation(value = "用户注册")
    @PostMapping(value = "user/register")
    Result<JSONObject> regiter(@Validated({MingruiOperation.Add.class}) @RequestBody UserDTO userDTO);


    @ApiOperation(value = "校验用户名或手机号唯一")
    @GetMapping(value = "user/check/{value}/{type}")
    Result<List<UserEntity>> checkUsernameOrPhone(@PathVariable(value = "value")   String value, @PathVariable(value = "type") Integer type);


    @ApiOperation(value = "给手机号发送短信")
    @PostMapping(value = "user/sendValidCode")
    Result<JSONObject> sendValidCode(@RequestBody UserDTO userDTO);


    @ApiOperation(value = "校验验证码")
    @GetMapping(value = "user/checkValidCode")
    Result<JSONObject> checkValidCode(String phone,String validcode);

    @ApiOperation(value = "校验用户名或手机号唯一")
    @GetMapping(value = "user/checkPhone/{value}")
    Result<List<UserEntity>> checkByPhone(@PathVariable(value = "value") String value);



}
