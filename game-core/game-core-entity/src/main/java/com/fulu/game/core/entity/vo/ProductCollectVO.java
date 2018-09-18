package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Category;
import lombok.Data;

import java.util.List;

/**
 * 首页商品VO
 *
 * @author Gong ZeChun
 * @date 2018/9/17 19:36
 */
@Data
public class ProductCollectVO extends Category {
    /**
     * 商品列表
     */
    private List<ProductShowCaseVO> voList;
}
