package com.fulu.game.core.service;

import com.fulu.game.core.entity.DynamicLike;
import com.fulu.game.core.entity.DynamicPushMsg;
import com.github.pagehelper.PageInfo;


/**
 * 动态Push消息推送表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-11 10:24:20
 */
public interface DynamicPushMsgService extends ICommonService<DynamicPushMsg,Integer>{

    /**
     * 获取消息推送记录接口
     * @param pageNum
     * @param pageSize
     * @return
     */
    PageInfo<DynamicPushMsg> list(Integer pageNum, Integer pageSize);
}
