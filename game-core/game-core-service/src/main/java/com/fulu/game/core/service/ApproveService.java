package com.fulu.game.core.service;

import com.fulu.game.core.entity.Approve;
import com.fulu.game.core.entity.vo.ApproveVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 好友认可记录表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:34
 */
@Transactional
public interface ApproveService extends ICommonService<Approve, Integer> {

    /**
     * 好友能力认可-新增记录
     *
     * @param techAuthId
     * @return
     */
    ApproveVO save(Integer techAuthId);

    List<Approve> findByParam(ApproveVO approveVO);
}
