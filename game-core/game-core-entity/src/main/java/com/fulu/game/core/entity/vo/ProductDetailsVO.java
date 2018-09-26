package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.UserCommentTag;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProductDetailsVO {

    //商品ID
    private Integer id;
    //商品icon
    private String categoryIcon;
    //分类ID
    private Integer categoryId;
    //关联技能
    private Integer techAuthId;
    //商品名称
    private String productName;
    //是否在线
    private Boolean onLine;
    //商品段位信息
    private String dan;

    //价格
    private BigDecimal price;
    //单位
    private String unit;
    //技能描述
    private String description;
    //订单数
    private int orderCount;
    //技能认证提交图标
    private String gradePicUrl;

    //是否已关注（1：已关注；0：未关注）
    private Integer isAttention;

    //技能标签
    private List<String> techTags;

    private UserInfoVO userInfo = new UserInfoVO();

    private List<ProductVO> otherProduct = new ArrayList<ProductVO>();

    /** 用户评论标签 */
    private List<UserCommentTag> userCommentTagList;

}
