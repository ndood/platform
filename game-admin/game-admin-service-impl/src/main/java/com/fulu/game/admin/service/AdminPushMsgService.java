package com.fulu.game.admin.service;

import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;
import com.fulu.game.core.service.PushMsgService;

public interface AdminPushMsgService extends PushMsgService{



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

}
