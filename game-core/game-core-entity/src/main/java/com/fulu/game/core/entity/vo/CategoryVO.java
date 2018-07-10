package com.fulu.game.core.entity.vo;

import com.fulu.game.core.entity.Category;
import com.fulu.game.core.entity.SalesMode;
import com.fulu.game.core.entity.Tag;
import com.fulu.game.core.entity.TechValue;
import lombok.Data;

import java.util.List;


/**
 * 分类表
 * 
 * @author wangbin
 * @date 2018-04-18 15:55:39
 */
@Data
public class CategoryVO extends Category {

    private List<SalesMode>  salesModeList;

    private List<TechValue> danList;

    private List<TechValue> areaList;

    private List<Tag> tagList;
    //最多标签数
    private Integer most;

    private List<TagVO> groupTags;
}
