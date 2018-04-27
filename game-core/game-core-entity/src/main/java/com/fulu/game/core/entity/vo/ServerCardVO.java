package com.fulu.game.core.entity.vo;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ServerCardVO {

    //商品ID
    private Integer productId;
    //商品icon
    private String icon;
    //分类ID
    private Integer categoryId;
    //关联技能
    private Integer techAuthId;
    //商品名称
    private String productName;

    //价格
    private BigDecimal price;

    //单位
    private String unit;

    //技能描述
    private String description;
    //评分
    private int star;
    //订单数
    private int orderCount;
    //技能标签
    private List<String> techTags;

    private UserInfo userInfo = new UserInfo();

    private List<OtherProduct> otherProduct = new ArrayList<OtherProduct>();

    @Data
    public static class UserInfo{
        private String userId;
        private String realName;
        private String headUrl;
        private int age;
        private String city;
        private String voice;
        List<String> tags;
        //陪玩师照片集
        private List<String> photos;
    }

    @Data
    public static class OtherProduct{
        private String icon;
        private String productName;
        private Integer techAuthId;
        private List<String> techTags;
        private BigDecimal price;
        private String unit;
    }

}
