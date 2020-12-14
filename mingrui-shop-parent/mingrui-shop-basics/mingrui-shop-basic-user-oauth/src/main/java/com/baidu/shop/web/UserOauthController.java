package com.baidu.shop.web;

import com.baidu.base.BaseApiService;
import com.baidu.base.Result;

import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.business.OauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.status.HTTPStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@RestController
@Api(tags = "用户认证接口")
public class UserOauthController extends  BaseApiService {



    @Autowired
    private OauthService oauthService;

    @Autowired
    private JwtConfig jwtConfig;


    @ApiOperation(value = "用户登陆")
    @PostMapping(value = "oauth/login")
    public  Result<JSONObject> login(@RequestBody UserEntity userEntity, HttpServletRequest request, HttpServletResponse response){

        //在impl层再次注入jwtConfig 比较冗余 所以直接当参数传过去
        String tocken  = oauthService.checkUser(userEntity,jwtConfig);

        if(StringUtils.isEmpty(tocken)){
            return this.setResultError(HTTPStatus.VALIDATE_PASSWORD_ERROR,"用户名或密码错误");
        }

        //将token放到cookie中;
        CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),
                tocken,jwtConfig.getCookieMaxAge(),true);

        return this.setResultSuccess();
    }


    @GetMapping(value = "oauth/verify")  //通过key ： cookieName 获得 token
    public Result<UserInfo> verifyUser(@CookieValue(value = "MRSHOP_TOKEN") String token,
                                       HttpServletRequest request,HttpServletResponse response){
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
            //重新解析token 证明用户是正确的登录状态 重新生成token 登录状态有恢复到30分钟了
            String newToken = JwtUtils.generateToken(userInfo, jwtConfig.getPrivateKey(), jwtConfig.getExpire());
            //重新放到cookie中
            CookieUtils.setCookie(request,response,jwtConfig.getCookieName(),newToken,jwtConfig.getCookieMaxAge(),true);

            //将信息传到前台页面
            return this.setResultSuccess(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return this.setResultError(HTTPStatus.VALIFY_ERROR,"验证失效");
        }

    }




}
