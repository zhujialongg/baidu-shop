package com.baidu.config;

import com.baidu.shop.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PublicKey;
import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Data
@Configuration
public class JwtConfig {


    @Value("${mrshop.jwt.pubKeyPath}")
    private String pubKeyPath;

    @Value("${mrshop.jwt.cookieName}")
    private String cookieName;

    //#{可以使用springEL表达式}
    @Value("#{'${mrshop.filter.excludes}'.split(',')}")
    private List<String> exludesPath;


    private PublicKey publicKey;//公钥

    private static final Logger logger =  LoggerFactory.getLogger(JwtConfig.class);

    @PostConstruct
    public void init(){
        try {
            //获取公钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥失败"+e);
            throw new RuntimeException();
        }

    }






}
