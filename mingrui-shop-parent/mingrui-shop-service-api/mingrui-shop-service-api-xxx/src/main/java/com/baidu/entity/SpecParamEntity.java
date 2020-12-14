package com.baidu.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Table(name = "tb_spec_param")
@Data
public class SpecParamEntity {



    @Id
    private Integer id;

    private Integer cid;

    private Integer groupId;

    private String name;

    //加上``会当成普通字符串处理
    @Column(name="`numeric`")
    private Integer numeric;

    private String unit;

    private Integer generic;

    private Integer searching;

    private String segments;


}
