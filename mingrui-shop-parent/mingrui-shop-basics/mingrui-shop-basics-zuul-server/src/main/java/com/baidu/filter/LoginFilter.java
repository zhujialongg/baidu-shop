package com.baidu.filter;

import com.baidu.config.JwtConfig;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Component
public class LoginFilter extends ZuulFilter {


    @Autowired
    private JwtConfig jwtConfig;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 5; //优先级 最好不要设置成0
    }

    @Override
    public boolean shouldFilter() {
        //获取上下文
        RequestContext cxt = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = cxt.getRequest();
        //获取当前请求的url
        String requestURI = request.getRequestURI();
        //当前请求如果不再白名单内则开启拦截器
        logger.debug("请求路径" + requestURI);
        //如果当前请求是登录请求,不执行拦截器
        return !jwtConfig.getExludesPath().contains(requestURI); //取反false 不拦截
    }

    @Override
    public Object run() throws ZuulException {

        //获取上下文
        RequestContext cxt = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = cxt.getRequest();

        logger.info("拦截到的请求" +request.getRequestURI());

        //获取tocken
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());
        logger.info("token信息" + token);

        //通过公钥解密
        if(token != null){
            try {
                JwtUtils.getInfoFromToken(token,jwtConfig.getPublicKey());
            } catch (Exception e) {
                logger.info("解析失败，拦截" +token);
                //校验出现异常，返回 403
                cxt.setSendZuulResponse(false);
                cxt.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);
            }
        }else{
            cxt.setResponseStatusCode(HttpStatus.SC_FORBIDDEN);
        }
        return null;
    }
}
