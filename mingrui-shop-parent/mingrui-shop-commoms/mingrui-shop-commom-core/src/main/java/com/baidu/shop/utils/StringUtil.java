package com.baidu.shop.utils;

/**
 * @ClassName
 * @Description //TODO
 * @Author zhujialong
 * @Date
 * @Version V1.0
 **/
public class StringUtil {


    //判断 字符串类型  不为空字符串 且不为null
    public static Boolean isNotEmpty(String str){

        return null !=str && !"".equals(str);
    }

    //判断 字符串类型 诶null 或为 空字符串
    public static Boolean isEmpty(String str){

        return null ==str || "".equals(str);
    }

    //字符转转  Integer方法
    public static Integer toInteger(String str){

        if(isNotEmpty(str)) return Integer.parseInt(str); //字符串不为空 就转换

        return 0;  //否则 返回个 0
    }


}
