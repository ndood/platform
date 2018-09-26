package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.PriceRule;
import lombok.Data;


/**
 * 定价规则表
 *
 * @author shijiaoyun
 * @date 2018-09-12 15:43:00
 */
@Data
public class PriceRuleVO  extends PriceRule {

    private Boolean usable;

    private String msg;

}
