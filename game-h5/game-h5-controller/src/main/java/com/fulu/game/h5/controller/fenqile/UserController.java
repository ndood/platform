package com.fulu.game.h5.controller.fenqile;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.UserCommentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户Controller
 *
 * @author Gong ZeChun
 * @date 2018/8/13 17:15
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/user")
public class UserController extends BaseController {
    private final UserCommentService commentService;

    @Autowired
    public UserController(UserCommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 查询陪玩师的所有评论
     *
     * @param pageNum  页码
     * @param pageSize 每页显示数据条数
     * @param serverId 陪玩师id
     * @return 封装结果集
     */
    @RequestMapping(value = "/comment/byserver")
    public Result findDetailsComments(Integer pageNum,
                                      Integer pageSize,
                                      Integer serverId) {
        PageInfo<UserCommentVO> page = commentService.findByServerId(pageNum, pageSize, serverId);
        return Result.success().data(page);
    }
}
