package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Product;
import lombok.Data;

@Data
public class SimpleProductVO extends Product {

    private UserInfoVO userInfo = new UserInfoVO();

}
