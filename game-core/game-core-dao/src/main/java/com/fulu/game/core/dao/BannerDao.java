package com.fulu.game.core.dao;

import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.vo.BannerVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * banner管理表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:22
 */
@Mapper
public interface BannerDao extends ICommonDao<Banner, Integer> {

    List<Banner> findByParameter(BannerVO bannerVO);

}
