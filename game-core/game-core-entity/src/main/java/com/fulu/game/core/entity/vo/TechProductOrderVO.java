package com.fulu.game.core.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class TechProductOrderVO {


    private Integer productId;

    private Integer categoryId;

    private String serverHeadUrl;

    private String serverNickname;

    private String productName;

    private BigDecimal price;

    private String unit;


    private List<OtherProduct> otherProductList = new ArrayList<>();

    @Data
    public static class OtherProduct{
        private Integer productId;
        private String productName;
        private Integer categoryId;
        private BigDecimal price;
        private String unit;
    }

}
