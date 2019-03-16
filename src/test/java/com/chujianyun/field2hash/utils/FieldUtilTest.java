package com.chujianyun.field2hash.utils;

import com.chujianyun.field2hash.Cat;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class FieldUtilTest {

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

        Map<String, Integer> field2HashPair = FieldUtil.getField2HashPair(cat, false);
        System.out.println("修改前" + field2HashPair);

        cat.setOwnerName("张无忌");

        Map<String, Integer> field2HashPair2 = FieldUtil.getField2HashPair(cat, false);
        System.out.println("修改后" + field2HashPair2);
    }

    /**
     * 获取属性值不同的属性名
     *
     * @throws IllegalAccessException
     */
    @Test
    public void getDifferentValueFieldNames() throws IllegalAccessException {

        Cat catClone = ObjectUtils.clone(cat);

        catClone.setOwnerName("张无忌");
        Set<String> differentValueFieldNames = FieldUtil.getDifferentValueFieldNames(cat, catClone, false, true);
        System.out.println(differentValueFieldNames);
        assertEquals(differentValueFieldNames.size(), 1);
    }

    /**
     * 解析待注解的属性
     */
    @Test
    public void getField2HashPair() throws IllegalAccessException {
        Map<String, Integer> field2HashPair1 = FieldUtil.getField2HashPair(cat, false);
        System.out.println(field2HashPair1);
        assertNull(field2HashPair1.get(age));
    }

    /**
     * 解析所有属性
     */
    @Test
    public void getField2HashPairAllFields() throws IllegalAccessException {
        Map<String, Integer> field2HashPair = FieldUtil.getField2HashPair(cat, true);
        System.out.println(field2HashPair);
        assertNotEquals(field2HashPair.get(age), "1");
    }
}