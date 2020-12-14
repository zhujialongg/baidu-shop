package com.baidu.service;


import com.alibaba.fastjson.JSONObject;
import com.baidu.dto.*;
import com.baidu.entity.*;
import com.baidu.fegin.BrandFeign;
import com.baidu.fegin.CategoryFeign;
import com.baidu.fegin.GoodsFeign;
import com.baidu.fegin.SpecificationFeign;
import com.baidu.base.BaseApiService;

import com.baidu.base.Result;
import com.baidu.utils.BaiduBeanUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecificationFeign specificationFeign;


    @Autowired
    private BrandFeign brandFeign;


    //注入静态化模板
    @Autowired
    private TemplateEngine templateEngine;

    //静态资源文件生成路径
    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;


    @Override
    public Result<JSONObject> delHTMLBySpuId(Integer spuId) {
        File file = new File(staticHTMLPath + File.separator + spuId + ".html");
        if(!file.delete()){
            return this.setResultError("文件删除失败");
        }
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {

        Map<String, Object> map = this.getPageInfoBySpuId(spuId);
        //创建 静态模板
        this.createStaticHTML(map,spuId);

        return this.setResultSuccess();
    }

    private void createStaticHTML(Map<String, Object> map, Integer spuId){
        //新增商品时同时新增es数据 还要创建es模板

        //创建模板上下文
        Context context = new Context();
        //将所有准备的数据放到模板中
        context.setVariables(map);
        //创建文件     指定文件路径         spuId+".html"===>69.html
        File file = new File(staticHTMLPath, spuId + ".html");
        //构建文件输出流
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(file, "UTF-8");
            //根据模板生成静态文件           模板名     上下文包含数据  输出流
            templateEngine.process("item1",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            printWriter.close();
        }

    }


    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {
        Result<List<SpuDTO>> spuResult = goodsFeign.getSpuInfo(new SpuDTO());

        if (spuResult.getCode()==200) {
            List<SpuDTO> spuList = spuResult.getData();
            spuList.stream().forEach(spuDTO -> {
                this.createStaticHTMLTemplate(spuDTO.getId());
            });
        }
        return this.setResultSuccess();
    }




    private Map<String, Object> getPageInfoBySpuId(Integer spuId){

        Map<String, Object> map = new HashMap<>();
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        Result<List<SpuDTO>> spuInfoResult = goodsFeign.getSpuInfo(spuDTO);
        if(spuInfoResult.getCode()==200){
            if(spuInfoResult.getData().size()==1){
                //根据spuId查询spu信息
                SpuDTO spuInfo = spuInfoResult.getData().get(0);
                map.put("spuInfo",spuInfo);
                //spuDetail
                map.put("spuDetailInfo",this.getsSpuDetailInfo(spuId));

                //根据spuInfo.getBrandId()查询品牌信息
                Result<PageInfo<BrandEntity>> brandInfoResult = this.getBrandInfoResult(spuInfo);

                //根据 spuInfo查询到的cid查询 分类信息
                map.put("cateList",this.getCateList(spuInfo, map));
                map.put("brandInfo",this.getBrandInfo(brandInfoResult).get(0));

                //通过spuInfo.getCid3()查询规格组
                //通用参数。。。。
                map.put("groupParamsList",this.getGroupsInParams(spuInfo));

                //特有规格参数
                map.put("specParamMap",this.getSpecParamMap(spuInfo));
            }
            //sku
            Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);
            if (skuResult.getCode()==200) map.put("skuList",skuResult.getData());
        }
        return map;
    }



    private SpuDetailEntity getsSpuDetailInfo(Integer spuId){
        Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBydSpu(spuId);
        if(spuDetailResult.getCode()==200){
            SpuDetailEntity   spuDetailInfo = spuDetailResult.getData();
           return spuDetailInfo;
        }
        return null;
    }

    private Result<PageInfo<BrandEntity>> getBrandInfoResult(SpuDTO spuInfo){
        BrandDTO brandDTO = new BrandDTO();
        brandDTO.setId(spuInfo.getBrandId());
        Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDTO);
        return brandInfoResult;
    }

    private List<CategoryEntity>  getCateList(SpuDTO spuInfo, Map<String, Object> map){
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIds(
                String.join(",", Arrays.asList(spuInfo.getCid1() + "", spuInfo.getCid2() + "", spuInfo.getCid3() + ""))
        );
        if(categoryResult.getCode()==200){
            System.out.println(categoryResult.getData().size());
            List<CategoryEntity> cateList = categoryResult.getData();
            return cateList;
        }
        return null;
    }


    private List<BrandEntity> getBrandInfo(Result<PageInfo<BrandEntity>> brandInfoResult){
        if(brandInfoResult.getCode()==200){
            PageInfo<BrandEntity> data = brandInfoResult.getData();
            List<BrandEntity> brandList = data.getList();
            if(brandList.size()==1){
                return brandList;
            }
        }
        return null;
    }

    private List<SpecGroupDTO> getGroupsInParams(SpuDTO spuInfo){
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
            return groupParamsList;
        }
        return null;
    }

    private Map<Integer, String> getSpecParamMap(SpuDTO spuInfo){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuInfo.getCid3());
        specParamDTO.setGeneric(0);
        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
        if(specParamResult.getCode()==200){
            //需要将数据转换为map方便页面操作
            Map<Integer, String> specMap = new HashMap<>();
            specParamResult.getData().stream().forEach(spec-> specMap.put(spec.getId(),spec.getName()) );
           return specMap;
        }
         return null;
    }






}
