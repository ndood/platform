package com.fulu.game.core.service;

import com.fulu.game.core.entity.Sharing;
import com.fulu.game.core.entity.vo.SharingVO;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 分享文案表
 *
 * @author yanbiao
 * @date 2018-05-14 11:46:29
 */
public interface SharingService extends ICommonService<Sharing, Integer> {

    PageInfo<Sharing> list(Integer pageNum, Integer pageSize);

    List<Sharing> findByParam(SharingVO sharingVO);
}
