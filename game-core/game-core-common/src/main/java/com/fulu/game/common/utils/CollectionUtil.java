package com.fulu.game.common.utils;


import cn.hutool.core.bean.BeanUtil;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {



    public static  <T,K extends T> List<K>   copyNewCollections(List<T> origList,Class<K> clazz){
        try {
            List<K> result = new ArrayList<>();
            for(T t: origList){
                K a = clazz.newInstance();
                BeanUtil.copyProperties(t,a);
                result.add(a);
            }
            return result;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
