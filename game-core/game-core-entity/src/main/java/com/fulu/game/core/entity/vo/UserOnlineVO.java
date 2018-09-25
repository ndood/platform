package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.AdminImLog;
import com.fulu.game.core.entity.VirtualDetails;
import lombok.Data;

import java.util.List;


/**
 * 虚拟币和魅力值详情流水表
 *
 * @author jaycee.Deng
 * @date 2018-08-18 15:26:37
 */
@Data
public class UserOnlineVO {
    
    private Boolean needSayHello;
    
    private Boolean openAgentIm;
    
    private List<AdminImLog> imLogList;
    
}
