package com.fulu.game.core.entity.vo;


import com.fulu.game.core.entity.Tag;
import lombok.Data;

import java.util.List;


/**
 * 标签表
 *
 * @author wangbin
 * @date 2018-04-18 16:29:26
 */
@Data
public class TagVO  extends Tag {

    private List<Tag> sonTags;

    private boolean selected;


}
