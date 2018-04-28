package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Tag;

import java.math.BigDecimal;
import java.util.List;

public class PlayProductVO {

    /**
     * 陪玩师ID
     */
    private String serverId;

    /**
     * 陪玩师昵称
     */
    private String nickName;

    /**
     * 陪玩师商品ID
     */
    private Integer productId;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品价格单位
     */
    private String unit;

    /**
     * 陪玩师个人标签
     */
    private List<Tag> tags;

    /**
     * 城市
     */
    private String city;

    /**
     * 陪玩师主图
     */
    private String mainPic;

    /**
     * 是否在线
     */
    private Boolean isOnline;



}
