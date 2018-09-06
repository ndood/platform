package com.fulu.game.core.entity.vo;


import cn.afterturn.easypoi.excel.annotation.Excel;
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
public class UserBodyAuthVO extends UserBodyAuth {
    @Excel(name = "申请人昵称", orderNum = "3", width = 15)
    private String nickName;

    @Excel(name = "申请人手机号", orderNum = "5", width = 15)
    private String mobile;

    private Date startTime;

    private Date endTime;
}
