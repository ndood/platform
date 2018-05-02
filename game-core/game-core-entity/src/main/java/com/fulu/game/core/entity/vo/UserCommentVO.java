package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.UserComment;
import lombok.Data;

/**
 * 用户(打手)星级评论表
 * @author yanbiao
 * @date 2018-04-29 13:26:30
 */
@Data
public class UserCommentVO  extends UserComment {

    //是否匿名评价，默认为否
    private Boolean recordUser = true;
}
