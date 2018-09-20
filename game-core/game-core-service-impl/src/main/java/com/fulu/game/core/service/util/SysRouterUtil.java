package com.fulu.game.core.service.util;

import com.fulu.game.core.entity.SysRouter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: shijiaoyun.
 * @Date: 2018/9/20 17:18.
 * @Description: router工具类
 */
public class SysRouterUtil {

    /**
     * 将Router的集合转换为tree格式
     * @param list
     * @return
     */
    public static List<SysRouter> formatRouter(List<SysRouter> list) {
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<SysRouter> resultList = new ArrayList<>();
        for (SysRouter router1 : list) {
            boolean flag = false;
            for (SysRouter router2 : list) {
                if (router1.getPid() != null) {
                    if (router1.getPid().equals(router2.getId())) {
                        flag = true;
                        if (router2.getChild() == null) {
                            router2.setChild(new ArrayList<SysRouter>());
                        }
                        router2.getChild().add(router1);
                        break;
                    }
                }
            }
            if (!flag) {
                resultList.add(router1);
            }
        }
        return resultList;
    }


}
