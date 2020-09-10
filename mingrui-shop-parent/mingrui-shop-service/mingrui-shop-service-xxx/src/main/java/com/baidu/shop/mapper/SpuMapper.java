package com.baidu.shop.mapper;

import com.baidu.shop.entity.SpuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public interface SpuMapper extends Mapper<SpuEntity> {


    @Select(value = "update tb_spu set saleable = 0 where id = #{id}")
    void updateByspuId(Integer id);

    @Select(value = "update tb_spu set saleable = 1 where id = #{id}")
    void update2ByspuId(Integer id);
}
