package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.VirtualProductAttach;
import lombok.Data;


/**
 * 虚拟商品附件表
 *
 * @author Gong Zechun
 * @date 2018-08-30 15:05:30
 */
@Data
public class VirtualProductAttachVO extends VirtualProductAttach {

    private String orderby;
    private String name;
    private Integer price;
    private Integer type;
    private Integer sort;
    private Integer accachCount;
    private String objectUrl;
    private boolean delFlag;
}
