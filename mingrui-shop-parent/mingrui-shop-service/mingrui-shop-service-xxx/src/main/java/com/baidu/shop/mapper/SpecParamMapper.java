package com.baidu.shop.mapper;


import com.baidu.shop.entity.SpecParamEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public interface SpecParamMapper extends Mapper<SpecParamEntity> {

//    @Select(value = "select * from tb_spec_param p where p.group_id = #{id}")
//    List<SpecParamEntity> selectByGroupId(Integer id);


}