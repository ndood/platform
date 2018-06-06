package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-06 10:29:12
 */
@Mapper
public interface PushMsgDao extends ICommonDao<PushMsg,Integer>{

    List<PushMsg> findByParameter(PushMsgVO pushMsgVO);

    /**
     * 查找当天没有推送的消息
     * @return
     */
    List<PushMsg> findNotPushMsgByTouchTime(@Param(value = "beginTime") Date beginTime,@Param(value = "endTime")  Date endTime);

}
