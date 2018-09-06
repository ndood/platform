package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.UserBodyAuth;
import lombok.Data;

import java.util.Date;


/**
 * 用户身份认证信息表
 *
 * @author jaycee.Deng
 * @date 2018-09-05 17:40:37
 */
@Data
public class UserBodyAuthVO  extends UserBodyAuth {

    private String nickName;
    
    private String mobile;

    private Date startTime;

    private Date endTime;
}
