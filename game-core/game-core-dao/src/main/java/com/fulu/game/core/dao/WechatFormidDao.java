package com.fulu.game.core.dao;

import com.fulu.game.core.entity.WechatFormid;
import com.fulu.game.core.entity.vo.WechatFormidVO;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 
 * @author wangbin
 * @email ${email}
 * @date 2018-05-11 10:35:21
 */
@Mapper
public interface WechatFormidDao extends ICommonDao<WechatFormid,Integer>{

    List<WechatFormid> findByParameter(WechatFormidVO wechatFormidVO);

    List<WechatFormidVO> findByUserIds(@Param(value = "userIds") List<Integer> userIds);

    void deleteByExpireTime(@Param(value = "expireTime") Date expireTime);
}
