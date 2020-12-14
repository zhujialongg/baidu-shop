package com.baidu.shop.mapper;

import com.baidu.entity.CategoryEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public interface CategoryMapper extends Mapper<CategoryEntity> , SelectByIdListMapper<CategoryEntity,Integer> {

    @Select(value = "select c.id,c.`name` from tb_category c where c.id in( select tb.category_id from tb_category_brand tb where tb.brand_id = #{brandId})")
     List<CategoryEntity> getByBrand(Integer brandId);

    @Select(value = "select  group_concat(`name` separator '/') as categoryName from tb_category where id in(#{cid1},#{cid2},#{cid3})")
    String getByCateId(Integer cid1, Integer cid2, Integer cid3);

}
