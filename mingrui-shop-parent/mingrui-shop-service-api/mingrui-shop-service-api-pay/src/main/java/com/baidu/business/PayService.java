package com.baidu.business;

import com.baidu.dto.PayInfoDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Api(tags = "支付接口")
public interface PayService {


    @ApiOperation(value = "请求支付")
    @GetMapping(value = "pay/requestPay")//请求支付
    void requestPay(PayInfoDTO payInfoDTO, HttpServletResponse response);


    @ApiOperation(value = "接收支付宝通知")
    @GetMapping(value = "pay/returnNotify")//请求支付
    void returnNotify(HttpServletRequest request);

    @ApiOperation(value = "返回支付成功页面")
    @GetMapping(value = "pay/returnURL")//请求支付
    void returnURL(HttpServletRequest request,HttpServletResponse response);

}
