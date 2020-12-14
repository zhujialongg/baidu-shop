package com.baidu.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName {name}
 * @Description: TODO
 * @Author zhujialong
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Table(name = "tb_carousl")
@Data
public class CarouselEntity {

    @Id
    private Long id;

    private String imageUrl;

    private Long spuId;



}
