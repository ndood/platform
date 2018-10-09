package com.fulu.game.core.entity.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fulu.game.core.entity.MoneyDetails;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 零钱流水表
 *
 * @author yanbiao
 * @date 2018-04-25 14:59:54
 */
@Data
public class MoneyDetailsVO extends MoneyDetails {

    @Excel(name = "用户联系方式", orderNum = "3", width = 25)
    private String mobile;
    @Excel(name = "用户昵称", orderNum = "4", width = 30)
    private String nickname;
    private Integer cashStatus;
    private String cashStatusMsg;

    private Date startTime;
    private Date endTime;
    private List<Integer> actions;
}
