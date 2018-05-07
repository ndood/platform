package com.fulu.game.core.entity.vo.responseVO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author
 * 2018.4.29
 * 管理员订单列表查询结果VO
 */
@Data
public class OrderResVO {
    private Integer id;//订单id
    private String orderNo;//订单编号
    private Integer status;//订单状态
    private Integer userId;//玩家id
    private String userNickname;//玩家昵称
    private String userMobile;//玩家手机号
    private String remark;//陪玩要求
    private String productName;//技能名称
    private BigDecimal price;//单价
    private String unit;//单位
    private Integer amount;//商品个数
    private BigDecimal totalMoney;//总额
    private BigDecimal commissionMoney;//佣金
    private Integer serverUserId;//打手id
    private String serverNickname;//打手昵称
    private String serverMobile;//打手手机号
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;
}
