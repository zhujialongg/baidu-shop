package com.baidu.shop.acpect;

import com.baidu.shop.comment.LogAnnotation;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;

import com.baidu.shop.entity.LogEntity;
import com.baidu.shop.mapper.LogMapper;
import com.baidu.shop.util.GetIp;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.utils.JSONUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName {name}
 * @Description: TODO
 * @Author zhujialong
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Aspect
@Component
public class LogAspect {


    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private LogMapper logMapper;
    /*
    * 切入点
    * */
    @Pointcut("@annotation(com.baidu.shop.comment.LogAnnotation)")
    private void logPointcut(){
        System.out.println("切入点 开始");
    };


    @Transactional
    @AfterReturning(returning="result",value = "logPointcut()")  //后置通知
    public void  saveRecordLog(JoinPoint joinPoint, Object result){

        //获取requestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        //获取参数 获取目标方法的参数信息
        Object[] args = joinPoint.getArgs();
        //获取织入点方法  用的最多 通知的签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

        //获取方法名 代理的是哪些方法
        String[] parameterNames = methodSignature.getParameterNames();

       Map<Object, Object> map = new HashMap<>();
        for (int i = 0;i < parameterNames.length;i++){
            if(!parameterNames[i].equals("token"))
                map.put(parameterNames[i],args[i]);
        }

        //获取到切入点所在的方法
        Method method = methodSignature.getMethod();

        LogEntity logEntity = new LogEntity();
        logEntity.setParams(JSONUtil.toJsonString(map));

        logEntity.setOperationTime(new Date());

        //获取注解参数
        LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);

        if(null != annotation){
            logEntity.setModel(annotation.operationModel()); //操作的模块
            logEntity.setOperation(annotation.operation()); //操作细节
            logEntity.setType(annotation.operationType()); //操作类型

        }

        logEntity.setIp(GetIp.getIpAddress(request));//用户登录的ip

        logEntity.setOperationMethod(request.getRequestURI());
        String token = CookieUtils.getCookieValue(request, jwtConfig.getCookieName());

        if (token != null) { //验证登录成功
            //从token中取出用户信息
            try {
                UserInfo infoFromToken = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());
                logEntity.setUserName(infoFromToken.getUsername());
                logEntity.setUserId(infoFromToken.getId().longValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
         logMapper.insertSelective(logEntity);

    }


}
