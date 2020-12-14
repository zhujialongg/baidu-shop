package com.baidu.util;

import com.baidu.entity.GoodsEntity;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class ESHightLightUtil<T> {


    //设置高亮查询字段                                          //是一个数组
    public static HighlightBuilder getHightListBulider(String ...highLightField){

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        Arrays.asList(highLightField).forEach(hlf->{

            HighlightBuilder.Field field = new HighlightBuilder.Field(hlf);

            field.preTags("<span style='color:red'>");
            field.postTags("</span>");

            highlightBuilder.field(field);
        });

        return highlightBuilder;
    }



    public  static <T> List<SearchHit<T>> getHightLightHit(List<SearchHit<T>> list){


        List<SearchHit<T>> collect = list.stream().map(hit -> {

            Map<String, List<String>> highlightFields = hit.getHighlightFields();

            highlightFields.forEach((key, value) -> {

                System.out.println(key + "" + value);
                T content = hit.getContent();
                try {
                    Method method = content.getClass().getMethod("set" + String.valueOf(key.charAt(0)).toUpperCase() + key.substring(1), String.class);

                    //执行set方法 并赋值
                    method.invoke(content, value.get(0));

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            });
            return hit;
        }).collect(Collectors.toList());

        return collect;

    }






}
