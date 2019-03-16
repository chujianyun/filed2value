package com.chujianyun.field2hash.annotation;

import java.lang.annotation.*;

/**
 * 待校验的属性
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field2Hash{
    String alias() default "";
}
