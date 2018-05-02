package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.MoneyDetails;
import lombok.Data;

/**
 * 零钱流水表
 *
 * @author yanbiao
 * @date 2018-04-25 14:59:54
 */
@Data
public class MoneyDetailsVO  extends MoneyDetails {
    private String orderBy;
    private String mobile;
    private String nickname;
    private Integer cashStatus;
}
