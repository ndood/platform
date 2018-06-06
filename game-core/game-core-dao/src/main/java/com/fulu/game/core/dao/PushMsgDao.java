package com.fulu.game.core.dao;

import com.fulu.game.core.entity.PushMsg;
import com.fulu.game.core.entity.vo.PushMsgVO;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-06-06 10:29:12
 */
@Mapper
public interface PushMsgDao extends ICommonDao<PushMsg,Integer>{

    List<PushMsg> findByParameter(PushMsgVO pushMsgVO);

}
