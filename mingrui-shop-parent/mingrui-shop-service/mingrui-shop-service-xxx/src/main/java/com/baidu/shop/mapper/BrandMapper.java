package com.baidu.shop.mapper;

import com.baidu.shop.entity.BrandEntity;
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
public interface BrandMapper extends Mapper<BrandEntity> {


    @Select(value = "select tb.* from tb_brand tb ,tb_category_brand tc where tb.id = tc.brand_id and tc.category_id = #{cid}")
    List<BrandEntity> getBrandByCategory(Integer cid);

}
