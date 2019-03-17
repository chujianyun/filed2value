package com.chujianyun.field2value.utils;

import com.chujianyun.field2value.annotation.Field2Value;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 对象属性名到其值的映射工具
 * 可用来
 *
 * @author liuwangyangedu@163.com
 * @date 2019年03月16日
 */
public class Field2ValueUtil {


    /**
     * 根据对象和属性名+别名的集合获取属性集合
     *
     * @param object            待解析的对象
     * @param fieldOrAliasNames 属性名或者别名的集合
     * @return 属性集合
     */
    public static Set<Field> getFieldsByFieldOrAliasNames(Object object, Set<String> fieldOrAliasNames) {
        if (object == null || fieldOrAliasNames == null || fieldOrAliasNames.isEmpty()) {
            return new HashSet<>(0);
        }

        Set<Field> fields2get = new HashSet<>(fieldOrAliasNames.size());
        Class<?> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            // 带注解
            if (field.isAnnotationPresent(Field2Value.class)) {
                Field2Value annotation = field.getAnnotation(Field2Value.class);
                String alias = annotation.alias();
                if (fieldOrAliasNames.contains(alias) || fieldOrAliasNames.contains(field.getName())) {
                    fields2get.add(field);
                    break;
                }
            } else {
                if (fieldOrAliasNames.contains(field.getName())) {
                    fields2get.add(field);
                }
            }
        }
        return fields2get;
    }

    /**
     * 根据属性的名称或者别名的名称获取属性的值
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
            if (field.isAnnotationPresent(Field2Value.class)) {
                Field2Value annotation = field.getAnnotation(Field2Value.class);
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
    public static Set<String> getDifferentValueFieldOrAliasNames(Object object1, Object object2, boolean resolveAllField, boolean onlyCompareCommonFields) throws IllegalAccessException {

        Map<String, Object> field2ValuePair1 = getField2ValuePair(object1, resolveAllField);
        Set<String> keySet1 = field2ValuePair1.keySet();
        Map<String, Object> field2ValuePair2 = getField2ValuePair(object2, resolveAllField);
        Set<String> keySet2 = field2ValuePair2.keySet();

        if (keySet1.isEmpty()) {
            return keySet2;
        }

        if (keySet2.isEmpty()) {
            return keySet1;
        }

        Set<String> fieldsWithDifferentValue = new HashSet<>();

        // 只比较公共属性
        for (Map.Entry<String, Object> entry : field2ValuePair1.entrySet()) {
            String fieldName = entry.getKey();
            Object value1 = entry.getValue();

            Object value2 = field2ValuePair2.get(fieldName);

            boolean sameHashCode = (value1.hashCode() == value2.hashCode());
            boolean sameObject = value1.equals(value2);
            if (!(sameHashCode && sameObject)) {
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
     * 获取属性及其对应值得hash值（可能有hash冲突，谨慎使用）
     *
     * @param resolveAllField 解析所有属性
     * @return 属性--> 值hash
     */
    public static <T> Map<String, Integer> getField2HashPair(T object, boolean resolveAllField) throws IllegalAccessException {

        if (object == null) {
            return new HashMap<>(0);
        }

        Map<String, Object> field2ValuePair = getField2ValuePair(object, resolveAllField);
        Map<String, Integer> field2hashPairMap = new HashMap<>(field2ValuePair.size());

        field2ValuePair.forEach((key, value) -> field2hashPairMap.put(key, value.hashCode()));
        return field2hashPairMap;
    }

    /**
     * 获取属性及其对应值的映射（推荐使用）
     *
     * @param resolveAllField 解析所有属性
     * @return 属性--> 值hash
     */
    public static <T> Map<String, Object> getField2ValuePair(T object, boolean resolveAllField) throws IllegalAccessException {

        if (object == null) {
            return new HashMap<>(0);
        }

        Class<?> clazz = object.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        Map<String, Object> field2hashMap = new HashMap<>(declaredFields.length);
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String key = field.getName();

            if (resolveAllField) {
                field2hashMap.put(key, field.get(object));
                continue;
            }

            if (field.isAnnotationPresent(Field2Value.class)) {
                Field2Value annotation = field.getAnnotation(Field2Value.class);
                String alias = annotation.alias();
                if (!"".equals(alias)) {
                    key = alias;
                }
                field2hashMap.put(key, field.get(object));
            }
        }
        return field2hashMap;
    }

}
