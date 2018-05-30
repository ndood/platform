package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Banner;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * banner管理表
 *
 * @author yanbiao
 * @date 2018-05-25 12:15:22
 */
@Data
public class BannerVO extends Banner {
    //排序id
    @NotNull(message = "排序id不能为空")
    @Range(min = 1, max = 2147483647, message = "排序id值超过范围")
    private Integer sort;
    //图片url
    @NotNull(message = "图片地址不能为空")
    private String picUrl;
    //跳转类型
    @NotNull(message = "跳转类型不能为空")
    @Range(min = 1, max = 5, message = "跳转类型填写1-10的数字")
    private Integer redirectType;
    //跳转地址
    private String redirectUrl;
}
