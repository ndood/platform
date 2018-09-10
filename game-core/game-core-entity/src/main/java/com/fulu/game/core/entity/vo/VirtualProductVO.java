package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.VirtualProduct;
import lombok.Data;


/**
 * 虚拟商品表
 *
 * @author Gong Zechun
 * @date 2018-08-30 10:01:57
 */
@Data
public class VirtualProductVO extends VirtualProduct {

    private Integer userId;
    
    private Integer buyStatus;
    
    private Integer targetUserId;
    
    private Integer fromUserId;
}
