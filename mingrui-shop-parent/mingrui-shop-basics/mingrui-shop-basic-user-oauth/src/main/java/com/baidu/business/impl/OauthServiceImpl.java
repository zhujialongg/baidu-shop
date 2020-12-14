package com.baidu.business.impl;

import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserOauthMapper;
import com.baidu.business.OauthService;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Service
public class OauthServiceImpl implements OauthService {

    @Resource
    private UserOauthMapper userOauthMapper;


    @Override
    public String checkUser(UserEntity userEntity, JwtConfig jwtConfig) {

        String tocken = null;
        Example example = new Example(UserEntity.class);
        example.createCriteria().andEqualTo("username",userEntity.getUsername());

        List<UserEntity> userEntityList = userOauthMapper.selectByExample(example);

         if(userEntityList.size()==1){ //用户存在
             UserEntity entity = userEntityList.get(0);
             if(BCrypt.checkpw(userEntity.getPassword(),entity.getPassword())){//比较密码
                    //创建tocken
                 try {
                       tocken = JwtUtils.generateToken(new UserInfo(entity.getId(), entity.getUsername()),
                             jwtConfig.getPrivateKey(),
                             jwtConfig.getExpire());
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }
        return tocken;
    }


}
