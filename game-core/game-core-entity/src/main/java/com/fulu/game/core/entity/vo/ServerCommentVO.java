package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.ServerComment;
import lombok.Data;


/**
 * 陪玩师评价用户表
 *
 * @author shijiaoyun
 * @date 2018-09-20 11:26:51
 */
@Data
public class ServerCommentVO  extends ServerComment {
    private String nickname;
    //性别(0不公开,1男,2女)
    private Integer gender;
    private Integer age;
    //头像URL
    private String headPortraitsUrl;
    private String imId;
}
