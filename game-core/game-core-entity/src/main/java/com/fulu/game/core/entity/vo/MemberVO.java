package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Member;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 系统管理员表
 * 
 * @author wangbin
 * @date 2018-04-18 15:38:16
 */
@Data
public class MemberVO extends Member  {

    private String orderBy = "create_time DESC";
}
