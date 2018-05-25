package com.fulu.game.core.service;

import com.fulu.game.core.entity.Approve;

/**
 * 好友认可记录表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:34
 */
public interface ApproveService extends ICommonService<Approve, Integer> {

    /**
     * 好友能力认可-新增记录
     *
     * @param techAuthId
     * @return
     */
    Approve save(Integer techAuthId);

}
