package com.baidu.entity;


import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_addr")
@Data
public class AddrEntity {

    @Id
    private Integer id;

    private String name;

    private Long phone;

    private String addr; //详细地址

    private String mailbox; //邮箱

    private String userId;

}
