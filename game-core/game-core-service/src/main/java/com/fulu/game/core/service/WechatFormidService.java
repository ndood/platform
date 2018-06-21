package com.fulu.game.core.service;

import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.entity.vo.WechatFormidVO;

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


    /**
     * 批量查询用户的formID和openId
     * @param userIds  用户ID列表,如果为NULL则查询全部用户的formId
     * @return
     */
    List<WechatFormidVO> findByUserId(List<Integer> userIds);


    void deleteFormIds(String... fromIds);
}
