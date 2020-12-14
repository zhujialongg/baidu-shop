package com.baidu.resp;

import com.baidu.entity.GoodsEntity;
import org.jcp.xml.dsig.internal.dom.DOMUtils;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public interface GoodsEsRespository extends ElasticsearchRepository<GoodsEntity,Long> {


    List<GoodsEntity> findAllByBrand(String title);


    List<GoodsEntity> findByAndPriceBetween(Double start,Double end);


}
