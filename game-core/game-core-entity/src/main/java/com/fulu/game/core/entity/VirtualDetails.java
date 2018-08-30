package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 虚拟货币流水表
 *
 * @author Gong Zechun
 * @date 2018-08-30 15:26:37
 */
@Data
public class VirtualDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //用户id
    private Integer userId;
    //剩余货币
    private Integer sum;
    //消费或者充值金额
    private Integer money;
    //备注
    private String remark;
    //创建时间
    private Date createTime;

}
