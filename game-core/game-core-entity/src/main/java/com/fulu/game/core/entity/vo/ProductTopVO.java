package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.ProductTop;

import javax.validation.constraints.NotNull;


/**
 * 商品置顶表
 *
 * @author wangbin
 * @date 2018-06-07 15:28:32
 */
public class ProductTopVO  extends ProductTop {

    //指定分类
    @NotNull(message = "[置顶分类]字段不能为空")
    private Integer categoryId;
    //手机号
    @NotNull(message = "[手机号]字段不能为空")
    private String mobile;
    //排序
    @NotNull(message = "[排序ID]字段不能为空")
    private Integer sort;

}