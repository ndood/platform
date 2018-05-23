package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Product;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;


/**
 * 商品表
 *
 * @author yanbiao
 * @date 2018-04-24 15:23:43
 */
@Data
@ToString
public class ProductVO  extends Product {

    private Boolean onLine;

    private Float hour;

    private Date startTime;

    private List<String> techTags;


}
