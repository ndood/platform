package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ProductShowCaseVO {
    //商品ID
    private Integer id;
    //用户商品
    private Integer userId;

    private Integer techAuthId;

    private String description;

    private Integer gender;
    //游戏ID
    private Integer categoryId;
    //商品id
    private Integer productId;
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
    //是否在线
    private Boolean onLine;

    private List<String> personTags;

    //段位
    private String dan;

    private String city;

    //周销售量
    private Integer orderCount;

    /**  用户年龄 */
    private Integer age;
}
