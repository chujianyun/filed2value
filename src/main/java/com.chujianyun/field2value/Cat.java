package com.chujianyun.field2value;

import com.chujianyun.field2value.annotation.Field2Value;
import lombok.Data;

/**
 * Cat测试实体
 *
 * @author liuwangyangedu@163.com
 * @date 2019年03月16日
 */
@Data
public class Cat implements Cloneable {
    private String name;

    private Byte age;

    @Field2Value(alias = "nick")
    private String nickName;

    @Field2Value
    private String ownerName;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
