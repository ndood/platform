package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Admin;
import lombok.Data;


/**
 * 系统管理员表
 *
 * @author yanbiao
 * @date 2018-04-24 10:20:44
 */
@Data
public class AdminVO  extends Admin {
    private String orderBy = "create_time DESC";
    private String token;
}
