package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.*;
import lombok.Data;

import java.util.List;


/**
 * 分类表
 * 
 * @author wangbin
 * @date 2018-04-18 15:55:39
 */
@Data
public class CategoryVO extends Category {

    private List<SalesMode>  salesModeList;

    private List<TechValue> danList;

    private List<TechValue> areaList;

    private List<Tag> tagList;
    //最多标签数
    private Integer most;

    private List<TagVO> groupTags;

    // 分类对应定价规则表
    private List<PriceRule> priceRuleList;

    // 认证状态（0：未认证；1：审核中；2：被拒绝）
    private Integer authStatus;
    // 认证状态对应描述（0：未认证；1：审核中；2：被拒绝）
    private String authStatusStr;
    // 被拒绝原因
    private String reason;
    // 子分类集合
    private List<CategoryVO> childCategoryList;
}
