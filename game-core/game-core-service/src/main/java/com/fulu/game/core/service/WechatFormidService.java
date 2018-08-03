package com.fulu.game.core.service;

import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.entity.vo.WechatFormidVO;

import java.util.Date;
import java.util.List;


/**
 * 
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-11 10:35:21
 */
public interface WechatFormidService extends ICommonService<WechatFormid,Integer>{


    List<WechatFormid> findInSevenDaysFormIdByUser(Integer userId,int type);

    void  deleteNotAvailableFormIds(WechatFormid ... wechatFormid);

    /**
     * 分页查询formID
     * @param userIds
     * @param offset
     * @param size
     * @return
     */
    List<WechatFormidVO> findByUserIds(int platform,List<Integer> userIds, int offset, int size);

    /**
     * 删除过期的formID
     * @param date
     */
    void deleteByExpireTime(Date date);


    void deleteFormIds(String... fromIds);
}
