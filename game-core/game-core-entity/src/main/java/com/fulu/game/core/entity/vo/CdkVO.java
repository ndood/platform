package com.fulu.game.core.entity.vo;

import com.fulu.game.common.enums.OrderStatusEnum;
import com.fulu.game.core.entity.Cdk;
import lombok.Data;

/**
 * cdk记录表
 *
 * @author yanbiao
 * @date 2018-06-13 15:33:53
 */
@Data
public class CdkVO extends Cdk {

    private Integer status;
    private String statusName;//订单状态中文名

    public String getStatusName() {
        return OrderStatusEnum.getMsgByStatus(status);
    }

}
