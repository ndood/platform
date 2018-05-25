package com.fulu.game.core.search.domain;

import io.searchbox.annotations.JestId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductShowCaseDoc {

    @JestId
    //商品ID
    private Integer id;
    //陪玩师ID
    private Integer userId;
    //商品描述
    private String description;
    //性别
    private Integer gender;
    //游戏ID
    private Integer categoryId;
    //游戏名称
    private String productName;
    //价格
    private BigDecimal price;
    //单位类型
    private String unit;
    //商品上架状态
    private Boolean status;
    //商品主图
    private String mainPhoto;
    //用户昵称
    private String nickName;
    //创建时间
    private Date createTime;
    //是否在首页展示
    private Boolean isIndexShow;
    //是否在线
    private Boolean onLine;
    //用户个人标签
    private List<String> personTags;
    //用户所在地
    private String city;

    private Integer orderCount;


}
