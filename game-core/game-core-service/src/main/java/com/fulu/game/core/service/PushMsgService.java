package com.fulu.game.core.service;

import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * @author wangbin
 * @email ${email}
 * @date 2018-06-06 10:29:12
 */
public interface PushMsgService extends ICommonService<PushMsg, Integer> {

    /**
     * 创建推送微信消息记录
     *
     * @param pushMsgVO
     */
    void push(PushMsgVO pushMsgVO);

    /**
     * 指定时间执行微信消息
     *
     * @param pushMsg
     */
    void appointPush(PushMsg pushMsg);

    /**
     * 查询推送消息列表
     *
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    PageInfo<PushMsg> list(int pageNum, int pageSize, String orderBy);

    /**
     * 查找今天没有推送的微信消息
     *
     * @return
     */
    List<PushMsg> findTodayNotPushMsg();


    void hitsStatistics(int pushId);

    PageInfo<PushMsg> officialNoticeList(Integer pageNum, Integer pageSize);
}
