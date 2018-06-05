package com.fulu.game.core.service;

import com.fulu.game.core.entity.Banner;
import com.fulu.game.core.entity.vo.BannerVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * banner管理表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:22
 */
public interface BannerService extends ICommonService<Banner, Integer> {
    Banner save(BannerVO bannerVO);

    Banner update(BannerVO bannerVO);

    Banner disable(Integer id,Boolean disable);

    void delete(Integer id);

    PageInfo<Banner> list(Integer pageNum, Integer pageSize);

    List<Banner> findByParam(BannerVO bannerVO);

}
