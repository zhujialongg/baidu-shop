package com.baidu.test;

import com.baidu.RunTestEsApplication;
import com.baidu.entity.GoodsEntity;
import com.baidu.resp.GoodsEsRespository;
import com.baidu.util.ESHightLightUtil;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Max;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
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
//让测试在 Spring容器环境下执行
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RunTestEsApplication.class})
public class TestEs {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private GoodsEsRespository goodsEsRepository;

    @Test
    public void createGoodsIndex(){

        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(IndexCoordinates.of("goods"));
        //indexOperations.create();

        System.out.println(indexOperations.exists()?"创建索引成功":"创建索引失败");
    }

    @Test
    public void createGoodsMapping(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);

        System.out.println(indexOperations.exists()?"创建成功":"创建失败");

    }

    @Test
    public void deleteGoodsIndex(){
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(GoodsEntity.class);

        indexOperations.delete();

        System.out.println("删除索引成功");
    }



    //新增
    @Test
    public void saveData(){
        GoodsEntity entity = new GoodsEntity();
        entity.setId(1L);
        entity.setBrand("小米");
        entity.setCategory("手机");
        entity.setImage("xiaomi.jpg");
        entity.setPrice(1000D);
        entity.setTitle("小米3");

        goodsEsRepository.save(entity);

        System.out.println("新增成功");

    }


    @Test
    public void  saveBatch(){

        GoodsEntity entity = new GoodsEntity();
        entity.setId(2L);
        entity.setBrand("苹果果");
        entity.setCategory("手机");
        entity.setImage("pingguo.jpg");
        entity.setPrice(5000D);
        entity.setTitle("iphone11手机");

        GoodsEntity entity2 = new GoodsEntity();
        entity2.setId(3L);
        entity2.setBrand("三星");
        entity2.setCategory("手机");
        entity2.setImage("sanxing.jpg");
        entity2.setPrice(3000D);
        entity2.setTitle("w2019手机");

        GoodsEntity entity3 = new GoodsEntity();
        entity3.setId(4L);
        entity3.setBrand("华为");
        entity3.setCategory("手机");
        entity3.setImage("huawei.jpg");
        entity3.setPrice(4000D);
        entity3.setTitle("华为mate30手机");


        //Iterator是个集合
        goodsEsRepository.saveAll(Arrays.asList(entity,entity2,entity3));
        System.out.println("批量新增成功");
    }

    @Test
    public void searchAll(){

        long count = goodsEsRepository.count();
        System.out.println("总条数："+count);

        //查询所有数据
        Iterable<GoodsEntity> all = goodsEsRepository.findAll();
        all.forEach(goods -> System.out.println(goods));

    }

    //条件查询
    @Test
    public void  searchAllParams(){

        List<GoodsEntity> title = goodsEsRepository.findAllByBrand("小米");
        System.out.println(title);

        System.out.println("================");


        List<GoodsEntity> list = goodsEsRepository.findByAndPriceBetween(3000D, 5000D);
        System.out.println(list);

    }

    @Test
     public void customizeSearch(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();


        NativeSearchQueryBuilder nativeSearchQueryBuilder = queryBuilder.withQuery(
                QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("title", "华为手机"))
        );

        System.out.println(queryBuilder);
        //排序
        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        //分页
        queryBuilder.withPageable(PageRequest.of(0,10));


        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

         search.getSearchHits().stream().forEach(hit->{
             System.out.println(hit.getContent());
         });

    }

    //高亮
    @Test
    public void hightList(){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //设置高亮
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        HighlightBuilder.Field title = new HighlightBuilder.Field("title");
//        title.preTags("<span style='color:red'>");
//        title.postTags("</span>");
//        highlightBuilder.field(title);
//
//        queryBuilder.withHighlightBuilder(highlightBuilder);

        //调用工具类  设置高亮
       queryBuilder.withHighlightBuilder(ESHightLightUtil.getHightListBulider("title"));


        queryBuilder.withQuery(QueryBuilders.boolQuery()
        .must(QueryBuilders.matchQuery("title","小米手机")));

        queryBuilder.withSort(SortBuilders.fieldSort("price").order(SortOrder.DESC));

        queryBuilder.withPageable(PageRequest.of(0,2));

        SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

        List<SearchHit<GoodsEntity>> searchHits = search.getSearchHits();

        List<SearchHit<GoodsEntity>> hightLightHit = ESHightLightUtil.getHightLightHit(searchHits);
//        List<SearchHit<GoodsEntity>> title1 = search.getSearchHits().stream().map(hit -> {
//
//            Map<String, List<String>> highlightFields = hit.getHighlightFields();
//            hit.getContent().setTitle(highlightFields.get("title").get(0));
//            System.out.println(highlightFields.get("title").get(0));
//            return hit;
//
//        }).collect(Collectors.toList());

        System.out.println(hightLightHit);

    }

        @Test
        public void searchAgg(){

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

            queryBuilder.addAggregation(
                    AggregationBuilders.terms("brand_agg").field("brand")
            );

            SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

            Aggregations aggregations = search.getAggregations();


            Terms terms = aggregations.get("brand_agg");

            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            buckets.forEach(bucket -> {

                System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());
            });
            System.out.println(search);

        }

        @Test
        public  void searchAggMethod(){

            NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

            queryBuilder.addAggregation(
                    AggregationBuilders.terms("brand_agg")
                    .field("brand")
                    //聚合函数
                    .subAggregation(AggregationBuilders.max("max_price").field("price"))
            );

            SearchHits<GoodsEntity> search = elasticsearchRestTemplate.search(queryBuilder.build(), GoodsEntity.class);

            Aggregations aggregations = search.getAggregations();

            Terms terms = aggregations.get("brand_agg");

            List<? extends Terms.Bucket> buckets = terms.getBuckets();
            buckets.forEach(bucket -> {

                System.out.println(bucket.getKeyAsString()+":"+bucket.getDocCount());

                //获取聚合
                Aggregations aggregations1 = bucket.getAggregations();
                //得到map
                Map<String, Aggregation> map = aggregations1.asMap();

                Max max_price = (Max) map.get("max_price");

                System.out.println(max_price.getValue());

            });


        }






}
