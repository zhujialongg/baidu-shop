package com.baidu.shop.mapper;

import com.baidu.shop.entity.CategoryBrandEntity;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
//接口的多继承
public interface CategoryBrandMapper extends InsertListMapper<CategoryBrandEntity>,Mapper<CategoryBrandEntity> {


}
