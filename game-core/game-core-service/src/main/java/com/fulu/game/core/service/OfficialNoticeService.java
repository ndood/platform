package com.fulu.game.core.service;

import com.fulu.game.core.entity.OfficialNotice;
import com.github.pagehelper.PageInfo;


/**
 * 官方公告表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-08-27 18:09:53
 */
public interface OfficialNoticeService extends ICommonService<OfficialNotice, Integer> {

    PageInfo<OfficialNotice> list(Integer pageNum, Integer pageSize);

    void add(OfficialNotice officialNotice);
}
