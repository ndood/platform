package com.fulu.game.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 *
 */
public class BeanUtil {

    /**
     * 注册时调用，此方法用于通过原生密码生成随机盐和加密密码
     */
    public static <T> Map<String, Object> bean2Map(T bean) throws Exception {
        if (bean == null) {
            return null;
        }
        Map<String, Object> mp = new HashMap<>();
        BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();

            if (!key.equals("class")) {

                Method getter = property.getReadMethod();// Java中提供了用来访问某个属性的
                // getter/setter方法
                Object value;

                value = getter.invoke(bean);
                mp.put(key, value);
            }

        }
        return mp;
    }

}
