package com.fulu.game.core.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 虚拟币和魅力值详情流水表
 *
 * @author Gong Zechun
 * @date 2018-08-30 19:38:52
 */
@Data
public class VirtualDetails implements Serializable {
    private static final long serialVersionUID = 1L;

    //主键id
    private Integer id;
    //用户id
    private Integer userId;
    //关联编号
    private String relevantNo;
    //剩余虚拟币或者魅力值余额
    private Integer sum;
    //虚拟币或魅力值的增加和消费记录
    private Integer money;
    //类型（1：虚拟币；2：魅力值）
    private Integer type;
    //备注
    private String remark;
    //创建时间
    private Date createTime;
}
