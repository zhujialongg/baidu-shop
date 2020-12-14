package com.baidu.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@ApiModel(value = "支付数据传输")
@Data
public class PayInfoDTO {


    @ApiModelProperty(value = "订单编号", example = "1")
    @NotNull(message = "订单编号不能为空")
    private Long orderId;

    @ApiModelProperty(value = "总金额,实际金额(元$)")
    private String totalAmount;

    @ApiModelProperty(value = "订单名称")
    private String orderName;

    @ApiModelProperty(value = "订单描述")
    private String description;

}
