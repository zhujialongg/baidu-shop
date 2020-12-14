package com.baidu.shop.mapper;

import com.baidu.dto.SkuDTO;
import com.baidu.entity.SkuEntity;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.DeleteByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public interface SkuMapper extends Mapper<SkuEntity>, DeleteByIdListMapper<SkuEntity,Long> {

    //k.*不能将带下划线的字段值映射 到 我们的java实体类中的 驼峰属性中
    @Select(value = "select k.*,k.own_spec as ownSpec,t.stock" +
            " from tb_sku k , tb_stock t where k.id = t.sku_id and k.spu_id=#{spuId}")
    List<SkuDTO> selectSkuAndStockBySpuId(Integer spuId);



}
