package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Approve;
import com.fulu.game.core.entity.vo.ApproveVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 好友认可记录表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:34
 */
@Mapper
public interface ApproveDao extends ICommonDao<Approve, Integer> {

    List<Approve> findByParameter(ApproveVO approveVO);

}
