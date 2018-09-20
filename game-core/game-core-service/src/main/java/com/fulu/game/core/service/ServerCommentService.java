package com.fulu.game.core.service;

import com.fulu.game.core.entity.ServerComment;
import com.fulu.game.core.entity.vo.ServerCommentVO;


/**
 * 陪玩师评价用户表
 * 
 * @author shijiaoyun
 * @email ${email}
 * @date 2018-09-20 11:26:51
 */
public interface ServerCommentService extends ICommonService<ServerComment,Integer>{

    /**
     * 保存评论
     * @param serverCommentVO
     */
    void save(ServerCommentVO serverCommentVO);

    /**
     * 查询评价信息
     * @param orderNo
     * @return
     */
    ServerCommentVO findByOrderNo(String orderNo);
}
