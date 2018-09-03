package com.fulu.game.core.service;

import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.fulu.game.core.service.ICommonService;
import com.github.pagehelper.PageInfo;

import java.util.List;


/**
 * @author wangbin
 * @email ${email}
 * @date 2018-06-06 10:29:12
 */
public interface PushMsgService extends ICommonService<PushMsg, Integer> {

    /**
     * 查询推送消息列表
     *
     * @param pageNum
     * @param pageSize
     * @param platform
     * @param orderBy
     * @return
     */
    PageInfo<PushMsg> list(Integer pageNum, Integer pageSize, Integer platform, String orderBy);

    /**
     * 查找今天没有推送的微信消息
     *
     * @return
     */
    List<PushMsg> findTodayNotPushMsg();


    void hitsStatistics(int pushId);

    PageInfo<PushMsg> officialNoticeList(Integer pageNum, Integer pageSize);

    /**
     * 获取最新的一条官方公告
     *
     * @return 官方公告Bean
     */
    PushMsg newOfficialNotice();
}
