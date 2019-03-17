package com.chujianyun.field2hash.utils;

import com.chujianyun.field2hash.Cat;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Field2HashUtil测试类
 *
 * @author liuwangyangedu@163.com
 * @date 2019年03月16日
 */
public class Field2ValueUtilTest {

    private Cat cat = null;

    private String age = "age";

    @Before
    public void init() {
        // 原始属性
        cat = new Cat();
        cat.setAge(Byte.parseByte("1"));
        cat.setName("喵咪");
        cat.setNickName("tomcat");
        cat.setOwnerName("了凡");
    }

    @Test
    public void filed2hashTest() throws IllegalAccessException {

        Map<String, Integer> field2HashPair = Field2ValueUtil.getField2HashPair(cat, false);
        System.out.println("修改前" + field2HashPair);

        cat.setOwnerName("张无忌");

        Map<String, Integer> field2HashPair2 = Field2ValueUtil.getField2HashPair(cat, false);
        System.out.println("修改后" + field2HashPair2);
    }

    /**
     * 获取属性值不同的属性名
     */
    @Test
    public void getDifferentValueFieldNames() throws IllegalAccessException {

        Cat catClone = ObjectUtils.clone(cat);

        catClone.setOwnerName("张无忌");
        // 两个对象不同的属性名活别名集合
        Set<String> differentValueFieldOrAliaNames = Field2ValueUtil.getDifferentValueFieldOrAliasNames(cat, catClone, false, true);
        System.out.println(differentValueFieldOrAliaNames);
        assertEquals(differentValueFieldOrAliaNames.size(), 1);

        // 属性名或别名集合
        for (String fieldNameOrAlias : differentValueFieldOrAliaNames) {
            System.out.println(Field2ValueUtil.getValueByFieldNameOrAlias(catClone, fieldNameOrAlias));
        }

        // 属性集合
        Set<Field> fieldsByFieldOrAliasNames = Field2ValueUtil.getFieldsByFieldOrAliasNames(catClone, differentValueFieldOrAliaNames);
         System.out.println(fieldsByFieldOrAliasNames);
    }

    /**
     * 解析待注解的属性
     */
    @Test
    public void getField2HashPair() throws IllegalAccessException {
        Map<String, Integer> field2HashPair1 = Field2ValueUtil.getField2HashPair(cat, false);
        System.out.println(field2HashPair1);
        assertNull(field2HashPair1.get(age));
    }

    /**
     * 解析所有属性
     */
    @Test
    public void getField2HashPairAllFields() throws IllegalAccessException {
        Map<String, Integer> field2HashPair = Field2ValueUtil.getField2HashPair(cat, true);
        System.out.println(field2HashPair);
        assertNotEquals(field2HashPair.get(age), "1");
    }
}