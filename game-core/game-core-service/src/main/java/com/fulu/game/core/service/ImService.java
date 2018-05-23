package com.fulu.game.core.service;

import com.fulu.game.core.entity.vo.ImUserVo;

public interface ImService {

    /**
     * 调用环信接口的token
     *
     * @return
     */
    String getToken();

    /**
     * 批量获取IMUser
     *
     * @return
     */
    ImUserVo getUsers(Integer limit, String cursor);
}
