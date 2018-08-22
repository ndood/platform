package com.fulu.game.app.util;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class WebUtils {


    /**
     * 将request中的参数转换成对象
     * @param request
     * @param beanClass
     * @param <T>
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    public static <T> T request2Bean(HttpServletRequest request, Class<T> beanClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        //实例化传进来的类型
        T bean = beanClass.newInstance();
        //之前使用request.getParameter("name")根据表单中的name值获取value值
        //request.getParameterMap()方法不需要参数，返回结果为Map<String,String[]>，也是通过前台表单中的name值进行获取
        Map map = request.getParameterMap();
        //将Map中的值设入bean中，其中Map中的key必须与目标对象中的属性名相同，否则不能实现拷贝
        BeanUtils.populate(bean, map);
        return bean;
    }
}
