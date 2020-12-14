package com.baidu.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Data
@Table(name = "tb_order_status")
public class OrderStatusEntity {


    @Id
    private Long orderId;

    private Integer status;

    private Date createTime;

    private Date paymentTime;


}
