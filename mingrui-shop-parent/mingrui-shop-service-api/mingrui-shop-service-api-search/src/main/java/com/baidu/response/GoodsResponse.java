package com.baidu.response;

import com.baidu.entity.BrandEntity;
import com.baidu.entity.CategoryEntity;
import com.baidu.base.Result;
import com.baidu.document.GoodsDoc;
import com.baidu.status.HTTPStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@Data
@NoArgsConstructor
public class GoodsResponse extends Result<List<GoodsDoc>> {

    private Long total;

    private Long totalPage;

    private List<BrandEntity> brandList;

    private List<CategoryEntity> categoryList;

    private Map<String, List<String>> specParamMap;


    public GoodsResponse(Long total, Long totalPage, List<BrandEntity> brandList,
                         List<CategoryEntity> categoryList, List<GoodsDoc> goodsDocs,Map<String, List<String>> specParamMap){

        super(HTTPStatus.OK,HTTPStatus.OK + "",goodsDocs);
        this.total = total;
        this.totalPage = totalPage;
        this.brandList = brandList;
        this.categoryList = categoryList;
        this.specParamMap = specParamMap;
    }


}
