package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Table(name="tb_spec_group")
@Data
public class SpecGroupEntity {


    @Id
    private Integer id;

    private Integer cid;

    private String name;



}
