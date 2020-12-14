package com.baidu.dto;

import com.baidu.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Data
@ApiModel(value = "用户DTO")
public class UserDTO {

    @ApiModelProperty(value = "用户主键",example = "1")
    @NotNull(message = "主键不能为空", groups = {MingruiOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "账户")
    @NotNull(message = "账户不能为空", groups = {MingruiOperation.Add.class})
    private String username;

    @ApiModelProperty(value = "密码")
    @NotNull(message = "密码不能为空", groups = {MingruiOperation.Add.class})
    private String password;

    @ApiModelProperty(value = "手机号")
    @NotNull(message = "手机号不能为空", groups = {MingruiOperation.Add.class})
    private String phone;

    @ApiModelProperty(hidden = true)
    private Date created;

    @ApiModelProperty(hidden = true)
    private String salt;



}
