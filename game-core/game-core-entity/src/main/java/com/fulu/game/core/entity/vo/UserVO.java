package com.fulu.game.core.entity.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import com.fulu.game.core.entity.UserInfoAuthFile;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户表
 *
 * @author wangbin
 * @date 2018-04-20 11:12:12
 */
@Data
public class UserVO extends User {
    /**
     * 用户id
     */
    private Integer userId;

    //推送时间间隔
    private Float pushTimeInterval;

    private Date startTime;

    private Date endTime;
    //注册来源名
    @Excel(name = "注册来源", orderNum = "5", width = 15)
    private String sourceName;

    /**
     * 消费金额
     */
    @Excel(name = "消费金额", orderNum = "6", width = 15)
    private BigDecimal paySum;
    @Excel(name = "下单数", orderNum = "8", width = 15)
    private Integer orderCount;

    /**
     * 优惠券发放状态： 0：发放失败（默认）；1：发放成功
     */
    private Integer coupouStatus;

    @JsonIgnore
    private String orderBy;

    //未读消息数
    private long unreadCount;

    /**
     * 用户兴趣
     */
    private String interests;
    /**
     * 用户职业
     */
    private String profession;
    /**
     * 用户简介
     */
    private String about;
    /**
     * 用户相册图片集
     */
    private String[] picUrls;
    /**
     * 用户视频地址
     */
    private String videoUrl;
    /**
     * 用户接单节能
     */
    private List<Product> userProducts;

    /**
     * 用户最新动态列表
     */
    private List<DynamicVO> newestDynamics;
    /**
     * 是否已关注（1：是；0：否）
     */
    private int isAttention = 0;
    /** 关注用户数 */
    private int attentions = 0;
    /** 粉丝数 */
    private int fans = 0;
    /** 用户月收入 */
    private BigDecimal monthIncome;
    // 来访次数
    private Integer accessCount;

    /**
     * 收入金额
     */
    @Excel(name = "收入金额", orderNum = "9", width = 15)
    private BigDecimal incomeSum;
    /**
     * 服务订单数
     */
    @Excel(name = "服务订单数", orderNum = "11", width = 15)
    private Integer serviceOrderCount;
    /**
     * 消费客单价
     */
    @Excel(name = "消费客单价", orderNum = "7", width = 15)
    private BigDecimal payUnitPrice;
    /**
     * 收入客单价
     */
    @Excel(name = "收入客单价", orderNum = "10", width = 15)
    private BigDecimal incomeUnitPrice;
    //标签组
    private List<TagVO> groupTags;
    //声音文件
    private List<UserInfoAuthFile> voiceList;
    //用户封面图
    private String mainPicUrl;

    /**
     * 语音地址
     */
    private String voiceUrl;
    /**
     * 语音时长
     */
    private Integer duration;

    /**
     * 技能数量（审核通过的）
     */
    @Excel(name = "技能数量", orderNum = "19", replace = {"0_null"}, width = 15)
    private Integer techNum;

    /**
     * 是否午夜场陪玩师（false：否；true：是）
     */
    @Excel(name = "是否午夜场陪玩师", orderNum = "17", replace = {"否_false", "是_true", "否_null"}, width = 15)
    private Boolean nigthFlag;

    /**
     * 是否马甲（false：否；true：是）
     */
    @Excel(name = "是否马甲", orderNum = "16", replace = {"否_false", "是_true", "否_null"}, width = 15)
    private Boolean vestFlag;

    /**
     * 是否开启代聊  0关闭  1开启
     */
    @Excel(name = "是否代聊", orderNum = "15", replace = {"否_false", "是_true", "否_null"}, width = 15)
    private Boolean openSubstituteIm;

    /**
     * 陪玩师注册时间
     */
    @Excel(name = "陪玩师注册时间", exportFormat = "yyyy-MM-dd HH:mm:ss", orderNum = "14", width = 20)
    private Date registerTime;

    @Excel(name = "钱包余额", orderNum = "12", width = 15)
    private String balanceStr;

    public String getBalanceStr() {
        return getBalance()
                + "/"
                + (getChargeBalance() == null
                ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_DOWN)
                : getChargeBalance().setScale(2, BigDecimal.ROUND_HALF_DOWN));
    }

    public BigDecimal getPayUnitPrice() {
        return payUnitPrice == null
                ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_DOWN)
                : payUnitPrice.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }

    public BigDecimal getIncomeUnitPrice() {
        return incomeUnitPrice == null
                ? BigDecimal.ZERO.setScale(2, BigDecimal.ROUND_HALF_DOWN)
                : incomeUnitPrice.setScale(2, BigDecimal.ROUND_HALF_DOWN);
    }
}
