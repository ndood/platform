package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Tag;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


/**
 * 标签表
 *
 * @author wangbin
 * @date 2018-04-18 16:29:26
 */
@Data
public class TagVO  extends Tag {

    private List<TagVO> sonTags = new ArrayList<>();

    private boolean selected;


}
