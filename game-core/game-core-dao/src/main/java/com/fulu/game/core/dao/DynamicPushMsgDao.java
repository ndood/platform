package com.fulu.game.core.dao;

import com.fulu.game.core.entity.DynamicPushMsg;
import com.fulu.game.core.entity.vo.DynamicPushMsgVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 动态Push消息推送表
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-11 10:24:20
 */
@Mapper
public interface DynamicPushMsgDao extends ICommonDao<DynamicPushMsg,Integer>{

    List<DynamicPushMsg> findByParameter(DynamicPushMsgVO dynamicPushMsgVO);

}
