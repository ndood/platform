package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.UserTechAuth;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class TechAuthProductVO extends UserTechAuth {

    private String categoryName;
    private String categoryIcon;

    List<ModelPrice> modelPriceList = new ArrayList<>();

    public void addModelPrice(ModelPrice modelPrice){
        this.modelPriceList.add(modelPrice);
    }

    @Data
    public static class ModelPrice{

        private Integer productId;
        private BigDecimal price;
        private Integer unitId;
        private String unitName;

    }

}
