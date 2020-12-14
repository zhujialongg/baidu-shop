package com.baidu.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.dto.SkuDTO;
import com.baidu.dto.SpecParamDTO;
import com.baidu.dto.SpuDTO;
import com.baidu.entity.BrandEntity;
import com.baidu.entity.CategoryEntity;
import com.baidu.entity.SpuDetailEntity;
import com.baidu.feign.BrandFeign;
import com.baidu.feign.CategoryFeign;
import com.baidu.feign.SpecificationFeign;
import com.baidu.response.GoodsResponse;
import com.baidu.base.BaseApiService;
import com.baidu.base.Result;
import com.baidu.document.GoodsDoc;
import com.baidu.entity.SpecParamEntity;
import com.baidu.feign.GoodsFeign;
import com.baidu.service.ShopElasticsearchService;
import com.baidu.status.HTTPStatus;
import com.baidu.utils.ESHighLightUtil;
import com.baidu.utils.JSONUtil;
import com.baidu.utils.StringUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
@RestController
public class ShopElasticsearchServiceImpl extends BaseApiService  implements ShopElasticsearchService {

    @Resource
    private GoodsFeign goodsFeign;

    @Resource
    private SpecificationFeign specificationFeign;

    @Resource
    private BrandFeign brandFeign;

    @Resource
    private CategoryFeign categoryFeign;

    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Override
    public Result<JSONObject> delData(Integer spuId) {
        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> saveData(Integer spuId) {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
        elasticsearchRestTemplate.save(goodsDocs);

        return this.setResultSuccess();
    }

    @Override
    public GoodsResponse search(String search, Integer page, String filter) {

        if(StringUtil.isEmpty(search)) throw new RuntimeException("搜索内容不能为空");
        NativeSearchQueryBuilder queryBuilder = this.getSearchQueryBuilder(search, page,filter);
        //查询
        SearchHits<GoodsDoc> hits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(hits.getSearchHits());
        List<GoodsDoc> goodsDocList = highLightHit.stream().map(searchHit -> searchHit.getContent()).collect(Collectors.toList());

        //分页 总条数 页数
        long total = hits.getTotalHits();
        //先将total装箱  然后拆箱成 double类型 为了 /10后的结果为  向上取整后后的double类型
        long totalPage = Double.valueOf(Math.ceil(Long.valueOf(hits.getTotalHits()).doubleValue() / 10)).longValue();

        //聚合  Terms为Aggregation实现类 有 getBuckets()  通过 es的查询对象hit获得getAggregations()
        Aggregations aggregations = hits.getAggregations();

        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(aggregations);
        List<CategoryEntity>  categoryList = null;
        Integer hotCid = 0;
        for(Map.Entry<Integer, List<CategoryEntity>> entry : map.entrySet()){
            hotCid = entry.getKey();
            categoryList = entry.getValue();
        }
        //通过cid查询 规格参数
        Map<String, List<String>> specParamMap = this.getSpecParam(hotCid, search);
        //获取品牌集合
        List<BrandEntity> brandList = this.getBrandList(aggregations);

        GoodsResponse goodsResponse = new GoodsResponse(total, totalPage, brandList, categoryList, goodsDocList,specParamMap);

        return goodsResponse;
    }
     

    private Map<String, List<String>> getSpecParam(Integer hotCid,String search){
        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(1); //只搜索有查询属性的规格参数

        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);

        if(specParamResult.getCode()==200){
            List<SpecParamEntity> specParamList  = specParamResult.getData();
            //聚合查询
            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
            queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

            //分页必须得查询一条数据
            queryBuilder.withPageable(PageRequest.of(0,1));

            specParamList.stream().forEach(specParam->{
                queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs."+specParam.getName()+".keyword"));
            });

            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsDoc.class);
            //装 规格参数名  和  规格参数
            Map<String, List<String>> map = new HashMap<>();

            Aggregations aggregations = searchHits.getAggregations();

            specParamList.stream().forEach(specParam->{
                Terms terms = aggregations.get(specParam.getName());
                List<? extends Terms.Bucket> buckets = terms.getBuckets();

                List<String> valueList = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

                map.put(specParam.getName(),valueList);
            });
            return map;
        }
        //成功就返回 map  否则 返回null
        return null;
    }



    //拆分 构建查询对象
    private NativeSearchQueryBuilder getSearchQueryBuilder(String search, Integer page,String filter){
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //过滤搜索
        if(StringUtil.isNotEmpty(filter) && filter.length() >2){ //因为有"{}"
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            filterMap.forEach((key,value)->{
                MatchQueryBuilder matchQueryBuilder = null;

                //品牌分类  和规格参数的查询方式不一样
                if(key.equals("cid3")||key.equals("brandId")){
                      matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else {
                    matchQueryBuilder =  QueryBuilders.matchQuery("specs." + key + ".keyword",value);
                }
                boolQueryBuilder.must(matchQueryBuilder); //将两个 查询 and起来
            });
            queryBuilder.withFilter(boolQueryBuilder); //过滤 查询字段
        }
        //多字段查询
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));
        //聚合 分类  品牌
        queryBuilder.addAggregation(AggregationBuilders.terms("cid_agg").field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId"));

        //设置高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));
        //分页
        queryBuilder.withPageable(PageRequest.of(page-1,10));
        return queryBuilder;
    }

    private List<BrandEntity> getBrandList(Aggregations aggregations){
        Terms brand_agg = aggregations.get("brand_agg");
        // 品牌
        List<? extends Terms.Bucket> brandIdBucketsList = brand_agg.getBuckets();
        List<String> brandIdList = brandIdBucketsList.stream().map(brandIdBucket -> {
            Number keyAsNumber = brandIdBucket.getKeyAsNumber();
            return keyAsNumber + "";
        }).collect(Collectors.toList());
        String brandIdStr = String.join(",", brandIdList);
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIds(brandIdStr);

        return brandResult.getData();
    }

    private  Map<Integer, List<CategoryEntity>>  getCategoryList(Aggregations aggregations){
        Terms cid_agg = aggregations.get("cid_agg");
        List<? extends Terms.Bucket> cidBucketList = cid_agg.getBuckets();

        //热度最高的  cid
       List<Integer> hotCidList = Arrays.asList(0);
        //maxCount 最多  热度最高
       List<Long> maxCount = Arrays.asList(0L);

       //实例化map  返回 List<CategoryEntity> 和 热度   cid
        Map<Integer, List<CategoryEntity>> map = new HashMap<>();

        //分类 StringBuffer  ？？
        List<String> cidList = cidBucketList.stream().map(cidBucket -> {
            Number keyAsNumber = cidBucket.getKeyAsNumber();

            if(cidBucket.getDocCount()>maxCount.get(0)){
                maxCount.set(0,cidBucket.getDocCount());
                hotCidList.set(0,keyAsNumber.intValue());  //cid
            }
            //也可以 写 cidBucket.getKeyAsString()
            return keyAsNumber.intValue() + "";
        }).collect(Collectors.toList());
        String cidStr = String.join(",", cidList);
        Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIds(cidStr);

       map.put(hotCidList.get(0),categoryResult.getData());

        return map;
    }


    @Override
    public Result<JSONObject> initGoodsEsData() {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);

           if(!indexOperations.exists()){
               //创建索引
               indexOperations.create();
               //创建映射
               indexOperations.createMapping();
           }
           //批量新增数据
            SpuDTO spuDTO = new SpuDTO();
           List<GoodsDoc> goodsDocs = this.esGoodsInfo(spuDTO);
           elasticsearchRestTemplate.save(goodsDocs);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> clearGoodsEsData() {

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsDoc.class);

       if(indexOperations.exists()){
           //删除索引
           indexOperations.delete();
       }
        return this.setResultSuccess();
    }

    //不对外开放
    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {

        //扩大 goodsDocs 作用域
        List<GoodsDoc> goodsDocs = new ArrayList<>();

        Result<List<SpuDTO>> spuInfo = goodsFeign.getSpuInfo(spuDTO);

        if(spuInfo.getCode()== HTTPStatus.OK){

            //差查询出多个 spu
         //   List<GoodsDoc> goodsDocs = new ArrayList<>();

            //spu数据
            List<SpuDTO> spuList = spuInfo.getData();

            spuList.stream().forEach(spu->{

                GoodsDoc goodsDoc = new GoodsDoc();

                goodsDoc.setId(spu.getId().longValue());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setBrandId(spu.getBrandId().longValue());
                goodsDoc.setCategoryName(spu.getCategoryName());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setCreateTime(spu.getCreateTime());

                //通过spuid查询sku
                Map<List<Long>, List<Map<String, Object>>> skus = this.getSkusAndPriceList(spu.getId());
                skus.forEach((key,value)->{
                    // 装价格 和 sku
                    goodsDoc.setPrice(key);
                    goodsDoc.setSkus(JSONUtil.toJsonString(value));
                });

                 //通过 cid查询规格参数
                Map<String, Object> specMap = this.getSpecMap(spu);

                goodsDoc.setSpecs(specMap);
                //最后放到 goodsDocs
                goodsDocs.add(goodsDoc);

            });
            System.out.println(goodsDocs);
        }

        return goodsDocs;
    }


    private Map<List<Long>,List<Map<String,Object>>> getSkusAndPriceList(Integer spuId){

        //装 价格 和 sku
        Map<List<Long>,List<Map<String,Object>>> hashMap = new HashMap<>();

        Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spuId);

        //扩大priceList  skuMapList 作用域
        List<Long> priceList = new ArrayList<>();
        List<Map<String, Object>> skuMapList = null;

        if(skuResult.getCode() == HTTPStatus.OK){

            List<SkuDTO> skuList = skuResult.getData();
           //扩大priceList  skuMapList
           //  ArrayList<Long> priceList = new ArrayList<>();
           //  List<Map<String, Object>> skuMapList = null;
            skuMapList = skuList.stream().map(sku -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", sku.getId());
                map.put("title", sku.getTitle());
                map.put("images", sku.getImages());
                map.put("price", sku.getPrice());

                priceList.add(sku.getPrice().longValue());

                return map;
            }).collect(Collectors.toList());
            //装价格 和 sku
//            goodsDoc.setPrice(priceList);
//            goodsDoc.setSkus(JSONUtil.toJsonString(skuMapList));
        }
        hashMap.put(priceList,skuMapList);
        return hashMap;
    };


    private Map<String,Object> getSpecMap(SpuDTO spuDTO){

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(spuDTO.getCid3());

        Result<List<SpecParamEntity>> specParamResult = specificationFeign.getSpecParamInfo(specParamDTO);
        //扩大作用域
        HashMap<String, Object> specMap = new HashMap<>();

        if(specParamResult.getCode() == HTTPStatus.OK){
            //只有规格参数的id 和 名字
            List<SpecParamEntity> paramList = specParamResult.getData();
            //通过 spu id 查询 spu detail
            Result<SpuDetailEntity> spuDetaildResult = goodsFeign.getSpuDetailBydSpu(spuDTO.getId());

            if(spuDetaildResult.getCode() == HTTPStatus.OK){
                // 因为 spu spuDetail
                SpuDetailEntity spuDetaiInfo  = spuDetaildResult.getData();

                //通用规格参数
                String genericSpecStr = spuDetaiInfo.getGenericSpec();
                // 将 genericSpecStr 转换为 map的value  String类型
                Map<String, String> genericSpecMap  = JSONUtil.toMapValueString(genericSpecStr);

                //特有规格参数
                String specialSpec = spuDetaiInfo.getSpecialSpec();
                Map<String, List<String>> specialSpecMap  = JSONUtil.toMapValueStrList(specialSpec);


                paramList.stream().forEach(param->{

                    if(param.getGeneric()==1){

                        if(param.getNumeric()==1 && param.getSearching()==1){
                            specMap.put(param.getName(),this.chooseSegment(genericSpecMap.get(param.getId()+""),param.getSegments(),param.getUnit()));

                        }else {
                            specMap.put(param.getName(),specialSpecMap.get(param.getId() + ""));
                        }
                    }else{
                        specMap.put(param.getName(),specialSpecMap.get(param.getId() + ""));
                    }
                });
            }
        }
        return specMap;
    }


    /**
     * 把具体的值转换成区间-->不做范围查询
     * @param value
     * @param segments
     * @param unit
     * @return
     */
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }


}
