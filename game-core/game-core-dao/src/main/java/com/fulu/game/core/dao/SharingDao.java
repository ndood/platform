package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.vo.SharingVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 分享文案表
 *
 * @author yanbiao
 * @date 2018-05-14 11:46:29
 */
@Mapper
public interface SharingDao extends ICommonDao<Sharing, Integer> {

    List<Sharing> findByParameter(SharingVO sharingVO);

}
