package com.baidu.shop.utils;

import org.apache.commons.lang.StringUtils;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public class ObjectUtils {


    public static String toString(Object obj){

        if(obj == null){
            return null;
        }
        return obj.toString();
    }

    public static Integer  toInteger(Object obj){
        if(obj == null){
            return 0;
        }

        if(obj instanceof Double || obj instanceof Float){
            return Integer.valueOf(StringUtils.substringBefore(obj.toString(),"."));
        }
        if (obj instanceof Number) {
            return Integer.valueOf(obj.toString());
        }
        if (obj instanceof String) {
            return Integer.valueOf(obj.toString());
        } else {
            return 0;
        }
    }


}
