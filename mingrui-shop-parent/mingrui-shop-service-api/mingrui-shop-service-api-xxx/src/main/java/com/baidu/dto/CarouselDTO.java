package com.baidu.dto;

import com.baidu.validate.group.MingruiOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName {name}
 * @Description: TODO
 * @Author zhujialong
 * @Date 2020/10/29
 * @Version V1.0
 **/
@ApiModel(value = "轮播图片DTO")
@Data
public class CarouselDTO {


    @ApiModelProperty(value = "主键",example = "1")
    @NotNull(message = "主键不能为空",groups = {MingruiOperation.Update.class})
    private Long id;

    @ApiModelProperty(value = "图片")
    @NotEmpty(message = "图片不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private String imageUrl;

    @ApiModelProperty(value = "品牌id")
    @NotNull(message = "品牌id不能为空",groups = {MingruiOperation.Add.class,MingruiOperation.Update.class})
    private Long spuId;


}
