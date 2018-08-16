package com.fulu.game.app.util;

import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class WebUtils {

    public static <T> T request2Bean(HttpServletRequest request, Class<T> beanClass){

        try{
            //实例化传进来的类型
            T t = beanClass.newInstance();
            //之前使用request.getParameter("name")根据表单中的name值获取value值
            //request.getParameterMap()方法不需要参数，返回结果为Map<String,String[]>，也是通过前台表单中的name值进行获取
            Map map = request.getParameterMap();
            //将Map中的值设入bean中，其中Map中的key必须与目标对象中的属性名相同，否则不能实现拷贝
            BeanUtils.populate(t, map);
            return t;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
