package com.fulu.game.core.service;

import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户(打手)星级评论表
 *
 * @author yanbiao
 * @date 2018-04-29 13:19:26
 */
@Transactional
public interface UserCommentService extends ICommonService<UserComment, Integer> {

    /**
     * 新增评价
     *
     * @param commentVO
     */
    void save(UserCommentVO commentVO);

    UserComment findByOrderNo(String orderNo);

    /**
     * 查询陪玩师的所有评论
     *
     * @return
     */
    PageInfo<UserCommentVO> findByServerId(int pageNum, int pageSize, Integer serverUserId);
}
