package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.User;
import lombok.Data;


/**
 * 用户表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
@Data
public class UserVO  extends User {
    private String orderBy = "create_time DESC";

}
