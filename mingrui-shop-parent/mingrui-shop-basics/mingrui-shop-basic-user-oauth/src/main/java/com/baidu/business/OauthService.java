package com.baidu.business;

import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.entity.UserEntity;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public interface OauthService {

    String checkUser(UserEntity userEntity, JwtConfig jwtConfig);
}
