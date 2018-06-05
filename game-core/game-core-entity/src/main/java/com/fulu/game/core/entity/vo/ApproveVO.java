package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Approve;
import lombok.Data;

/**
 * 好友认可记录表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:34
 */
@Data
public class ApproveVO extends Approve {
    //技能状态
    private Integer techStatus;
    //有效认可次数
    private Integer approveCount;
    private Integer requireCount;
}
