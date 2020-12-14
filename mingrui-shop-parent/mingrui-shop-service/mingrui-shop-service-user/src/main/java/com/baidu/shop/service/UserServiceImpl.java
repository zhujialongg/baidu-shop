package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.dto.UserDTO;
import com.baidu.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.constant.ShopConstant;
import com.baidu.constant.UserConstant;
import com.baidu.redis.repository.RedisRepository;
import com.baidu.service.UserService;
import com.baidu.utils.BCryptUtil;
import com.baidu.utils.BaiduBeanUtil;
import com.baidu.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService  implements UserService {

    @Resource
    private UserMapper userMapper;

    @Autowired
    private RedisRepository redisRepository;

    /*
    * 校验手机号是否唯一
    *
    * */
    @Override
    public Result<List<UserEntity>> checkByPhone(String value) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(StringUtil.isEmpty(value)){
            return this.setResultError("手机号不能为空");
        }
        criteria.andEqualTo("phone",value);
        List<UserEntity> userEntityList = userMapper.selectByExample(example);

        return this.setResultSuccess(userEntityList);
    }

    @Override
    public Result<JSONObject> checkValidCode(String phone, String validcode) {

        String code = redisRepository.get(ShopConstant.USER_PHONE_PRE + phone);
        //涉及到空指针问题  若sendValidCode方法里等待时间超时,code会被清空
        if(!validcode.equals(code)) return this.setResultError("验证码输入错误");

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {

        //生成随机的六位验证码
        String code = (int)((Math.random()*9 + 1) * 100000) + "";
        //短信条数只有10条,不够我们测试.所以就不发送短信验证码了,直接在控制台打印就可以

        redisRepository.set(ShopConstant.USER_PHONE_PRE + userDTO.getPhone(), code);
        log.debug("向手机号码:{} 发送验证码:{}",userDTO.getPhone(),code);
        redisRepository.expire(ShopConstant.USER_PHONE_PRE + userDTO.getPhone(),120);
        //发送短信验证码
        //LuosimaoDuanxinUtil.SendCode(userDTO.getPhone(),code);
        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUsernameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(type== UserConstant.USER_TYPE_USERNAME){
            criteria.andEqualTo("username",value);
        }else if(type==UserConstant.USER_TYPE_PHONE){
            criteria.andEqualTo("phone",value);
        }
        List<UserEntity> userEntityList = userMapper.selectByExample(example);
        //正确的做法应该返回 一个常量
        return this.setResultSuccess(userEntityList);
    }

    @Override
    public Result<JSONObject> regiter(UserDTO userDTO) {

        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userDTO.getPassword(),BCryptUtil.gensalt()));
        userEntity.setCreated(new Date());

        userMapper.insertSelective(userEntity);

        return this.setResultSuccess();
    }


}
