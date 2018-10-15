package com.fulu.game.core.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fulu.game.core.entity.Product;
import com.fulu.game.core.entity.User;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class SimpleUserInfoVO extends User{

    /**
     * 用户简介
     */
    private String about;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String salt;

    @JsonIgnore
    private String imPsw;

    @JsonIgnore
    private String idcard;

    @JsonIgnore
    private BigDecimal balance;
    //充值零钱（不可提现）
    @JsonIgnore
    private BigDecimal chargeBalance;
    //虚拟零钱（对应钻石数量）
    @JsonIgnore
    private Long virtualBalance;

    //注册ip
    @JsonIgnore
    private String registIp;
    //登录ip
    @JsonIgnore
    private String loginIp;

    @JsonIgnore
    private String mobile;

    @JsonIgnore
    private Date loginTime;

    @JsonIgnore
    private Date createTime;

    //修改时间
    @JsonIgnore
    private Date updateTime;
    /**
     * 用户接单节能
     */
    private List<Product> userProducts;

}
