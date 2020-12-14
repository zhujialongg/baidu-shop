package com.baidu.service;

import com.baidu.dto.*;
import com.baidu.entity.*;
import com.baidu.fegin.BrandFeign;
import com.baidu.fegin.CategoryFeign;
import com.baidu.fegin.GoodsFeign;
import com.baidu.fegin.SpecificationFeign;
import com.baidu.base.Result;
import com.baidu.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
//@Service
public class PageServiceImpl implements PageService {

//    @Autowired
    private GoodsFeign goodsFeign;

//    @Autowired
    private BrandFeign brandFeign;

//    @Autowired
    private CategoryFeign categoryFeign;

//    @Autowired
    private SpecificationFeign specificationFeign;

    @Override
    public Map<String, Object> getPageInfoSpuId(Integer spuId) {

        HashMap<String, Object> map = new HashMap<>();

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(spuDTO);
        if(spuInfoResult.getCode()==200){
            if(spuInfoResult.getData().size()==1){

                //根据spuId查询spu信息
                SpuDTO spuInfo = spuInfoResult.getData().get(0);
                map.put("spuInfo",spuInfo);
                //spuDetail
                Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBydSpu(spuId);
                if(spuDetailResult.getCode()==200){
                    SpuDetailEntity   spuDetailInfo = spuDetailResult.getData();
                        map.put("spuDetailInfo",spuDetailInfo);
                }

                //根据spuInfo.getBrandId()查询品牌信息
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(spuInfo.getBrandId());
                Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDTO);

                //根据 spuInfo查询到的cid查询 分类信息
                Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIds(
                        String.join(",", Arrays.asList(spuInfo.getCid1() + "", spuInfo.getCid2() + "", spuInfo.getCid3() + ""))
                );
                if(categoryResult.getCode()==200){
                    System.out.println(categoryResult.getData().size());
                    map.put("cateList",categoryResult.getData());
                }
                if(brandInfoResult.getCode()==200){
                 PageInfo<BrandEntity> data = brandInfoResult.getData();

                    List<BrandEntity> brandList = data.getList();
                    if(brandList.size()==1){
                            map.put("brandInfo", brandList.get(0));
                    }
                }

                //通过spuInfo.getCid3()查询规格组
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuInfo.getCid3());
                Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.getSpecGroupInfo(specGroupDTO);

                if(specGroupResult.getCode()==200){
                    List<SpecGroupEntity> specGroupList = specGroupResult.getData();

                    List<SpecGroupDTO> groupParamsList = specGroupList.stream().map(specGroup -> {
                        SpecGroupDTO sgd = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);
                        //通过specGroup.getId() 和通用generic查询  通用参数
                        SpecParamDTO specParamDTO = new SpecParamDTO();
                        specParamDTO.setGroupId(specGroup.getId());
                        specParamDTO.setGeneric(1);
                        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
                        if (specParamResult.getCode() == 200) {
                            sgd.setSpecParams(specParamResult.getData());
                        }
                        return sgd;
                    }).collect(Collectors.toList());
                    //通用参数。。。。
                    map.put("groupParamsList",groupParamsList);

                }

                //特有规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specGroupDTO.setCid(spuInfo.getCid3());
                specParamDTO.setGeneric(0);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
                if(specParamResult.getCode()==200){
                    //需要将数据转换为map方便页面操作
                    HashMap<Integer, String> specMap = new HashMap<>();
                    specParamResult.getData().stream().forEach(spec->{
                        specMap.put(spec.getId(),spec.getName());
                    });
                    map.put("specParamMap",specMap);
                }
            }
            //sku
            Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
            if (skuResult.getCode()==200) {
                List<SkuDTO> skuList = skuResult.getData();
                map.put("skuList",skuList);
            }
        }
        return map;
    }
}
