package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Table;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Table(name = "tb_category_brand")
@Data
public class CategoryBrandEntity {


    private Integer  categoryId;

    private Integer brandId;



}
