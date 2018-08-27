package com.fulu.game.core.dao;

import com.fulu.game.core.entity.OfficialNotice;
import com.fulu.game.core.entity.vo.OfficialNoticeVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 官方公告表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-27 18:09:53
 */
@Mapper
public interface OfficialNoticeDao extends ICommonDao<OfficialNotice, Integer> {

    List<OfficialNotice> findByParameter(OfficialNoticeVO officialNoticeVO);

}
