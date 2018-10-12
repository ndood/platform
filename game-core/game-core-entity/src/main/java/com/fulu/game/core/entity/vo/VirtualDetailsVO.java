package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.VirtualDetails;
import lombok.Data;

import java.util.Date;


/**
 * 虚拟币和魅力值详情流水表
 *
 * @author Gong Zechun
 * @date 2018-08-30 15:26:37
 */
@Data
public class VirtualDetailsVO extends VirtualDetails {

    private Date startTime;
    
}
