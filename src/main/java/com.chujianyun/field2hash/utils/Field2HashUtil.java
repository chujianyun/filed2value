package com.chujianyun.field2hash.utils;

import com.chujianyun.field2hash.annotation.Field2Hash;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 对象属性名到其值得hash值映射工具
 *
 * @author liuwangyangedu@163.com
 * @date 2019年03月16日
 */
public class Field2HashUtil {

    /**
     * 根据对象和属性到值得哈希映射map，获取指定key的值
     *
     * @param object           对象
     * @param fieldNameOrAlias 属性名或别名
     * @return 该属性的值
     */
    public static Object getValueByFieldNameOrAlias(Object object, String fieldNameOrAlias) throws IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();

        Field field2resolve = null;
        for (Field field : declaredFields) {
            // 直接属性名相同
            if (field.getName().equals(fieldNameOrAlias)) {
                field2resolve = field;
                break;
            }
            // 别名相同
            if (field.isAnnotationPresent(Field2Hash.class)) {
                Field2Hash annotation = field.getAnnotation(Field2Hash.class);
                String alias = annotation.alias();
                if (!"".equals(alias) && alias.equals(fieldNameOrAlias)) {
                    field2resolve = field;
                    break;
                }
            }

        }
        if (field2resolve != null) {
            field2resolve.setAccessible(true);
            return field2resolve.get(object);
        }
        return null;
    }


    /**
     * 获取两个对象属性的值不同的所有属性名称
     *
     * @param object1                 第一个对象
     * @param object2                 第二个对象
     * @param onlyCompareCommonFields 设计费
     * @return 属性的值不同的所有属性名称
     */
    public static Set<String> getDifferentValueFieldNames(Object object1, Object object2, boolean resolveAllField, boolean onlyCompareCommonFields) throws IllegalAccessException {

        Map<String, Integer> field2HashPair1 = getField2HashPair(object1, resolveAllField);
        Set<String> keySet1 = field2HashPair1.keySet();
        Map<String, Integer> field2HashPair2 = getField2HashPair(object2, resolveAllField);
        Set<String> keySet2 = field2HashPair2.keySet();

        if (keySet1.isEmpty()) {
            return keySet2;
        }

        if (keySet2.isEmpty()) {
            return keySet1;
        }

        Set<String> fieldsWithDifferentValue = new HashSet<>();
        // 只比较公共属性

        for (Map.Entry<String, Integer> entry : field2HashPair1.entrySet()) {
            String fieldName = entry.getKey();
            Integer hashCode = entry.getValue();

            Integer hashCode2 = field2HashPair2.get(fieldName);
            if (!hashCode.equals(hashCode2)) {
                fieldsWithDifferentValue.add(fieldName);
            }
        }

        // 不相同的fields
        if (!onlyCompareCommonFields) {
            Set<String> keySet1Copy = new HashSet<>(keySet1);
            Set<String> keySet2Copy = new HashSet<>(keySet2);
            keySet1.removeAll(keySet2);
            keySet2Copy.removeAll(keySet1Copy);

            fieldsWithDifferentValue.addAll(keySet1);
            fieldsWithDifferentValue.addAll(keySet2Copy);
        }
        return fieldsWithDifferentValue;
    }

    /**
     * 获取属性及其对应值得hash值
     *
     * @param resolveAllField 解析所有属性
     * @return 属性--> 值hash
     */
    public static <T> Map<String, Integer> getField2HashPair(T object, boolean resolveAllField) throws IllegalAccessException {

        if (object == null) {
            return new HashMap<>(0);
        }

        Class<?> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Map<String, Integer> field2hashMap = new HashMap<>(declaredFields.length);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String key = field.getName();

            if (resolveAllField) {
                field2hashMap.put(key, field.get(object).hashCode());
                continue;
            }

            if (field.isAnnotationPresent(Field2Hash.class)) {
                Field2Hash annotation = field.getAnnotation(Field2Hash.class);
                String alias = annotation.alias();
                if (!"".equals(alias)) {
                    key = alias;
                }
                field2hashMap.put(key, field.get(object).hashCode());
            }
        }
        return field2hashMap;
    }


}
