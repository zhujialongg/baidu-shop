package com.baidu.shop.comment;

import java.lang.annotation.*;

/**
 * @ClassName {name}
 * @Description: TODO
 * @Author zhujialong
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Target(ElementType.METHOD) //Target：用于描述注解作用的范围 method：可以作用在方法上
@Retention(RetentionPolicy.RUNTIME) //runtime表示注解在那个阶段执行
@Documented
public @interface LogAnnotation {

    String operationModel() default ""; // 操作模块

    String operationType() default "";  // 操作类型/操作方法

    String operation() default "";  // 具体操作


}
