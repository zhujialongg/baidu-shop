package com.baidu.business;

import com.baidu.base.Result;
import com.baidu.dto.AddrDTO;
import com.baidu.dto.OrderDTO;
import com.baidu.dto.OrderInfo;
import com.baidu.entity.AddrEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Api(tags = "订单接口")
public interface OrderService {

    @ApiOperation(value = "创建订单")
    @PostMapping(value = "order/createOrder")
    Result<String> createOrder(@RequestBody OrderDTO orderDTO,@CookieValue(value = "MRSHOP_TOKEN") String token);


    @ApiOperation(value = "根据订单id查询订单信息")
    @GetMapping(value = "order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);


    @ApiOperation(value = "新增地址信息")
    @PostMapping(value = "save/saveAddrInfo")
    Result<JSONObject> saveAddr(@RequestBody AddrDTO addrDTO,@CookieValue(value = "MRSHOP_TOKEN") String token);


    @ApiOperation(value = "查询地址信息")
    @GetMapping(value = "search/getAddrInfo")
    Result<List<AddrEntity>> searchAddr(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @ApiOperation(value = "根据id查询地址信息")
    @GetMapping(value = "search/getAddrInfoById")
    Result<List<AddrEntity>> getAddrInfoById(@RequestParam String id);


    @ApiOperation(value = "删除地址信息")
    @DeleteMapping(value = "delete/removeAddrInfoById")
    Result<JSONObject> removeAddrInfoById(@RequestParam String id);
}
