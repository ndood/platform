package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Product;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SimpleProductVO extends Product {

    private UserInfoVO userInfo = new UserInfoVO();

    private List<Product> otherProducts = new ArrayList<>();
}
