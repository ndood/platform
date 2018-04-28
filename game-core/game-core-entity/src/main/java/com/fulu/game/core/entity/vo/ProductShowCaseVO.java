package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductShowCaseVO {

    private Integer id;
    //用户商品
    private Integer userId;

    private String description;

    private Integer gender;
    //游戏ID
    private Integer categoryId;
    //游戏名称
    private String productName;
    //价格
    private BigDecimal price;
    //单位类型
    private String unit;

    private Boolean status;

    private String mainPhoto;

    private String nickName;

    private Date createTime;

    private List<String> personTags;

    private String city;



}
