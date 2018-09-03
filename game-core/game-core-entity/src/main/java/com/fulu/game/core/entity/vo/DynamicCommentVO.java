package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.DynamicComment;
import lombok.Data;


/**
 * 动态评论表
 *
 * @author shijiaoyun
 * @date 2018-08-30 11:21:42
 */
@Data
public class DynamicCommentVO  extends DynamicComment {

    /** 被回复内容 */
    private String toContent;
}
