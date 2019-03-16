package com.chujianyun.field2hash.annotation;

import java.lang.annotation.*;

/**
 * 待校验的属性
 * 允许指定别名
 *
 * @author liuwangyangedu@163.com
 * @date 2019年03月16日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field2Hash {
    String alias() default "";
}
