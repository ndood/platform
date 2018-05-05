package com.fulu.game.play.controller;

import com.fulu.game.common.Result;
import com.fulu.game.core.entity.UserComment;
import com.fulu.game.core.entity.vo.UserCommentVO;
import com.fulu.game.core.service.UserCommentService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户(打手)星级评价表
 * @author yanbiao
 * @date 2018-04-29 13:26:30
 */

@RestController
@RequestMapping("/api/v1/user/comment")
@Slf4j
public class UserCommentController extends BaseController{

    @Autowired
    private UserCommentService commentService;

    /**
     * 用户-添加评价
     * @return
     */
    @RequestMapping("/save")
    public Result save(UserCommentVO commentVO){
        commentService.save(commentVO);
        return Result.success().msg("添加成功！");
    }

    /**
     * 用户-查询评论
     * @return
     */
    @RequestMapping("/get")
    public Result get(@RequestParam("orderNo") String orderNo){
        UserComment comment = commentService.findByOrderNo(orderNo);
        if (null == comment){
            return Result.error().msg("该评论不存在！");
        }
        comment.setServerUserId(null);
        comment.setScoreAvg(null);
        return Result.success().data(comment).msg("查询成功！");
    }



    /**
     * 查询陪玩师的所有评论
     * @param pageNum
     * @param pageSize
     * @param serverId
     * @return
     */
    @RequestMapping(value = "/byserver")
    public Result findDetailsComments(Integer pageNum,
                                      Integer pageSize,
                                      Integer serverId){
        PageInfo<UserCommentVO> page = commentService.findByServerId(pageNum,pageSize,serverId);
        return Result.success().data(page);
    }



}
