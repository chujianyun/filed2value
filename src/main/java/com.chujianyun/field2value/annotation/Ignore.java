package com.chujianyun.field2value.annotation;

import java.lang.annotation.*;

/**
 * 需要忽略的属性
 *
 * @author liuwangyangedu@163.com
 * @date: 2019-04-08 10:15
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Ignore {
}
