package com.fulu.game.core.service;

import com.fulu.game.core.entity.WechatFormid;

import java.util.List;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-11 10:35:21
 */
public interface WechatFormidService extends ICommonService<WechatFormid,Integer>{


    List<WechatFormid> findInSevenDaysFormIdByUser(Integer userId);

    void  deleteNotAvailableFormIds(WechatFormid ... wechatFormid);
}
