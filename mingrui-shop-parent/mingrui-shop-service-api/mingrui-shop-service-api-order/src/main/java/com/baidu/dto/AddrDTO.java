package com.baidu.dto;

import com.baidu.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "地址数据传输")
public class AddrDTO {


    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "名字")
    @NotEmpty(message = "不能为空",groups = {MingruiOperation.Update.class})
    private String name;

    @ApiModelProperty(value = "手机号",example = "1")
    @NotNull(message = "手机号不能为空",groups = {MingruiOperation.Update.class})
    private Long phone;

    @ApiModelProperty(value = "详细地址")
    @NotEmpty(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private String addr; //详细地址

    @ApiModelProperty(value = "邮箱")
    private String mailbox; //邮箱

    @NotEmpty(message = "用户id不为空",groups = {MingruiOperation.Update.class})
    private String userId;

}
