package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.ImUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PicGroupVO {

    private String name;
    private Integer price;
    private Integer virtualProductId;
    private List<String> urls = new ArrayList<>();
}
