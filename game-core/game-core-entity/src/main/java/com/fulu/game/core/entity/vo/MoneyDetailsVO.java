package com.fulu.game.core.entity.vo;

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

    private String mobile;
    private String nickname;
    private Integer cashStatus;
    private String cashStatusMsg;

    private Date startTime;
    private Date endTime;
    private List<Integer> actions;
}
