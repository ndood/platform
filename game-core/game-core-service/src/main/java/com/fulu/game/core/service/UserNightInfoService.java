package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserNightInfo;
import com.fulu.game.core.entity.vo.UserNightInfoVO;
import com.github.pagehelper.PageInfo;


/**
 * 午夜场陪玩师信息表
 *
 * @author Gong Zechun
 * @email ${email}
 * @date 2018-09-17 15:32:26
 */
public interface UserNightInfoService extends ICommonService<UserNightInfo, Integer> {

    PageInfo<UserNightInfo> list(Integer pageNum, Integer pageSize);

    void remove(Integer id);

    UserNightInfoVO getNightConfig(Integer userId);

    UserNightInfo setNightConfig(Integer userId, Integer sort, Integer categoryId, Integer type);
}
