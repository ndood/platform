package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Product;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;


/**
 * 商品表
 *
 * @author yanbiao
 * @date 2018-04-24 15:23:43
 */
@Data
public class ProductVO  extends Product {

    private Integer hour;

    private Date startTime;

}
